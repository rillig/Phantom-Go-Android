package de.roland_illig.android.phantomgo

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import de.roland_illig.phantomgo.Player
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Handles drawing of a Go board and translates click coordinates to board coordinates.
 *
 * Subclasses must implement [boardSize], [getBoard] and [onBoardClicked].
 *
 * The [AbstractBoardView.Cell] returned by [getBoard] contains all information necessary
 * for drawing the given intersection. The actual drawing is done by this class.
 */
abstract class AbstractBoardView : View {

    // For some reason, the click event is also fired for large distances.
    // Therefore, keep track of the start and end of the click.
    private val clickStart = Point(-1, -1)
    private val clickEnd = Point(-1, -1)

    private val cross = Point(-1, -1)

    protected abstract val boardSize: Int

    protected abstract fun getBoard(x: Int, y: Int): Cell

    protected abstract val highlightCross: Boolean

    protected abstract fun onBoardClicked(x: Int, y: Int)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        setOnTouchListener { _, e ->
            val size = min(width, height)
            fun screenToBoard(sc: Double) = (sc * (boardSize + 1) / size - 1).roundToInt()
            val x = screenToBoard(e.x.toDouble())
            val y = screenToBoard(e.y.toDouble())
            when {
                e.action == MotionEvent.ACTION_DOWN -> {
                    clickStart.set(x, y)
                    cross.set(x, y)
                    if (highlightCross) {
                        invalidate()
                    }
                }
                e.action == MotionEvent.ACTION_UP -> {
                    clickEnd.set(x, y)
                    cross.set(-1, -1)
                    if (highlightCross) {
                        invalidate()
                    }
                }
                Point(x, y) != cross -> {
                    cross.set(x, y)
                    if (highlightCross) {
                        invalidate()
                    }
                }
            }
            false
        }
        setOnClickListener {
            if (clickEnd.x in 0 until boardSize && clickEnd.y in 0 until boardSize && clickEnd == clickStart) {
                onBoardClicked(clickEnd.x, clickEnd.y)
            }
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(g: Canvas) {
        super.onDraw(g)

        g.save()

        val boardSize = boardSize
        val screenSize = min(width, height).toFloat()

        fun boardToScreen(bc: Float) = (screenSize * (bc + 1) / (boardSize + 1)).roundToInt().toFloat()
        val lineDistance = (boardToScreen(1.0F) - boardToScreen(0.0F))

        val lineWidth = floor(lineDistance / 20.0F).coerceAtLeast(1.0F)

        fun linePaint(color: Long) = Paint().also { it.color = color.toInt(); it.strokeWidth = lineWidth }
        fun fillPaint(color: Long) = Paint().also { it.color = color.toInt(); it.isAntiAlias = true }

        val linePaint = linePaint(0xFF000000)
        val currentLinePaint = linePaint(0xFFFF9900)
        val boardPaint = fillPaint(0xFFD48E00)
        val blackPaint = fillPaint(0xFF000000)
        val whitePaint = fillPaint(0xFFFFFFFF)
        val blackTranslucentPaint = fillPaint(0x55000000)
        val whiteTranslucentPaint = fillPaint(0x55FFFFFF)

        g.drawRect(RectF(0.0F, 0.0F, width.toFloat(), height.toFloat()), boardPaint)

        g.translate(
            ((width - screenSize.toInt()) / 2).toFloat(),
            ((height - screenSize.toInt()) / 2).toFloat()
        )

        val crossX = cross.x
        val crossY = cross.y
        val highlightCross = highlightCross && crossX in 0 until boardSize && crossY in 0 until boardSize

        for (i in (0 until boardSize)) {
            val start = boardToScreen(0.0F) - lineWidth / 2.0F
            val end = boardToScreen((boardSize - 1).toFloat()) + lineWidth / 2.0F
            val fixed = boardToScreen(i.toFloat())
            if (!(highlightCross && i == crossY)) {
                g.drawLine(start, fixed, end, fixed, linePaint)
            }
            if (!(highlightCross && i == crossX)) {
                g.drawLine(fixed, start, fixed, end, linePaint)
            }
        }

        if (highlightCross) {
            val startY = boardToScreen(0.0F) + lineWidth / 2.0F
            val endY = boardToScreen((boardSize - 1).toFloat()) - lineWidth / 2.0F
            val screenX = boardToScreen(crossX.toFloat())
            g.drawLine(screenX, startY, screenX, endY, currentLinePaint)

            val startX = boardToScreen(0.0F) + lineWidth / 2.0F
            val endX = boardToScreen((boardSize - 1).toFloat()) - lineWidth / 2.0F
            val screenY = boardToScreen(crossY.toFloat())
            g.drawLine(startX, screenY, endX, screenY, currentLinePaint)
        }

        fun fillCircle(g: Canvas, x: Int, y: Int, radius: Float, paint: Paint) {
            val top = boardToScreen(y - radius)
            val left = boardToScreen(x - radius)
            val bottom = boardToScreen(y + radius)
            val right = boardToScreen(x + radius)
            g.drawOval(RectF(left, top, right, bottom), paint)
        }

        for (y in 0 until boardSize) {
            for (x in 0 until boardSize) {

                val cell = getBoard(x, y)
                if (cell.dead || cell.territory != null) {
                    if (cell.dead) {
                        fillCircle(g, x, y, 0.48F, if (cell.color == Player.BLACK) blackTranslucentPaint else whiteTranslucentPaint)
                    }
                    if (cell.territory != null) {
                        fillCircle(g, x, y, 0.16F, if (cell.territory == Player.BLACK) blackPaint else whitePaint)
                    }
                } else if (cell.color != null) {
                    fillCircle(g, x, y, 0.48F, if (cell.color == Player.BLACK) blackPaint else whitePaint)
                }
            }
        }

        g.restore()
    }

    protected class Cell(val color: Player?, val territory: Player?, val dead: Boolean)
}
