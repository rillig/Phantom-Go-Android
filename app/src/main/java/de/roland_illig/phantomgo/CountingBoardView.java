package de.roland_illig.phantomgo;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import de.roland_illig.android.phantomgo.R;

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
        updateSummary();
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
            updateSummary();
            invalidate();
        }
    }

    private void updateSummary() {
        CountResult result = countingBoard.count();
        String summary = getResources().getString(R.string.result_summary,
                result.blackTerritory, result.blackCaptured, result.blackScore,
                result.whiteTerritory, result.whiteCaptured, result.whiteScore);
        ((TextView) ((View) getParent()).findViewById(R.id.countingSummary)).setText(summary);
    }
}
