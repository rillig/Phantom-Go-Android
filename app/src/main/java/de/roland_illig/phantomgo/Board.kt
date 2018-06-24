package de.roland_illig.phantomgo

import java.util.HashSet

class Board(val size: Int) : java.io.Serializable {

    private val nowhere = Point(-1, -1)

    private val pieces = Array(size) { Array<Player?>(size) { null } }
    private val captured = IntArray(2)
    var turn = Player.BLACK
    private var prevMove = nowhere
    private var passed = false
    var gameOver = false; private set
    var empty = true; private set

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

    private fun captureCount(x: Int, y: Int, turn: Player): Int {
        if (x in 0 until size && y in 0 until size) {
            if (get(x, y) == turn && getLiberties(x, y) == 0) {

                fun capture(x: Int, y: Int): Int {
                    if (x in 0 until size && y in 0 until size && pieces[x][y] == turn) {
                        pieces[x][y] = null
                        return (1
                                + capture(x - 1, y)
                                + capture(x + 1, y)
                                + capture(x, y - 1)
                                + capture(x, y + 1))
                    }
                    return 0
                }

                return capture(x, y)
            }
        }
        return 0
    }

    operator fun set(x: Int, y: Int, color: Player?) {
        pieces[x][y] = color
    }

    fun play(x: Int, y: Int): RefereeResult {
        if (gameOver) {
            throw IllegalStateException("GameOver")
        }

        val turn = turn
        val other = turn.other()

        if (get(x, y) == turn) {
            return RefereeResult.OwnStone
        }
        if (get(x, y) == other) {
            return RefereeResult.OtherStone
        }

        fun atari(x: Int, y: Int, color: Player): Boolean {
            return x in 0 until size && y in 0 until size
                    && get(x, y) == color && getLiberties(x, y) == 1
        }

        val selfAtariBefore = false
                || atari(x - 1, y, turn)
                || atari(x + 1, y, turn)
                || atari(x, y - 1, turn)
                || atari(x, y + 1, turn)

        val leftBefore = atari(x - 1, y, other)
        val rightBefore = atari(x + 1, y, other)
        val aboveBefore = atari(x, y - 1, other)
        val belowBefore = atari(x, y + 1, other)
        val capturesSomething = leftBefore || rightBefore || aboveBefore || belowBefore

        val dx = x - prevMove.x
        val dy = y - prevMove.y
        if (dx * dx + dy * dy == 1) {
            if (atari(prevMove.x, prevMove.y, other)) {
                return RefereeResult.Ko
            }
        }

        pieces[x][y] = turn
        var undo = true

        try {
            if (!capturesSomething && getLiberties(x, y) == 0) {
                return RefereeResult.Suicide
            }

            val atari = false
                    || !leftBefore && atari(x - 1, y, other)
                    || !rightBefore && atari(x + 1, y, other)
                    || !aboveBefore && atari(x, y - 1, other)
                    || !belowBefore && atari(x, y + 1, other)

            val capturedStones = (0
                    + captureCount(x - 1, y, other)
                    + captureCount(x + 1, y, other)
                    + captureCount(x, y - 1, other)
                    + captureCount(x, y + 1, other))

            val selfAtari = atari(x, y, turn) && !selfAtariBefore

            captured[turn.ordinal] += capturedStones
            prevMove = if (capturedStones == 1 && selfAtari) Point(x, y) else nowhere
            this.turn = other
            undo = false
            empty = false

            return RefereeResult.Ok(atari, selfAtari, capturedStones)
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
        return RefereeResult.Pass
    }

    fun edit(x: Int, y: Int, color: Player) {
        turn = color
        val result = play(x, y)
        if (result is RefereeResult.Invalid) {
            val newColor = if (result is RefereeResult.OwnStone) null else color
            set(x, y, newColor);
        }
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (y in 0 until size) {
            for (x in 0 until size) {
                val player = get(x, y)
                sb.append(if (player != null) "XO"[player.ordinal] else '+')
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
