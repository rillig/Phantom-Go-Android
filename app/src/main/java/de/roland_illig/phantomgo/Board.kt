package de.roland_illig.phantomgo

open class Board(val size: Int) : java.io.Serializable {

    private val nowhere = Intersection(-1, -1)

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

    private fun captureCount(x: Int, y: Int, turn: Player, captured: MutableList<Intersection>) {
        if (x in 0 until size && y in 0 until size) {
            if (get(x, y) == turn && getLiberties(x, y) == 0) {

                fun capture(cx: Int, cy: Int) {
                    if (cx in 0 until size && cy in 0 until size && pieces[cx][cy] == turn) {
                        pieces[cx][cy] = null
                        captured += Intersection(cx, cy)
                        for (n in neighbors(cx, cy)) {
                            capture(n.x, n.y)
                        }
                    }
                }

                capture(x, y)
            }
        }
    }

    operator fun set(x: Int, y: Int, color: Player?) {
        pieces[x][y] = color
    }

    fun play(x: Int, y: Int): RefereeResult {
        check(!gameOver) { "GameOver" }

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

        val selfAtariBefore = atari(x - 1, y, turn)
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

            val atari = !leftBefore && atari(x - 1, y, other)
                    || !rightBefore && atari(x + 1, y, other)
                    || !aboveBefore && atari(x, y - 1, other)
                    || !belowBefore && atari(x, y + 1, other)

            val capturedStones = mutableListOf<Intersection>()
            for (n in neighbors(x, y)) {
                captureCount(n.x, n.y, other, capturedStones)
            }

            val selfAtari = atari(x, y, turn) && !selfAtariBefore

            captured[turn.ordinal] += capturedStones.size
            prevMove = if (capturedStones.size == 1 && selfAtari) Intersection(x, y) else nowhere
            this.turn = other
            undo = false
            empty = false

            return RefereeResult.Ok(atari, selfAtari, capturedStones.toList())
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
            set(x, y, newColor)
        }
    }

    override fun toString(): String {
        return toStringLines().joinToString("") { it + "\n" }
    }

    fun toStringLines(): List<String> {
        val lines = mutableListOf<String>()
        val sb = StringBuilder()
        for (y in 0 until size) {
            for (x in 0 until size) {
                val player = get(x, y)
                sb.append(if (player != null) "XO"[player.ordinal] else '+')
                if (x < size - 1) sb.append(" ") else {
                    lines += sb.toString()
                    sb.setLength(0)
                }
            }
        }
        return lines
    }

    fun getLiberties(x: Int, y: Int): Int {
        val color = get(x, y)!!
        val todo = mutableSetOf<Intersection>()
        val done = mutableSetOf<Intersection>()
        val liberties = mutableSetOf<Intersection>()

        fun countInternal(np: Intersection) {
            val neighbor = get(np.x, np.y)
            if (neighbor == color && !done.contains(np)) {
                todo.add(np)
            }
            if (neighbor == null) {
                liberties.add(np)
            }
        }

        todo.add(Intersection(x, y))

        while (todo.isNotEmpty()) {
            val it = todo.iterator()
            val point = it.next()
            it.remove()
            done.add(point)

            for (n in neighbors(point.x, point.y)) {
                countInternal(n)
            }
        }

        return liberties.size
    }

    private fun neighbors(x: Int, y: Int): List<Intersection> {
        val neighbors = mutableListOf<Intersection>()
        if (y > 0) neighbors += Intersection(x, y - 1)
        if (x > 0) neighbors += Intersection(x - 1, y)
        if (x < size - 1) neighbors += Intersection(x + 1, y)
        if (y < size - 1) neighbors += Intersection(x, y + 1)
        return neighbors
    }
}
