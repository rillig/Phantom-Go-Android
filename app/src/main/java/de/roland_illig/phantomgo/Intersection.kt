package de.roland_illig.phantomgo

data class Intersection(
    val x: Int,
    val y: Int
) : java.io.Serializable {
    override fun toString() = "${"ABCDEFGHIJKLMNOPQRSTUVWXYZ"[x]}${y + 1}"
}
