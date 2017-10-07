package de.roland_illig.android.phantomgo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import de.roland_illig.phantomgo.Player

abstract class AbstractBoardView : View {

    private var lastX = -1
    private var lastY = -1

    protected abstract val boardSize: Int

    protected abstract fun getBoard(x: Int, y: Int): Cell

    protected abstract fun onBoardClicked(x: Int, y: Int)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        setOnTouchListener { view, e ->
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

        fun solidPaint(color: Long): Paint {
            val paint = Paint()
            paint.color = color.toInt()
            paint.style = Paint.Style.FILL
            paint.isAntiAlias = true
            return paint
        }

        val linePaint = solidPaint(0xFF000000).also { it.strokeWidth = lineWidth }
        val currentLinePaint = solidPaint(0xFFFF9900).also { it.strokeWidth = lineWidth }
        val boardPaint = solidPaint(0xFFD48E00)
        val blackPaint = solidPaint(0xFF000000)
        val whitePaint = solidPaint(0xFFFFFFFF)
        val blackTranslucentPaint = solidPaint(0x55000000)
        val whiteTranslucentPaint = solidPaint(0x55FFFFFF)

        g.drawRect(RectF(0.toFloat(), 0.toFloat(), screenSize, screenSize), boardPaint)

        for (i in 0 until boardSize) {
            val start = boardToScreen(0.0)
            val end = boardToScreen((boardSize - 1).toDouble())
            val fixed = boardToScreen(i.toDouble())
            val hpaint = if (i == lastY && lastX in 0 until boardSize) currentLinePaint else linePaint
            val vpaint = if (i == lastX && lastY in 0 until boardSize) currentLinePaint else linePaint
            g.drawLine(start, fixed, end, fixed, hpaint)
            g.drawLine(fixed, start, fixed, end, vpaint)
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
