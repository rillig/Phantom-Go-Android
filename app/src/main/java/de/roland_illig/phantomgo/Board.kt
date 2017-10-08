package de.roland_illig.phantomgo

import android.support.annotation.VisibleForTesting
import java.util.Arrays
import java.util.HashSet

class Board(val size: Int) : java.io.Serializable {

    private val pieces = Array(size) { arrayOfNulls<Player>(size) }
    private val captured = IntArray(2)
    var turn: Player = Player.BLACK
    private var prevMove: Point = Point(-1, -1)
    private var passed: Boolean = false
    var gameOver: Boolean = false; private set
    var empty: Boolean = false; private set

    init {
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

    private fun getLiberties(x: Int, y: Int, color: Player)
            = if (x in 0 until size && y in 0 until size && get(x, y) == color) getLiberties(x, y) else -1

    private fun captureCount(x: Int, y: Int, turn: Player): Int {
        if (x in 0 until size && y in 0 until size) {
            if (getLiberties(x, y, turn) == 0) {
                return capture(x, y, turn)
            }
        }
        return 0
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

        val selfLeftBefore = getLiberties(x - 1, y, turn) == 1
        val selfAboveBefore = getLiberties(x, y - 1, turn) == 1
        val selfRightBefore = getLiberties(x + 1, y, turn) == 1
        val selfBelowBefore = getLiberties(x, y + 1, turn) == 1
        val selfAtariBefore = selfLeftBefore || selfAboveBefore || selfRightBefore || selfBelowBefore

        val leftBefore = getLiberties(x - 1, y, other) == 1
        val aboveBefore = getLiberties(x, y - 1, other) == 1
        val rightBefore = getLiberties(x + 1, y, other) == 1
        val belowBefore = getLiberties(x, y + 1, other) == 1
        val capturesSomething = leftBefore || aboveBefore || rightBefore || belowBefore

        val dx = x - prevMove.x
        val dy = y - prevMove.y
        if (dx * dx + dy * dy == 1) {
            if (getLiberties(prevMove.x, prevMove.y, other) == 1) {
                return RefereeResult.ko()
            }
        }

        pieces[x][y] = turn
        var undo = true
        try {
            if (!capturesSomething && getLiberties(x, y) == 0) {
                return RefereeResult.suicide()
            }

            val left = getLiberties(x - 1, y, other) == 1
            val above = getLiberties(x, y - 1, other) == 1
            val right = getLiberties(x + 1, y, other) == 1
            val below = getLiberties(x, y + 1, other) == 1

            val atari = (!leftBefore && left
                    || !aboveBefore && above
                    || !rightBefore && right
                    || !belowBefore && below)

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

    fun getLiberties(x: Int, y: Int): Int {
        val color = get(x, y)!!
        val todo: MutableSet<Point> = HashSet()
        val done: MutableSet<Point> = HashSet()
        val liberties: MutableSet<Point> = HashSet()

        fun countInternal(nx: Int, ny: Int) {
            if (nx in 0 until size && ny in 0 until size) {
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

        todo.add(Point(x, y))

        while (!todo.isEmpty()) {
            val it = todo.iterator()
            val point = it.next()
            it.remove()
            done.add(point)

            val px = point.x
            val py = point.y
            countInternal(px - 1, py)
            countInternal(px + 1, py)
            countInternal(px, py - 1)
            countInternal(px, py + 1)
        }

        return liberties.size
    }

    internal data class Point(val x: Int, val y: Int) : java.io.Serializable
}
