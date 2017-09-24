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

    public void configure(Board board, CountingBoard countingBoard) {
        this.board = board;
        this.countingBoard = countingBoard;
    }

    @Override
    protected Cell getBoard(int x, int y) {
        Player stone = board.get(x, y);
        Player territory = countingBoard.getTerritory(x, y);
        boolean dead = countingBoard.isDead(x, y);
        return new Cell(stone, false, territory, false, dead);
    }

    @Override
    protected int getBoardSize() {
        return board.getSize();
    }

    @Override
    protected void boardMouseClicked(int x, int y) {
        if (board.get(x, y) != null) {
            countingBoard.toggleDead(x, y);
            invalidate();
        }
    }
}
