package de.roland_illig.phantomgo

enum class Player {
    BLACK, WHITE;

    fun other(): Player {
        return if (this == BLACK) WHITE else BLACK
    }
}
