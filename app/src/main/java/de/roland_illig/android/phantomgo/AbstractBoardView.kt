package de.roland_illig.android.phantomgo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import de.roland_illig.phantomgo.Player

/**
 * Handles drawing of a Go board and translates click coordinates to board coordinates.
 *
 * Subclasses must implement [boardSize], [getBoard] and [onBoardClicked].
 *
 * The [AbstractBoardView.Cell] returned by [getBoard] contains all information necessary
 * for drawing the given intersection. The actual drawing is done by this class.
 */
abstract class AbstractBoardView : View {

    private var lastX = -1
    private var lastY = -1

    protected abstract val boardSize: Int

    protected abstract fun getBoard(x: Int, y: Int): Cell

    protected abstract val activeTurn: Boolean

    protected abstract fun onBoardClicked(x: Int, y: Int)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        setOnTouchListener { _, e ->
            val size = Math.min(width, height)
            fun screenToBoard(sc: Double) = Math.round(sc * (boardSize + 1) / size - 1).toInt()
            val x = screenToBoard(e.x.toDouble())
            val y = screenToBoard(e.y.toDouble())
            if (x != lastX || y != lastY) {
                lastX = x
                lastY = y
                invalidate()
            }
            false
        }
        setOnClickListener {
            if (lastX in 0 until boardSize && lastY in 0 until boardSize) {
                onBoardClicked(lastX, lastY)
            }
        }
    }

    override fun onDraw(g: Canvas) {
        super.onDraw(g)

        val boardSize = boardSize
        val screenSize = Math.min(width, height).toFloat()

        fun boardToScreen(bc: Double) = Math.round(screenSize * (bc + 1) / (boardSize + 1)).toFloat()

        val lineWidth = Math.max(1.0, Math.floor((boardToScreen(1.0) - boardToScreen(0.0)) / 20.0)).toFloat()

        fun linePaint(color: Long) = Paint().also { it.color = color.toInt(); it.strokeWidth = lineWidth }
        fun fillPaint(color: Long) = Paint().also { it.color = color.toInt(); it.isAntiAlias = true }

        val linePaint = linePaint(0xFF000000)
        val currentLinePaint = linePaint(0xFFFF9900)
        val boardPaint = fillPaint(0xFFD48E00)
        val blackPaint = fillPaint(0xFF000000)
        val whitePaint = fillPaint(0xFFFFFFFF)
        val blackTranslucentPaint = fillPaint(0x55000000)
        val whiteTranslucentPaint = fillPaint(0x55FFFFFF)

        g.drawRect(RectF(0.0F, 0.0F, screenSize, screenSize), boardPaint)

        for (i in 0 until boardSize) {
            val start = boardToScreen(0.0) - lineWidth / 2.0F
            val end = boardToScreen((boardSize - 1).toDouble()) + lineWidth / 2.0F
            val fixed = boardToScreen(i.toDouble())
            if (i != lastY || !activeTurn) {
                g.drawLine(start, fixed, end, fixed, linePaint)
            }
            if (i != lastX || !activeTurn) {
                g.drawLine(fixed, start, fixed, end, linePaint)
            }
        }

        if (lastX in 0 until boardSize) {
            val startY = boardToScreen(0.0) + lineWidth / 2.0F
            val endY = boardToScreen((boardSize - 1).toDouble()) - lineWidth / 2.0F
            val screenX = boardToScreen(lastX.toDouble())
            if (activeTurn) {
                g.drawLine(screenX, startY, screenX, endY, currentLinePaint)
            }
        }
        if (lastY in 0 until boardSize) {
            val startX = boardToScreen(0.0) + lineWidth / 2.0F
            val endX = boardToScreen((boardSize - 1).toDouble()) - lineWidth / 2.0F
            val screenY = boardToScreen(lastY.toDouble())
            if (activeTurn) {
                g.drawLine(startX, screenY, endX, screenY, currentLinePaint)
            }
        }

        fun fillCircle(g: Canvas, x: Int, y: Int, radius: Double, paint: Paint) {
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
                        fillCircle(g, x, y, 0.48, if (cell.color == Player.BLACK) blackTranslucentPaint else whiteTranslucentPaint)
                    }
                    if (cell.territory != null) {
                        fillCircle(g, x, y, 0.16, if (cell.territory == Player.BLACK) blackPaint else whitePaint)
                    }
                } else if (cell.color != null) {
                    fillCircle(g, x, y, 0.48, if (cell.color == Player.BLACK) blackPaint else whitePaint)
                }
            }
        }
    }

    protected class Cell(val color: Player?, val territory: Player?, val dead: Boolean)
}
