package de.roland_illig.phantomgo

data class Intersection(
        val x: Int,
        val y: Int) {
    override fun toString(): String {
        return "" + "ABCDEFGHIJKLMNOPQRSTUVWXYZ"[x] + (y + 1)
    }
}
