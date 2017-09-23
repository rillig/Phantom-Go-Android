package de.roland_illig.phantomgo;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;
import de.roland_illig.android.phantomgo.R;

public class PlayerBoardView extends AbstractBoardView {

    private Board refereeBoard = new Board(9);
    private Board board = new Board(9);
    private Player player = Player.BLACK;
    private int hoverX = -1;
    private int hoverY = -1;

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
                    return new Cell(refereeBoard.getTurn(), false, false, true);
                }
            }
        }
        return new Cell(board.get(x, y), false, false, false);
    }

    @Override
    protected void boardMouseClicked(int x, int y) {
        if (isToggleButtonChecked(R.id.playButton)) {
            if (player != refereeBoard.getTurn()) {
                ((TextView) findParentView(R.id.referee)).setText(R.string.not_your_turn);
                return;
            }
            Board playerBoard = board;
            RefereeResult result = refereeBoard.play(x, y);
            if (result.invalidReason != null) {
                switch (result.invalidReason) {
                    case OTHER_STONE:
                        playerBoard.set(x, y, player.other());
                        break;
                    case OWN_STONE:
                        playerBoard.set(x, y, player);
                        break;
                    case SUICIDE:
                    case KO:
                        playerBoard.set(x, y, null);
                        break;
                }
            } else {
                RefereeResult playerResult = playerBoard.copy().play(x, y);
                if (playerResult.toString().equals(result.toString())) {
                    playerBoard.setTurn(player);
                    playerBoard.play(x, y);
                } else {
                    playerBoard.set(x, y, player);
                }
                //getBoard(player.other()).setTurn(player.other());
            }
        } else if (isToggleButtonChecked(R.id.blackButton)) {
            board.setTurn(Player.BLACK);
            if (board.play(x, y).invalidReason != null) {
                board.set(x, y, Player.BLACK);
            }
        } else if (isToggleButtonChecked(R.id.whiteButton)) {
            board.setTurn(Player.WHITE);
            if (board.play(x, y).invalidReason != null) {
                board.set(x, y, Player.WHITE);
            }
        } else if (isToggleButtonChecked(R.id.eraserButton)) {
            board.set(x, y, null);
        }
        invalidate();
    }

    private <T extends View> T findParentView(int resourceId) {
        return ((View) getParent()).findViewById(resourceId);
    }

    private boolean isToggleButtonChecked(int resourceId) {
        ToggleButton button = findParentView(resourceId);
        return button.isChecked();
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
}
