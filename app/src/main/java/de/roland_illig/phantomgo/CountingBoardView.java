package de.roland_illig.phantomgo;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

public class CountingBoardView extends AbstractBoardView {

    private Board board;
    private CountingBoard countingBoard;

    public CountingBoardView(Context context) {
        super(context);
    }

    public CountingBoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CountingBoardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void configure(Board board) {
        this.board = board;
        this.countingBoard = new CountingBoard(board);
    }

    @Override
    protected Cell getBoard(int x, int y) {
        Player color = board.get(x, y);
        boolean territory = color == null || countingBoard.isDead(x, y);
        boolean hovering = false;
        return new Cell(color, false, territory, hovering);
    }

    @Override
    protected int getBoardSize() {
        return board.getSize();
    }

    @Override
    protected void boardMouseClicked(int x, int y) {
        countingBoard.toggleDead(x, y);
        invalidate();
    }
}
