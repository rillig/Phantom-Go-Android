package de.roland_illig.phantomgo;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

public class RefereeBoardView extends AbstractBoardView {

    private Board board = new Board(9);

    public RefereeBoardView(Context context) {
        super(context);
    }

    public RefereeBoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RefereeBoardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void configure(Board board) {
        this.board = board;
    }

    @Override
    protected int getBoardSize() {
        return board.getSize();
    }

    @Override
    protected Cell getBoard(int x, int y) {
        return new Cell(board.get(x, y), false, false, false);
    }
}
