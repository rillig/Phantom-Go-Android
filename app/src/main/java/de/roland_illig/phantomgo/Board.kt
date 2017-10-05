package de.roland_illig.phantomgo

import android.support.annotation.VisibleForTesting
import java.util.*

class Board(val size: Int) : java.io.Serializable {

    private val pieces = Array(size) { arrayOfNulls<Player>(size) }
    private val captured = IntArray(2)
    var turn: Player = Player.BLACK
    private var prevMove: Point? = null
    private var passed: Boolean = false
    var gameOver: Boolean = false
        private set
    var empty: Boolean = false
        private set

    init {
        reset()
    }

    fun reset() {
        for (row in pieces) {
            Arrays.fill(row, null)
        }
        Arrays.fill(captured, 0)
        turn = Player.BLACK
        prevMove = Point(-1, -1)
        passed = false
        gameOver = false
        empty = true
    }

    fun copy(): Board {
        val copy = Board(size)
        for (x in 0 until size) {
            System.arraycopy(pieces[x], 0, copy.pieces[x], 0, size)
        }
        System.arraycopy(captured, 0, copy.captured, 0, captured.size)
        copy.turn = turn
        copy.prevMove = prevMove
        copy.passed = passed
        copy.gameOver = gameOver
        copy.empty = empty
        return copy
    }

    operator fun get(x: Int, y: Int) = pieces[x][y]

    fun getCaptured(player: Player) = captured[player.ordinal]

    private fun getLiberties(x: Int, y: Int, color: Player?) = if (get(x, y) == color) getLiberties(x, y) else -1

    private fun captureCount(x: Int, y: Int, turn: Player): Int {
        if (x in 0 until size && y in 0 until size) {
            if (getLiberties(x, y, turn) == 0) {
                return capture(x, y, turn)
            }
        }
        return 0
    }

    @VisibleForTesting
    fun getLiberties(x: Int, y: Int): Int {
        val counter = LibertiesCounter(get(x, y)!!)
        counter.count(x, y)
        return counter.liberties.size
    }

    private fun capture(x: Int, y: Int, color: Player): Int {
        var captured = 1
        pieces[x][y] = null
        if (x > 0 && pieces[x - 1][y] == color)
            captured += capture(x - 1, y, color)
        if (x < size - 1 && pieces[x + 1][y] == color)
            captured += capture(x + 1, y, color)
        if (y > 0 && pieces[x][y - 1] == color)
            captured += capture(x, y - 1, color)
        if (y < size - 1 && pieces[x][y + 1] == color)
            captured += capture(x, y + 1, color)
        return captured
    }

    operator fun set(x: Int, y: Int, color: Player?) {
        pieces[x][y] = color
    }

    fun play(x: Int, y: Int): RefereeResult {
        if (gameOver) {
            throw IllegalStateException("GameOver")
        }
        val other = turn.other()

        if (get(x, y) == turn) {
            return RefereeResult.ownStone()
        }
        if (get(x, y) == other) {
            return RefereeResult.otherStone()
        }

        val selfLeftBefore = x > 0 && getLiberties(x - 1, y, turn) == 1
        val selfAboveBefore = y > 0 && getLiberties(x, y - 1, turn) == 1
        val selfRightBefore = x + 1 < size && getLiberties(x + 1, y, turn) == 1
        val selfBelowBefore = y + 1 < size && getLiberties(x, y + 1, turn) == 1
        val selfAtariBefore = selfLeftBefore || selfAboveBefore || selfRightBefore || selfBelowBefore

        val leftBefore = if (x > 0) getLiberties(x - 1, y, other) else 0
        val aboveBefore = if (y > 0) getLiberties(x, y - 1, other) else 0
        val rightBefore = if (x + 1 < size) getLiberties(x + 1, y, other) else 0
        val belowBefore = if (y + 1 < size) getLiberties(x, y + 1, other) else 0

        val dx = x - prevMove!!.x
        val dy = y - prevMove!!.y
        if (dx * dx + dy * dy == 1) {
            if (getLiberties(prevMove!!.x, prevMove!!.y, other) == 1) {
                return RefereeResult.ko()
            }
        }

        pieces[x][y] = turn
        var undo = true
        try {
            val capturesSomething = leftBefore == 1 || aboveBefore == 1 || rightBefore == 1 || belowBefore == 1
            if (!capturesSomething && getLiberties(x, y) == 0) {
                return RefereeResult.suicide()
            }

            val left = if (x > 0) getLiberties(x - 1, y, other) else 0
            val above = if (y > 0) getLiberties(x, y - 1, other) else 0
            val right = if (x + 1 < size) getLiberties(x + 1, y, other) else 0
            val below = if (y + 1 < size) getLiberties(x, y + 1, other) else 0

            val atari = (leftBefore > 1 && left == 1
                    || aboveBefore > 1 && above == 1
                    || rightBefore > 1 && right == 1
                    || belowBefore > 1 && below == 1)

            val capturedStones = (captureCount(x - 1, y, other)
                    + captureCount(x, y - 1, other)
                    + captureCount(x + 1, y, other)
                    + captureCount(x, y + 1, other))

            val selfAtari = getLiberties(x, y, turn) == 1 && !selfAtariBefore

            captured[turn.ordinal] += capturedStones
            prevMove = if (capturedStones == 1 && selfAtari) Point(x, y) else Point(-1, -1)
            turn = turn.other()
            undo = false
            empty = false

            return RefereeResult.ok(atari, selfAtari, capturedStones)
        } finally {
            if (undo) {
                pieces[x][y] = null
            }
        }
    }

    fun pass(): RefereeResult {
        if (gameOver) {
            throw IllegalStateException("GameOver")
        }
        empty = false
        gameOver = passed
        passed = true
        turn = turn.other()
        return RefereeResult.pass()
    }

    @VisibleForTesting
    fun setup(vararg rows: String) {
        for (y in 0 until size) {
            for (x in 0 until size) {
                val ch = rows[y][2 * x]
                pieces[x][y] = parseChar(ch)
            }
        }
    }

    private fun parseChar(ch: Char): Player? {
        if (ch == 'W') return Player.WHITE
        if (ch == 'B') return Player.BLACK
        if (ch == '.') return null
        throw IllegalArgumentException(ch.toString())
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (y in 0 until size) {
            for (x in 0 until size) {
                val player = get(x, y)
                sb.append(if (player != null) "BW"[player.ordinal] else '.')
                sb.append(if (x == size - 1) "\n" else " ")
            }
        }
        return sb.toString()
    }

    private inner class LibertiesCounter internal constructor(internal val color: Player) {
        internal val todo: MutableSet<Point> = HashSet()
        internal val done: MutableSet<Point> = HashSet()
        internal val liberties: MutableSet<Point> = HashSet()

        internal fun count(x: Int, y: Int) {
            checkNotNull(color)

            todo.add(Point(x, y))

            while (!todo.isEmpty()) {
                val it = todo.iterator()
                val point = it.next()
                it.remove()
                done.add(point)

                val px = point.x
                val py = point.y
                if (px > 0) { // TODO: == floodfill?
                    countInternal(px - 1, py)
                }
                if (py > 0) {
                    countInternal(px, py - 1)
                }
                if (px < size - 1) {
                    countInternal(px + 1, py)
                }
                if (py < size - 1) {
                    countInternal(px, py + 1)
                }
            }
        }

        private fun countInternal(nx: Int, ny: Int) {
            val np = Point(nx, ny)
            val neighbor = get(nx, ny)
            if (neighbor == color && !done.contains(np)) {
                todo.add(np)
            }
            if (neighbor == null) {
                liberties.add(np)
            }
        }
    }

    companion object {
        private val serialVersionUID = 20171005L
    }

    internal data class Point(val x: Int, val y: Int) : java.io.Serializable {
        companion object {
            private val serialVersionUID = 20171005L
        }
    }
}
