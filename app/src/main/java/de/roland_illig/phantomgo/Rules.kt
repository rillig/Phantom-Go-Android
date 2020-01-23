package de.roland_illig.phantomgo

data class Rules(
    /** See [toroidal go](https://senseis.xmp.net/?ToroidalGo). */
    var toroidal: Boolean
) {
    constructor() : this(false)
}
