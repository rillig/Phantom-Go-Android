package de.roland_illig.phantomgo

class CountingBoard(board: Board) : java.io.Serializable {
    val size = board.size
    private val color: Array<Array<Player?>>
    private val dead: Array<BooleanArray>
    private val territory: Array<Array<Player?>>
    private var countResult: CountResult? = null
    private val blackCaptured: Int
    private val whiteCaptured: Int

    init {
        this.color = initPieces(board)
        this.dead = Array(size) { BooleanArray(size) }
        this.territory = Array(size) { Array<Player?>(size) { null } }
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
                pieces[x][y] = board[Intersection(x, y)]
            }
        }
        return pieces
    }

    private fun floodFillStep(input: Array<Array<Player?>>, output: Array<BooleanArray>, done: Array<BooleanArray>, from: Player?, x: Int, y: Int) {
        if (x in output.indices && y in output.indices) {
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
                sb.append(when  {
                    dead[x][y] -> '#'
                    territory[x][y] != null -> "xo"[territory[x][y]!!.ordinal]
                    color[x][y] != null -> "XO"[color[x][y]!!.ordinal]
                    else -> '+'
                })
                sb.append(if (x == size - 1) "\n" else " ")
            }
        }
        return sb.toString()
    }
}
