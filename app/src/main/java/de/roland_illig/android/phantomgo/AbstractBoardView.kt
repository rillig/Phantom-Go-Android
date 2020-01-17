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

    private val drawer = Drawer()

    protected abstract val boardSize: Int

    protected abstract fun getBoard(x: Int, y: Int): Cell

    protected abstract val highlightCross: Boolean

    protected abstract fun onBoardClicked(x: Int, y: Int)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        setOnTouchListener { _, e -> onTouch(e); false }
        setOnClickListener { onClick() }
    }

    private fun onTouch(e: MotionEvent) {
        val size = min(width, height)
        fun screenToBoard(sc: Double) = (sc * (boardSize + 1) / size - 1).roundToInt()
        val x = screenToBoard(e.x.toDouble())
        val y = screenToBoard(e.y.toDouble())

        if (e.action == MotionEvent.ACTION_DOWN) {
            clickStart.set(x, y)
            cross.set(x, y)
            if (highlightCross) invalidate()
            return
        }

        if (e.action == MotionEvent.ACTION_UP) {
            clickEnd.set(x, y)
            cross.set(-1, -1)
            if (highlightCross) invalidate()
            return
        }

        if (Point(x, y) != cross) {
            cross.set(x, y)
            if (highlightCross) invalidate()
        }
    }

    private fun onClick() {
        if (clickEnd.x !in 0 until boardSize) return
        if (clickEnd.y !in 0 until boardSize) return
        if (clickEnd != clickStart) return
        onBoardClicked(clickEnd.x, clickEnd.y)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(g: Canvas) {
        super.onDraw(g)

        drawer.draw(g)
    }

    protected class Cell(val color: Player?, val territory: Player?, val dead: Boolean)

    private inner class Drawer {

        val boardPaint = fillPaint(0xFFD48E00)
        val blackPaint = fillPaint(0xFF000000)
        val whitePaint = fillPaint(0xFFFFFFFF)
        val blackTranslucentPaint = fillPaint(0x55000000)
        val whiteTranslucentPaint = fillPaint(0x55FFFFFF)

        var screenSize = 0.0F
        val linePaint = linePaint(0xFF000000)
        val currentLinePaint = linePaint(0xFFFF9900)

        fun draw(g: Canvas) {
            g.save()

            screenSize = min(width, height).toFloat()

            drawBackground(g)

            g.translate(
                ((width - screenSize.toInt()) / 2).toFloat(),
                ((height - screenSize.toInt()) / 2).toFloat()
            )

            drawLines(g)
            drawStones(g)

            g.restore()
        }

        private fun drawBackground(g: Canvas) {
            g.drawRect(RectF(0.0F, 0.0F, width.toFloat(), height.toFloat()), boardPaint)
        }

        private fun drawLines(g: Canvas) {
            val lineDistance = boardToScreen(1) - boardToScreen(0)
            val lineWidth = floor(lineDistance / 20.0F).coerceAtLeast(1.0F)

            linePaint.strokeWidth = lineWidth
            currentLinePaint.strokeWidth = lineWidth

            val crossX = cross.x
            val crossY = cross.y
            val highlightCross =
                highlightCross && crossX in 0 until boardSize && crossY in 0 until boardSize

            for (i in 0 until boardSize) {
                val start = boardToScreen(0) - lineWidth / 2.0F
                val end = boardToScreen(boardSize - 1) + lineWidth / 2.0F
                val fixed = boardToScreen(i)

                if (!(highlightCross && i == crossY)) {
                    g.drawLine(start, fixed, end, fixed, linePaint)
                }
                if (!(highlightCross && i == crossX)) {
                    g.drawLine(fixed, start, fixed, end, linePaint)
                }
            }

            if (highlightCross) {
                val startY = boardToScreen(0) + lineWidth / 2.0F
                val endY = boardToScreen(boardSize - 1) - lineWidth / 2.0F
                val screenX = boardToScreen(crossX)
                g.drawLine(screenX, startY, screenX, endY, currentLinePaint)

                val startX = boardToScreen(0) + lineWidth / 2.0F
                val endX = boardToScreen(boardSize - 1) - lineWidth / 2.0F
                val screenY = boardToScreen(crossY)
                g.drawLine(startX, screenY, endX, screenY, currentLinePaint)
            }
        }

        private fun drawStones(g: Canvas) {
            for (y in 0 until boardSize) {
                for (x in 0 until boardSize) {
                    drawStone(g, x, y)
                }
            }
        }

        private fun drawStone(g: Canvas, x: Int, y: Int) {
            val cell = getBoard(x, y)
            if (cell.dead || cell.territory != null) {
                if (cell.dead) {
                    val paint =
                        if (cell.color == Player.BLACK) blackTranslucentPaint else whiteTranslucentPaint
                    fillCircle(g, x, y, 0.48F, paint)
                }
                if (cell.territory != null) {
                    val paint =
                        if (cell.territory == Player.BLACK) blackPaint else whitePaint
                    fillCircle(g, x, y, 0.16F, paint)
                }
            } else if (cell.color != null) {
                val paint = if (cell.color == Player.BLACK) blackPaint else whitePaint
                fillCircle(g, x, y, 0.48F, paint)
            }
        }

        private fun fillCircle(g: Canvas, x: Int, y: Int, radius: Float, paint: Paint) {
            val top = boardToScreenF(y - radius)
            val left = boardToScreenF(x - radius)
            val bottom = boardToScreenF(y + radius)
            val right = boardToScreenF(x + radius)
            g.drawOval(RectF(left, top, right, bottom), paint)
        }

        private fun boardToScreen(bc: Int) =
            (screenSize * (bc + 1) / (boardSize + 1)).roundToInt().toFloat()

        private fun boardToScreenF(bc: Float) =
            (screenSize * (bc + 1) / (boardSize + 1)).roundToInt().toFloat()

        private fun fillPaint(argb: Long) = Paint().apply {
            color = argb.toInt()
            isAntiAlias = true
        }

        private fun linePaint(argb: Long) = Paint().apply {
            color = argb.toInt()
        }
    }
}
