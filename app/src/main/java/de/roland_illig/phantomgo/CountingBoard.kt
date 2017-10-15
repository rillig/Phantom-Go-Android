package de.roland_illig.phantomgo

import android.support.annotation.VisibleForTesting

class CountingBoard(board: Board) : java.io.Serializable {
    private val size = board.size
    private val color: Array<Array<Player?>>
    private val region: Array<IntArray>
    private val dead: Array<BooleanArray>
    private val territory: Array<Array<Player?>>
    private var countResult: CountResult? = null
    private val blackCaptured: Int
    private val whiteCaptured: Int

    init {
        this.color = initPieces(board)
        this.region = initRegion(color)
        this.dead = Array(size) { BooleanArray(size) }
        this.territory = Array(size) { arrayOfNulls<Player?>(size) }
        this.blackCaptured = board.getCaptured(Player.BLACK)
        this.whiteCaptured = board.getCaptured(Player.WHITE)
    }

    fun toggleDead(x: Int, y: Int) {
        if (color[x][y] == null) {
            throw IllegalArgumentException("$x,$y")
        }

        val toToggle = Array(size) { BooleanArray(size) }
        floodFillStep(color, toToggle, Array(size) { BooleanArray(size) }, color[x][y], x, y)
        floodFillStep(color, toToggle, Array(size) { BooleanArray(size) }, null, x, y)
        floodFillStep(color, toToggle, Array(size) { BooleanArray(size) }, color[x][y], x, y)

        for (j in 0 until size) {
            for (i in 0 until size) {
                if (toToggle[i][j] && color[i][j] != null) {
                    dead[i][j] = !dead[i][j]
                }
            }
        }

        countResult = null
    }

    private fun initPieces(board: Board): Array<Array<Player?>> {
        val size = board.size
        val pieces = Array(size) { arrayOfNulls<Player?>(size) }
        for (y in 0 until size) {
            for (x in 0 until size) {
                pieces[x][y] = board[x, y]
            }
        }
        return pieces
    }

    private fun initRegion(pieces: Array<Array<Player?>>): Array<IntArray> {
        val size = pieces.size
        val chain = Array(size) { IntArray(size) }
        var id = 1
        for (y in 0 until size) {
            for (x in 0 until size) {
                if (chain[x][y] == 0) {
                    floodFill(pieces, chain, pieces[x][y], id, x, y)
                    id++
                }
            }
        }
        return chain
    }

    private fun floodFill(input: Array<Array<Player?>>, output: Array<IntArray>, from: Player?, to: Int, x: Int, y: Int) {
        if (x in 0 until output.size && y in 0 until output.size) {
            if (output[x][y] == 0 && input[x][y] == from) {
                output[x][y] = to
                floodFill(input, output, from, to, x - 1, y)
                floodFill(input, output, from, to, x + 1, y)
                floodFill(input, output, from, to, x, y - 1)
                floodFill(input, output, from, to, x, y + 1)
            }
        }
    }

    private fun floodFillStep(input: Array<Array<Player?>>, output: Array<BooleanArray>, done: Array<BooleanArray>, from: Player?, x: Int, y: Int) {
        if (x in 0 until output.size && y in 0 until output.size) {
            if (!done[x][y] && (output[x][y] || input[x][y] == from)) {
                done[x][y] = true
                output[x][y] = true
                floodFillStep(input, output, done, from, x - 1, y)
                floodFillStep(input, output, done, from, x + 1, y)
                floodFillStep(input, output, done, from, x, y - 1)
                floodFillStep(input, output, done, from, x, y + 1)
            }
        }
    }

    @VisibleForTesting
    fun regionsToString(): String {
        val alphabet = "0123456789" + "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "abcdefghijklmnopqrstuvwxyz"
        val sb = StringBuilder()
        for (y in 0 until size) {
            for (x in 0 until size) {
                sb.append(alphabet[region[x][y] % alphabet.length])
                sb.append(if (x == size - 1) "\n" else " ")
            }
        }
        return sb.toString()
    }

    fun count(): CountResult {
        if (countResult != null) {
            return countResult!!
        }

        var blackDead = 0
        var whiteDead = 0

        for (y in 0 until size) {
            for (x in 0 until size) {
                if (dead[x][y]) {
                    if (color[x][y] == Player.BLACK) {
                        blackDead++
                    } else {
                        whiteDead++
                    }
                }
            }
        }

        val black = Array(size) { BooleanArray(size) }
        val white = Array(size) { BooleanArray(size) }

        for (y in 0 until size) {
            for (x in 0 until size) {
                if (!dead[x][y]) {
                    if (this.color[x][y] == Player.BLACK) {
                        black[x][y] = true
                    }
                    if (this.color[x][y] == Player.WHITE) {
                        white[x][y] = true
                    }
                }
            }
        }

        do {
            var changed = false
            for (y in 0 until size) {
                for (x in 0 until size) {
                    if (color[x][y] == null || dead[x][y]) {
                        if (!black[x][y] && hasNeighbor(x, y, black)) {
                            black[x][y] = true
                            changed = true
                        }
                        if (!white[x][y] && hasNeighbor(x, y, white)) {
                            white[x][y] = true
                            changed = true
                        }
                    }
                }
            }
        } while (changed)

        var blackTerritory = 0
        var whiteTerritory = 0
        for (y in 0 until size) {
            for (x in 0 until size) {
                territory[x][y] = null
                if (color[x][y] == null || dead[x][y]) {
                    if (black[x][y] && !white[x][y]) {
                        territory[x][y] = Player.BLACK
                        blackTerritory++
                    }
                    if (white[x][y] && !black[x][y]) {
                        territory[x][y] = Player.WHITE
                        whiteTerritory++
                    }
                }
            }
        }

        countResult = CountResult(
                blackTerritory, whiteTerritory,
                blackCaptured + whiteDead, whiteCaptured + blackDead)
        return countResult!!
    }

    private fun hasNeighbor(x: Int, y: Int, color: Array<BooleanArray>): Boolean {
        return (x > 0 && color[x - 1][y]
                || y > 0 && color[x][y - 1]
                || x + 1 < size && color[x + 1][y]
                || y + 1 < size && color[x][y + 1])
    }

    fun isDead(x: Int, y: Int): Boolean = dead[x][y]

    fun getTerritory(x: Int, y: Int): Player? {
        count()
        return territory[x][y]
    }

    override fun toString(): String {
        count()
        val sb = StringBuilder()
        for (y in 0 until size) {
            for (x in 0 until size) {
                when (territory[x][y]) {
                    Player.WHITE -> sb.append(if (dead[x][y]) '#' else 'w')
                    Player.BLACK -> sb.append(if (dead[x][y]) '#' else 'b')
                    else -> sb.append(if (color[x][y] != null) "BW"[color[x][y]!!.ordinal] else '.')
                }
                sb.append(if (x == size - 1) "\n" else " ")
            }
        }
        return sb.toString()
    }
}
