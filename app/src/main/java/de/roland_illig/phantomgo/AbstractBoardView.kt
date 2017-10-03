package de.roland_illig.phantomgo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

abstract class AbstractBoardView : View {

    private var lastX = 0.toFloat()
    private var lastY = 0.toFloat()

    protected abstract val boardSize: Int

    protected abstract fun getBoard(x: Int, y: Int): Cell

    protected open fun boardMouseClicked(x: Int, y: Int) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        setOnTouchListener { view, e ->
            lastX = e.x
            lastY = e.y
            false
        }
        setOnClickListener {
            val x = screenToBoard(lastX)
            val y = screenToBoard(lastY)
            if (x in 0 until boardSize && y in 0 until boardSize) {
                boardMouseClicked(x, y)
            }
        }
    }

    override fun onDraw(g: Canvas) {
        super.onDraw(g)

        val bsize = boardSize

        val linePaint = solidPaint(0xFF000000.toInt())
        linePaint.strokeWidth = lineWidth().toFloat()
        val boardPaint = solidPaint(0xFFD48E00.toInt())
        val blackPaint = solidPaint(0xFF000000.toInt())
        val whitePaint = solidPaint(0xFFFFFFFF.toInt())
        val blackTranslucentPaint = solidPaint(0x55000000)
        val whiteTranslucentPaint = solidPaint(0x55FFFFFF)

        g.drawPaint(boardPaint)

        for (i in 0 until bsize) {
            val start = boardToScreen(0.0)
            val end = boardToScreen((bsize - 1).toDouble())
            val fixed = boardToScreen(i.toDouble())
            g.drawLine(start.toFloat(), fixed.toFloat(), end.toFloat(), fixed.toFloat(), linePaint)
            g.drawLine(fixed.toFloat(), start.toFloat(), fixed.toFloat(), end.toFloat(), linePaint)
        }

        for (y in 0 until bsize) {
            for (x in 0 until bsize) {

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

    private fun fillCircle(g: Canvas, x: Int, y: Int, radius: Double, paint: Paint) {
        val top = boardToScreen(y - radius)
        val left = boardToScreen(x - radius)
        val bottom = boardToScreen(y + radius)
        val right = boardToScreen(x + radius)
        g.drawOval(RectF(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat()), paint)
    }

    private fun lineWidth(): Int {
        return Math.max(1, ((boardToScreen(1.0) - boardToScreen(0.0)) / 20.0).toInt())
    }

    private fun boardToScreen(bc: Double): Int {
        val size = Math.min(width, height)
        return Math.round((size * bc + size) / (boardSize + 1)).toInt()
    }

    private fun screenToBoard(sc: Float): Int {
        val size = Math.min(width, height)
        return Math.round((sc * (boardSize + 1)).toDouble() / size - 1).toInt()
    }

    protected class Cell(val color: Player?, val territory: Player?, val dead: Boolean)

    companion object {
        private fun solidPaint(color: Int): Paint {
            val paint = Paint()
            paint.color = color
            paint.style = Paint.Style.FILL
            paint.isAntiAlias = true
            return paint
        }
    }
}
