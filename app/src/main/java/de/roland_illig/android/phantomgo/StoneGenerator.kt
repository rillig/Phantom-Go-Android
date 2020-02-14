package de.roland_illig.android.phantomgo

import android.graphics.Bitmap
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * The stripes on the stones are formed by looking at an ellipsoid 3-D stone from the top.
 *
 * To produce a striped stone, a [bivalve shell](https://en.wikipedia.org/wiki/Bivalve_shell)
 * of infinite size is thought to have grown constantly over time, producing differently
 * colored material at each time, which then leads to the stripes.
 *
 * The [stoneThickness] is relative to the stone's radius: 0.0 is a flat stone,
 * 1.0 is spherical.
 *
 * The brightness of the [backgroundColor] and [stripeColor] pixel of the stripes is given
 * in a range from 0 to 255.
 *
 * The color of a pixel depends only on its distance from the
 * [plane with normal vector](https://en.wikipedia.org/wiki/Hesse_normal_form)
 * ([dirX], [dirY], [dirZ]).
 *
 * Each stripe has the same brightness gradient, consisting of three sections.
 * Their relative lengths are given in [up], [down], [pause].
 * At the beginning, the brightness is [stripeColor].
 * Then, for [up], it changes smoothly to [backgroundColor].
 * Then, for [down], it changes smoothly back to [stripeColor].
 * Then, for [pause], it stays [stripeColor].
 *
 * This shape is then scaled by [stripesPerRadius] and shifted by [phaseShift].
 */
data class StoneParams(
    val stoneThickness: Double,
    val backgroundColor: Int,
    val lightColor: Int,
    val lightPower: Double,
    val lightX: Double,
    val lightY: Double,
    val lightZ: Double,
    val stripeColor: Int,
    val dirX: Double,
    val dirY: Double,
    val dirZ: Double,
    val stripesPerRadius: Double,
    val up: Double,
    val down: Double,
    val pause: Double,
    val phaseShift: Double
)

object StoneGenerator {

    fun drawWhiteStone(img: Bitmap, params: StoneParams) {
        val psize = img.width
        for (py in 0 until psize) {
            val y = linear(2 * py + 1, 1, 2 * psize - 1, -1.0, 1.0)
            for (px in 0 until psize) {
                val x = linear(2 * px + 1, 1, 2 * psize - 1, -1.0, 1.0)
                val alpha = alpha(px, py, psize)
                val brightness = stripeBrightness(x, y, params) + lightBrightness(x, y, params)
                if (brightness != -1) {
                    img.setPixel(
                        px,
                        py,
                        0x010101 * sat(0, brightness, 255) + 0x01000000 * sat(0, alpha, 255)
                    )
                }
            }
        }
    }

    private fun alpha(px: Int, py: Int, psize: Int): Int {

        // Scale all coordinates by 2 to stay in integer arithmetic.
        val center = 2 * psize / 2
        val x = 2 * px - center
        val y = 2 * py - center
        val r = center - 1
        val alphaWidth = 2 * 2
        val ir = r - alphaWidth

        val hypot2 = x * x + y * y
        return when {
            hypot2 > r * r -> 0
            hypot2 < ir * ir -> 255
            else -> 255 - (255 * (sqrt(hypot2.toDouble()) - ir) / (r - ir)).roundToInt()
        }
    }

    interface BrightnessSpy {
        fun spy(
            params: StoneParams,
            x: Double, y: Double, z: Double,
            orthoLength: Double,
            distance: Double, scaledDistance: Double, phaseFraction: Double,
            brightness: Int
        )
    }

    private fun stripeBrightness(
        x: Double,
        y: Double,
        params: StoneParams,
        spy: BrightnessSpy? = null
    ): Int {
        val sqrtArg = 1.0 - x * x - y * y
        if (sqrtArg < 0.0) {
            return -1
        }

        val z = StrictMath.sqrt(sqrtArg) * params.stoneThickness
        val orthoLength = hypot3(params.dirX, params.dirY, params.dirZ)
        val distance = (x * params.dirX + y * params.dirY + z * params.dirZ) / orthoLength
        val scaledDistance = (distance * params.stripesPerRadius + params.phaseShift)
        val phaseFraction = scaledDistance - StrictMath.floor(scaledDistance)

        val phaseLength = params.up + params.down + params.pause
        val scaledUpEnd = params.up / phaseLength
        val scaledDownEnd = scaledUpEnd + params.down / phaseLength
        val brightness = when {
            phaseFraction < scaledUpEnd -> smooth(
                phaseFraction,
                0.0,
                scaledUpEnd,
                params.backgroundColor,
                params.stripeColor
            )
            phaseFraction < scaledDownEnd -> smooth(
                phaseFraction,
                scaledUpEnd,
                scaledDownEnd,
                params.stripeColor,
                params.backgroundColor
            )
            else -> params.backgroundColor
        }
        spy?.spy(params, x, y, z, orthoLength, distance, scaledDistance, phaseFraction, brightness)

        return brightness
    }

    private fun smooth(t: Double, tFrom: Double, tTo: Double, from: Int, to: Int): Int {
        val arg01 = (t - tFrom) / (tTo - tFrom)
        val argRad = (arg01 - 0.5) * Math.PI
        val rangem11 = sin(argRad)
        val range01 = rangem11 / 2.0 + 0.5
        return from + ((to - from) * range01).toInt()
    }

    private fun linear(src: Int, srcMin: Int, srcMax: Int, targetMin: Double, targetMax: Double) =
        sat(
            targetMin,
            targetMin + (targetMax - targetMin) * (src - srcMin) / (srcMax - srcMin),
            targetMax
        )

    private fun sat(min: Int, x: Int, max: Int): Int =
        if (x < min) min else if (x > max) max else x

    private fun sat(min: Double, x: Double, max: Double): Double =
        if (x < min) min else if (x > max) max else x

    private fun hypot3(dx: Double, dy: Double, dz: Double) =
        StrictMath.sqrt(dx * dx + dy * dy + dz * dz)

    private fun lightBrightness(x: Double, y: Double, params: StoneParams) = 0
}