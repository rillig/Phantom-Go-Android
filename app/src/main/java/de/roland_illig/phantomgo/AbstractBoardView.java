package de.roland_illig.phantomgo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public abstract class AbstractBoardView extends View {

    private float lastX;
    private float lastY;

    protected abstract Cell getBoard(int x, int y);

    protected abstract int getBoardSize();

    protected void boardMouseMoved(int x, int y) {
    }

    protected void boardMouseClicked(int x, int y) {
    }

    protected void boardMouseExited() {
    }

    public AbstractBoardView(Context context) {
        super(context);
        init();
    }

    public AbstractBoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AbstractBoardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent e) {
                lastX = e.getX();
                lastY = e.getY();
                return false;
            }
        });
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int x = screenToBoard(lastX);
                int y = screenToBoard(lastY);
                if (0 <= x && x < getBoardSize() && 0 <= y && y < getBoardSize()) {
                    boardMouseClicked(x, y);
                }
            }
        });
        /*
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int x = screenToBoard(e.getX());
                int y = screenToBoard(e.getY());
                boardMouseMoved(x, y);
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boardMouseExited();
            }
        });
        */
    }

    @Override
    protected void onDraw(Canvas g) {
        super.onDraw(g);

        int bsize = getBoardSize();

        Paint boardPaint = solidPaint(0xFFD48E00);
        Paint blackPaint = solidPaint(0xFF000000);
        Paint whitePaint = solidPaint(0xFFFFFFFF);
        Paint blackHoverPaint = solidPaint(0x55000000);
        Paint whiteHoverPaint = solidPaint(0x55FFFFFF);

        g.drawPaint(boardPaint);

        for (int i = 0; i < bsize; i++) {
            int start = boardToScreen(0);
            int end = boardToScreen(bsize - 1);
            int fixed = boardToScreen(i);
            g.drawLine(start, fixed, end, fixed, blackPaint);
            g.drawLine(fixed, start, fixed, end, blackPaint);
        }

        for (int y = 0; y < bsize; y++) {
            for (int x = 0; x < bsize; x++) {

                Cell cell = getBoard(x, y);
                if (cell.hovering) {
                    fillCircle(g, x, y, 0.48, cell.color == Player.BLACK ? blackHoverPaint : whiteHoverPaint);
                } else if (cell.dead || cell.territory != null) {
                    if (cell.dead) {
                        fillCircle(g, x, y, 0.48, cell.color == Player.BLACK ? blackHoverPaint : whiteHoverPaint);
                    }
                    if (cell.territory != null) {
                        fillCircle(g, x, y, 0.16, cell.territory == Player.BLACK ? blackPaint : whitePaint);
                    }
                } else if (cell.color != null) {
                    fillCircle(g, x, y, 0.48, cell.color == Player.BLACK ? blackPaint : whitePaint);
                }
            }
        }
    }

    private Paint solidPaint(int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        return paint;
    }

    private void fillCircle(Canvas g, int x, int y, double radius, Paint paint) {
        int top = boardToScreen(y - radius);
        int left = boardToScreen(x - radius);
        int bottom = boardToScreen(y + radius);
        int right = boardToScreen(x + radius);
        g.drawOval(new RectF(left, top, right, bottom), paint);
    }

    private int boardToScreen(double bc) {
        int size = Math.min(getWidth(), getHeight());
        return (int) Math.round((size * bc + size) / (getBoardSize() + 1));
    }

    private int screenToBoard(float sc) {
        int size = Math.min(getWidth(), getHeight());
        return (int) Math.round(((double) (sc * (getBoardSize() + 1))) / size - 1);
    }

    protected static class Cell {
        protected final Player color;
        protected final boolean lastMove;
        protected final Player territory;
        protected final boolean hovering;
        protected final boolean dead;

        protected Cell(Player color, boolean lastMove, Player territory, boolean hovering, boolean dead) {
            this.color = color;
            this.lastMove = lastMove;
            this.territory = territory;
            this.hovering = hovering;
            this.dead = dead;
        }
    }
}
