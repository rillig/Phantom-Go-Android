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
        val unit = min(width, height) / (boardSize + 1)
        val size = unit * (boardSize + 1)

        val dx = ((width - size) / 2).toFloat()
        val dy = ((height - size) / 2).toFloat()

        fun screenToBoard(sc: Double) = (sc * (boardSize + 1) / size - 1).roundToInt()
        val x = screenToBoard(e.x.toDouble() - dx)
        val y = screenToBoard(e.y.toDouble() - dy)

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

        val board = fillPaint(0xFFD48E00)
        val black = fillPaint(0xFF000000)
        val white = fillPaint(0xFFFFFFFF)
        val softBlack = fillPaint(0x55000000)
        val softWhite = fillPaint(0x55FFFFFF)

        var unit = 0
        val line = linePaint(0xFF000000)
        val currentLine = linePaint(0xFFFF9900)

        fun draw(g: Canvas) {
            g.save()

            unit = min(width, height) / (boardSize + 1)
            val size = unit * (boardSize + 1)

            drawBackground(g)

            g.translate(((width - size) / 2).toFloat(), ((height - size) / 2).toFloat())

            drawLines(g)
            drawStones(g)

            g.restore()
        }

        private fun drawBackground(g: Canvas) {
            g.drawRect(RectF(0.0F, 0.0F, width.toFloat(), height.toFloat()), board)
        }

        private fun drawLines(g: Canvas) {
            val lineDistance = boardToScreen(1) - boardToScreen(0)
            val lineWidth = floor(lineDistance / 20.0F).coerceAtLeast(1.0F)

            line.strokeWidth = lineWidth
            currentLine.strokeWidth = lineWidth

            val crossX = cross.x
            val crossY = cross.y
            val highlightCross =
                highlightCross && crossX in 0 until boardSize && crossY in 0 until boardSize

            for (i in 0 until boardSize) {
                val start = boardToScreen(0) - lineWidth / 2.0F
                val end = boardToScreen(boardSize - 1) + lineWidth / 2.0F
                val fixed = boardToScreen(i)

                if (!(highlightCross && i == crossY)) {
                    g.drawLine(start, fixed, end, fixed, line)
                }
                if (!(highlightCross && i == crossX)) {
                    g.drawLine(fixed, start, fixed, end, line)
                }
            }

            drawHoshis(g)

            if (highlightCross) {
                val startY = boardToScreen(0) + lineWidth / 2.0F
                val endY = boardToScreen(boardSize - 1) - lineWidth / 2.0F
                val screenX = boardToScreen(crossX)
                g.drawLine(screenX, startY, screenX, endY, currentLine)

                val startX = boardToScreen(0) + lineWidth / 2.0F
                val endX = boardToScreen(boardSize - 1) - lineWidth / 2.0F
                val screenY = boardToScreen(crossY)
                g.drawLine(startX, screenY, endX, screenY, currentLine)
            }
        }

        /** [https://senseis.xmp.net/?Hoshi] */
        private fun drawHoshis(g: Canvas) {
            fun drawHoshi(x: Int, y: Int) = fillCircle(g, x, y, 0.1F, black)

            val small = if (boardSize < 13) 2 else 3
            val center = boardSize / 2
            val large = boardSize - 1 - small

            if (boardSize % 2 != 0) drawHoshi(center, center)
            if (boardSize >= 9) {
                drawHoshi(small, small)
                drawHoshi(large, small)
                drawHoshi(small, large)
                drawHoshi(large, large)
            }
            if (boardSize >= 19) {
                drawHoshi(center, small)
                drawHoshi(small, center)
                drawHoshi(large, center)
                drawHoshi(center, large)
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
                    val paint = if (cell.color == Player.BLACK) softBlack else softWhite
                    fillCircle(g, x, y, 0.48F, paint)
                }
                if (cell.territory != null) {
                    val paint = if (cell.territory == Player.BLACK) black else white
                    fillCircle(g, x, y, 0.16F, paint)
                }
            } else if (cell.color != null) {
                val paint = if (cell.color == Player.BLACK) black else white
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

        private fun boardToScreen(bc: Int) = (unit * (bc + 1)).toFloat()

        private fun boardToScreenF(bc: Float) = unit * (bc + 1)

        private fun fillPaint(argb: Long) = Paint().apply {
            color = argb.toInt()
            isAntiAlias = true
        }

        private fun linePaint(argb: Long) = Paint().apply {
            color = argb.toInt()
            style = Paint.Style.STROKE
        }
    }
}
