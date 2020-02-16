package de.roland_illig.phantomgo

fun Board.setup(vararg rows: String) {
    fun parseChar(ch: Char) = when (ch) {
        'O' -> Player.WHITE
        'X' -> Player.BLACK
        '.' -> null
        else -> throw IllegalArgumentException("$ch")
    }

    for (y in 0 until size) {
        for (x in 0 until size) {
            val ch = rows[y][2 * x]
            this[x, y] = parseChar(ch)
        }
    }
}
