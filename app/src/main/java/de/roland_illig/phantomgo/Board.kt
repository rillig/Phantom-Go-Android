package de.roland_illig.phantomgo

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

open class Board(val size: Int) : java.io.Serializable {

    var rules = Rules.Normal

    private val pieces = Array(size) { Array<Player?>(size) { null } }
    private val captured = IntArray(2)
    var turn = Player.BLACK
    private var koMove: Intersection? = null
    private var passed = 0
    var gameOver = false; private set
    val started get() = lastMove != null || passed > 0
    var lastMove: Intersection? = null; private set

    fun copy(): Board = ByteArrayOutputStream().let {
        ObjectOutputStream(it).writeObject(this)
        return ObjectInputStream(ByteArrayInputStream(it.toByteArray())).readObject() as Board
    }

    operator fun get(pos: Intersection) = pieces[pos.x][pos.y]

    operator fun set(pos: Intersection, color: Player?) {
        pieces[pos.x][pos.y] = color
    }

    override fun toString() = toStringLines().joinToString("") { it + "\n" }

    fun toStringLines(): List<String> {
        val lines = mutableListOf<String>()
        val sb = StringBuilder()
        for (y in 0 until size) {
            for (x in 0 until size) {
                sb.append(".XO"[1 + (pieces[x][y]?.ordinal ?: -1)])
                if (x < size - 1) sb.append(" ")
            }
            lines += "$sb"
            sb.setLength(0)
        }
        return lines
    }

    fun getCaptured(player: Player) = captured[player.ordinal]

    fun edit(pos: Intersection, color: Player) {
        turn = color
        val result = play(pos)
        if (result !is RefereeResult.Invalid) return
        val newColor = if (result is RefereeResult.OwnStone) null else color
        set(pos, newColor)
    }

    operator fun set(x: Int, y: Int, color: Player?) {
        pieces[x][y] = color
    }

    fun pass(): RefereeResult {
        check(!gameOver) { "GameOver" }

        lastMove = null
        passed++
        gameOver = passed >= 2
        turn = turn.other()
        return RefereeResult.Pass
    }

    fun play(pos: Intersection): RefereeResult {
        check(!gameOver) { "GameOver" }

        val turn = turn
        val other = turn.other()

        if (this[pos] == turn) return RefereeResult.OwnStone
        if (this[pos] == other) return RefereeResult.OtherStone

        val neighbors = neighbors(pos)
        val koMove = koMove
        if (koMove != null && koMove in neighbors && isAtari(koMove, other)) {
            return RefereeResult.Ko
        }

        val prevBoard = copy()

        this[pos] = turn
        if (neighbors.none { prevBoard.isAtari(it, other) } && getLiberties(pos) == 0) {
            this[pos] = null
            return RefereeResult.Suicide
        }

        val placed = mutableListOf(pos)
        val captured = mutableListOf<Intersection>()
        moveMagnetic(pos, placed)
        val atari = captureOther(placed, other, captured, prevBoard)
        val selfAtari = captureTurn(placed, turn, captured, prevBoard)

        this.captured[turn.ordinal] += captured.size
        this.koMove = if (captured.size == 1 && selfAtari) pos else null
        this.turn = other
        lastMove = pos
        passed = 0

        return RefereeResult.Ok(atari, selfAtari, captured.toList())
    }

    private fun moveMagnetic(pos: Intersection, placed: MutableList<Intersection>) {
        if (rules != Rules.Magnetic) return

        data class Direction(val x: Int, val y: Int)

        operator fun Intersection.plus(dir: Direction) =
            Intersection(this.x + dir.x, this.y + dir.y)

        operator fun Intersection.minus(dir: Direction) =
            Intersection(this.x - dir.x, this.y - dir.y)

        fun move(dx: Int, dy: Int) {
            val dir = Direction(dx, dy)
            var from = pos + dir
            while (from.ok() && this[from] == null) from += dir
            if (!from.ok()) return

            val to = if (this[from] == turn) {
                var to = from + dir
                while (to.ok() && this[to] == null) to += dir
                to - dir
            } else {
                pos + dir
            }

            if (to == from) return
            this[to] = this[from]
            this[from] = null
            placed += to
        }

        move(0, -1)
        move(-1, 0)
        move(+1, 0)
        move(0, +1)
    }

    private fun captureOther(
        placed: List<Intersection>,
        other: Player,
        captured: MutableList<Intersection>,
        prevBoard: Board
    ): Boolean {
        var atari = false
        for (p in placed) {
            for (n in neighbors(p)) {
                maybeCapture(n, other, captured)
                if (isAtari(n, other) && !prevBoard.isAtari(n, other)) atari = true
            }
        }
        return atari
    }

    private fun captureTurn(
        placed: List<Intersection>,
        turn: Player,
        captured: MutableList<Intersection>,
        prevBoard: Board
    ): Boolean {
        var selfAtari = false
        for (p in placed) {
            maybeCapture(p, turn, captured)
            if (!isAtari(p, turn)) continue
            if (prevBoard.neighbors(p).any { prevBoard.isAtari(it, turn) }) continue
            selfAtari = true
        }
        return selfAtari
    }

    private fun isAtari(pos: Intersection, color: Player) =
        pos.ok() && get(pos) == color && getLiberties(pos) == 1

    private fun maybeCapture(
        pos: Intersection,
        color: Player,
        captured: MutableList<Intersection>
    ) {
        if (!pos.ok()) return
        if (!(get(pos) == color && getLiberties(pos) == 0)) return

        fun takeOut(pos: Intersection) {
            if (this[pos] != color) return

            this[pos] = null
            captured += pos
            for (n in neighbors(pos)) if (n.ok()) takeOut(n)
        }

        takeOut(pos)
    }

    fun getLiberties(pos: Intersection): Int {
        val color = this[pos]!!
        val todo = mutableSetOf<Intersection>()
        val done = mutableSetOf<Intersection>()
        val liberties = mutableSetOf<Intersection>()

        fun countInternal(np: Intersection) {
            val neighbor = this[np]
            if (neighbor == color && !done.contains(np)) todo.add(np)
            if (neighbor == null) liberties.add(np)
        }

        todo.add(pos)

        while (todo.isNotEmpty()) {
            val it = todo.iterator()
            val point = it.next()
            it.remove()
            done.add(point)

            for (n in neighbors(point)) countInternal(n)
        }

        return liberties.size
    }

    private fun neighbors(pos: Intersection): List<Intersection> {
        val max = size - 1
        val (x, y) = pos
        return if (rules == Rules.Toroidal) {
            listOf(
                Intersection(x, if (y > 0) y - 1 else max),
                Intersection(if (x > 0) x - 1 else max, y),
                Intersection(if (x < max) x + 1 else 0, y),
                Intersection(x, if (y < max) y + 1 else 0)
            )
        } else {
            val neighbors = mutableListOf<Intersection>()
            if (y > 0) neighbors += Intersection(x, y - 1)
            if (x > 0) neighbors += Intersection(x - 1, y)
            if (x < max) neighbors += Intersection(x + 1, y)
            if (y < max) neighbors += Intersection(x, y + 1)
            neighbors
        }
    }

    private fun Intersection.ok() = this.x in 0 until size && this.y in 0 until size
}
