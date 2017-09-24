package de.roland_illig.phantomgo;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import de.roland_illig.android.phantomgo.R;
import java.util.ArrayList;
import java.util.List;

public class PlayerBoardView extends AbstractBoardView {

    private Board refereeBoard = new Board(9);
    private Board board = new Board(9);
    private Player player = Player.BLACK;
    private int hoverX = -1;
    private int hoverY = -1;
    private List<RefereeResult> refereeResults = new ArrayList<>();

    public PlayerBoardView(Context context) {
        super(context);
    }

    public PlayerBoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PlayerBoardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void configure(Board refereeBoard, Board board, Player player) {
        this.refereeBoard = refereeBoard;
        this.board = board;
        this.player = player;
    }

    @Override
    protected int getBoardSize() {
        return board.getSize();
    }

    @Override
    protected Cell getBoard(int x, int y) {
        if (player == refereeBoard.getTurn()) {
            if (x == hoverX && y == hoverY) {
                if (board.get(hoverX, hoverY) == null) {
                    return new Cell(refereeBoard.getTurn(), false, null, true, false);
                }
            }
        }
        return new Cell(board.get(x, y), false, null, false, false);
    }

    @Override
    protected void boardMouseClicked(int x, int y) {
        if (isChecked(R.id.playButton)) {
            onPlayModeClick(x, y);
        } else if (isChecked(R.id.blackButton)) {
            board.setTurn(Player.BLACK);
            if (board.play(x, y).invalidReason != null) {
                board.set(x, y, Player.BLACK);
            }
        } else if (isChecked(R.id.whiteButton)) {
            board.setTurn(Player.WHITE);
            if (board.play(x, y).invalidReason != null) {
                board.set(x, y, Player.WHITE);
            }
        } else if (isChecked(R.id.eraserButton)) {
            board.set(x, y, null);
        }
        invalidate();
    }

    private boolean isChecked(int resourceId) {
        return ((RadioButton) ((View) getParent()).findViewById(resourceId)).isChecked();
    }

    private void onPlayModeClick(int x, int y) {
        if (player != refereeBoard.getTurn()) {
            ((TextView) findParentView(R.id.referee)).setText(R.string.not_your_turn);
            return;
        }

        RefereeResult result = refereeBoard.play(x, y);
        refereeResults.add(result);
        ((TextView) findParentView(R.id.referee)).setText(GermanReferee.comment(result, player));

        if (result.invalidReason != null) {
            switch (result.invalidReason) {
                case OTHER_STONE:
                    board.set(x, y, player.other());
                    break;
                case OWN_STONE:
                    board.set(x, y, player);
                    break;
                case SUICIDE:
                case KO:
                    board.set(x, y, null);
                    break;
            }
        } else {
            RefereeResult playerResult = board.copy().play(x, y);
            if (playerResult.toString().equals(result.toString())) {
                board.setTurn(player);
                board.play(x, y);
            } else {
                board.set(x, y, player);
            }
            findParentView(R.id.handOverButton).setEnabled(true);
        }
    }

    private <T extends View> T findParentView(int resourceId) {
        return ((View) getParent()).findViewById(resourceId);
    }

    @Override
    protected void boardMouseMoved(int x, int y) {
        if (x != hoverX || y != hoverY) {
            hoverX = x;
            hoverY = y;
            invalidate();
        }
    }

    @Override
    protected void boardMouseExited() {
        hoverX = -1;
        hoverY = -1;
        invalidate();
    }

    public List<RefereeResult> getRefereeResults() {
        return refereeResults;
    }

    public void pass() {
        if (player != refereeBoard.getTurn()) {
            ((TextView) findParentView(R.id.referee)).setText(R.string.not_your_turn);
            return;
        }

        RefereeResult result = refereeBoard.pass();
        refereeResults.add(result);
        ((TextView) findParentView(R.id.referee)).setText(GermanReferee.comment(result, player));
    }
}
