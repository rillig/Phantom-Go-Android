package de.roland_illig.phantomgo

import android.support.annotation.VisibleForTesting

class CountingBoard(board: Board) {
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
            throw IllegalStateException(x.toString() + "," + y)
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

    private fun floodFill(`in`: Array<Array<Player?>>, out: Array<IntArray>, from: Player?, to: Int, x: Int, y: Int) {
        if (0 <= x && x < out.size && 0 <= y && y < out.size) {
            if (out[x][y] == 0 && `in`[x][y] == from) {
                out[x][y] = to
                floodFill(`in`, out, from, to, x - 1, y)
                floodFill(`in`, out, from, to, x + 1, y)
                floodFill(`in`, out, from, to, x, y - 1)
                floodFill(`in`, out, from, to, x, y + 1)
            }
        }
    }

    private fun floodFillStep(`in`: Array<Array<Player?>>, out: Array<BooleanArray>, done: Array<BooleanArray>, from: Player?, x: Int, y: Int) {
        if (x in 0.until(out.size) && y in 0.until(out.size)) {
            if (!done[x][y] && (out[x][y] || `in`[x][y] == from)) {
                done[x][y] = true
                out[x][y] = true
                floodFillStep(`in`, out, done, from, x - 1, y)
                floodFillStep(`in`, out, done, from, x + 1, y)
                floodFillStep(`in`, out, done, from, x, y - 1)
                floodFillStep(`in`, out, done, from, x, y + 1)
            }
        }
    }

    @VisibleForTesting
    fun regionsToString(): String {
        val alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
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

        while (true) {
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

            if (changed)
                break
        }

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

    fun isDead(x: Int, y: Int): Boolean {
        return dead[x][y]
    }

    fun getTerritory(x: Int, y: Int): Player? {
        count()
        return territory[x][y]
    }

    override fun toString(): String {
        count()
        val sb = StringBuilder()
        for (y in 0 until size) {
            for (x in 0 until size) {
                if (territory[x][y] == Player.WHITE) {
                    sb.append(if (dead[x][y]) '#' else 'w')
                } else if (territory[x][y] == Player.BLACK) {
                    sb.append(if (dead[x][y]) '#' else 'b')
                } else {
                    sb.append(if (color[x][y] != null) "BW"[color[x][y]!!.ordinal] else '.')
                }
                sb.append(if (x == size - 1) "\n" else " ")
            }
        }
        return sb.toString()
    }
}
