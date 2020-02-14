package de.roland_illig.phantomgo

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

open class Board(val size: Int) : java.io.Serializable {

    private val nowhere = Intersection(-1, -1)
    var rules = Rules.Normal

    private val pieces = Array(size) { Array<Player?>(size) { null } }
    private val captured = IntArray(2)
    var turn = Player.BLACK
    private var koMove = nowhere
    private var passed = 0
    var gameOver = false; private set
    var empty = true; private set

    fun copy(): Board = ByteArrayOutputStream().let {
        ObjectOutputStream(it).writeObject(this)
        return ObjectInputStream(ByteArrayInputStream(it.toByteArray())).readObject() as Board
    }

    operator fun get(x: Int, y: Int) = pieces[x][y]

    override fun toString() = toStringLines().joinToString("") { it + "\n" }

    fun toStringLines(): List<String> {
        val lines = mutableListOf<String>()
        val sb = StringBuilder()
        for (y in 0 until size) {
            for (x in 0 until size) {
                val player = get(x, y)
                sb.append(if (player != null) "XO"[player.ordinal] else '+')
                if (x < size - 1) sb.append(" ")
                else {
                    lines += sb.toString()
                    sb.setLength(0)
                }
            }
        }
        return lines
    }

    fun getCaptured(player: Player) = captured[player.ordinal]

    fun edit(x: Int, y: Int, color: Player) {
        turn = color
        val result = play(x, y)
        if (result is RefereeResult.Invalid) {
            val newColor = if (result is RefereeResult.OwnStone) null else color
            set(x, y, newColor)
        }
    }

    operator fun set(x: Int, y: Int, color: Player?) {
        pieces[x][y] = color
    }

    fun pass(): RefereeResult {
        check(!gameOver) { "GameOver" }

        empty = false
        passed++
        gameOver = passed >= 2
        turn = turn.other()
        return RefereeResult.Pass
    }

    fun play(x: Int, y: Int): RefereeResult {
        check(!gameOver) { "GameOver" }

        val turn = turn
        val other = turn.other()

        if (get(x, y) == turn) return RefereeResult.OwnStone
        if (get(x, y) == other) return RefereeResult.OtherStone

        val neighbors = neighbors(x, y)
        if (koMove in neighbors && atari(koMove, other)) {
            return RefereeResult.Ko
        }

        val atariBefore = neighbors.map { atari(it, other) }
        val selfAtariBefore = neighbors.any { atari(it, turn) }

        pieces[x][y] = turn
        if (atariBefore.all { !it } && getLiberties(x, y) == 0) {
            pieces[x][y] = null
            return RefereeResult.Suicide
        }

        val atari = neighbors.withIndex().any { (i, n) -> !atariBefore[i] && atari(n, other) }

        val captured = mutableListOf<Intersection>()
        for (n in neighbors) capture(n.x, n.y, other, captured)

        val selfAtari = atari(Intersection(x, y), turn) && !selfAtariBefore

        this.captured[turn.ordinal] += captured.size
        koMove = if (captured.size == 1 && selfAtari) Intersection(x, y) else nowhere
        this.turn = other
        empty = false
        passed = 0

        return RefereeResult.Ok(atari, selfAtari, captured.toList())
    }

    private fun atari(i: Intersection, color: Player): Boolean {
        return i.x in 0 until size && i.y in 0 until size
                && get(i.x, i.y) == color && getLiberties(i.x, i.y) == 1
    }

    private fun capture(x: Int, y: Int, turn: Player, captured: MutableList<Intersection>) {
        if (!(x in 0 until size && y in 0 until size)) return
        if (!(get(x, y) == turn && getLiberties(x, y) == 0)) return

        fun capture(cx: Int, cy: Int) {
            if (!(cx in 0 until size && cy in 0 until size)) return
            if (pieces[cx][cy] != turn) return

            pieces[cx][cy] = null
            captured += Intersection(cx, cy)
            for (n in neighbors(cx, cy)) capture(n.x, n.y)
        }

        capture(x, y)
    }

    fun getLiberties(x: Int, y: Int): Int {
        val color = get(x, y)!!
        val todo = mutableSetOf<Intersection>()
        val done = mutableSetOf<Intersection>()
        val liberties = mutableSetOf<Intersection>()

        fun countInternal(np: Intersection) {
            val neighbor = get(np.x, np.y)
            if (neighbor == color && !done.contains(np)) todo.add(np)
            if (neighbor == null) liberties.add(np)
        }

        todo.add(Intersection(x, y))

        while (todo.isNotEmpty()) {
            val it = todo.iterator()
            val point = it.next()
            it.remove()
            done.add(point)

            for (n in neighbors(point.x, point.y)) countInternal(n)
        }

        return liberties.size
    }

    private fun neighbors(x: Int, y: Int): List<Intersection> {
        val max = size - 1
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
}
