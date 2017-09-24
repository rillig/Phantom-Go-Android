package de.roland_illig.phantomgo;

import android.support.annotation.VisibleForTesting;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Board {
    private final int size;

    private final Player[][] pieces;
    private final int[] captured = new int[2];
    private Player turn;
    private Point prevMove;
    private boolean passed;
    private boolean gameOver;

    private RefereeListener refereeListener;
    private UpdateListener updateListener;

    public Board(int size) {
        this.size = size;
        pieces = new Player[size][size];
        reset();
    }

    public void reset() {
        turn = Player.BLACK;
        for (Player[] row : pieces) {
            Arrays.fill(row, null);
        }
        prevMove = new Point(-1, -1);
        Arrays.fill(captured, 0);
        passed = false;
        gameOver = false;

        if (updateListener != null) {
            updateListener.onUpdate();
        }
    }

    public Board copy() {
        Board copy = new Board(size);
        for (int x = 0; x < size; x++) {
            System.arraycopy(pieces[x], 0, copy.pieces[x], 0, size);
        }
        System.arraycopy(captured, 0, copy.captured, 0, captured.length);
        copy.turn = turn;
        copy.prevMove = prevMove;
        return copy;
    }

    public Player get(int x, int y) {
        return pieces[x][y];
    }

    public int getCaptured(Player player) {
        return captured[player.ordinal()];
    }

    private int tryCapture(int x, int y, Player turn) {
        if (0 <= x && x < size && 0 <= y && y < size) {
            if (getLiberties(x, y, turn) == 0) {
                return capture(x, y, turn);
            }
        }
        return 0;
    }

    private int getLiberties(int x, int y, Player color) {
        return get(x, y) == color ? getLiberties(x, y) : -1;
    }

    @VisibleForTesting
    int getLiberties(int x, int y) {
        LibertiesCounter counter = new LibertiesCounter(get(x, y));
        counter.count(x, y);
        return counter.liberties.size();
    }

    private int capture(int x, int y, Player color) {
        int captured = 1;
        pieces[x][y] = null;
        if (x > 0 && pieces[x - 1][y] == color)
            captured += capture(x - 1, y, color);
        if (x < size - 1 && pieces[x + 1][y] == color)
            captured += capture(x + 1, y, color);
        if (y > 0 && pieces[x][y - 1] == color)
            captured += capture(x, y - 1, color);
        if (y < size - 1 && pieces[x][y + 1] == color)
            captured += capture(x, y + 1, color);
        return captured;
    }

    public void setRefereeListener(RefereeListener refereeListener) {
        this.refereeListener = refereeListener;
    }

    public void setUpdateListener(UpdateListener updateListener) {
        this.updateListener = updateListener;
    }

    public void set(int x, int y, Player color) {
        pieces[x][y] = color;
        if (updateListener != null) {
            updateListener.onUpdate();
        }
    }

    public void setTurn(Player turn) {
        this.turn = turn;
    }

    public interface RefereeListener {
        void onReferee(RefereeResult result);
    }

    public interface UpdateListener {
        void onUpdate();
    }

    private class LibertiesCounter {
        final Player color;
        final Set<Point> todo = new HashSet<>();
        final Set<Point> done = new HashSet<>();
        final Set<Point> liberties = new HashSet<>();

        LibertiesCounter(Player color) {
            color.getClass();
            this.color = color;
        }

        void count(int x, int y) {
            todo.add(new Point(x, y));

            while (!todo.isEmpty()) {
                Iterator<Point> it = todo.iterator();
                Point point = it.next();
                it.remove();
                done.add(point);

                int px = point.x;
                int py = point.y;
                if (px > 0) {
                    countInternal(px - 1, py);
                }
                if (py > 0) {
                    countInternal(px, py - 1);
                }
                if (px < size - 1) {
                    countInternal(px + 1, py);
                }
                if (py < size - 1) {
                    countInternal(px, py + 1);
                }
            }
        }

        private void countInternal(int nx, int ny) {
            Point np = new Point(nx, ny);
            Player neighbor = get(nx, ny);
            if (neighbor == color && !done.contains(np)) {
                todo.add(np);
            }
            if (neighbor == null) {
                liberties.add(np);
            }
        }
    }

    public int getSize() {
        return size;
    }

    public RefereeResult play(int x, int y) {
        RefereeResult result = playInternal(x, y);
        if (refereeListener != null) {
            refereeListener.onReferee(result);
        }
        if (result.invalidReason == null && updateListener != null) {
            updateListener.onUpdate();
        }
        return result;
    }

    private RefereeResult playInternal(int x, int y) {
        if (gameOver) {
            throw new IllegalStateException("GameOver");
        }
        Player other = turn.other();

        if (get(x, y) == turn) {
            return RefereeResult.ownStone();
        }
        if (get(x, y) == other) {
            return RefereeResult.otherStone();
        }

        boolean selfLeftBefore = x > 0 && getLiberties(x - 1, y, turn) == 1;
        boolean selfAboveBefore = y > 0 && getLiberties(x, y - 1, turn) == 1;
        boolean selfRightBefore = x + 1 < size && getLiberties(x + 1, y, turn) == 1;
        boolean selfBelowBefore = y + 1 < size && getLiberties(x, y + 1, turn) == 1;
        boolean selfAtariBefore = selfLeftBefore || selfAboveBefore || selfRightBefore || selfBelowBefore;

        int leftBefore = x > 0 ? getLiberties(x - 1, y, other) : 0;
        int aboveBefore = y > 0 ? getLiberties(x, y - 1, other) : 0;
        int rightBefore = x + 1 < size ? getLiberties(x + 1, y, other) : 0;
        int belowBefore = y + 1 < size ? getLiberties(x, y + 1, other) : 0;

        int dx = x - prevMove.x;
        int dy = y - prevMove.y;
        if (dx * dx + dy * dy == 1) {
            if (getLiberties(prevMove.x, prevMove.y, other) == 1) {
                return RefereeResult.ko();
            }
        }

        pieces[x][y] = turn;
        boolean undo = true;
        try {
            boolean capturesSomething = leftBefore == 1 || aboveBefore == 1 || rightBefore == 1 || belowBefore == 1;
            if (!capturesSomething && getLiberties(x, y) == 0) {
                return RefereeResult.suicide();
            }

            int left = x > 0 ? getLiberties(x - 1, y, other) : 0;
            int above = y > 0 ? getLiberties(x, y - 1, other) : 0;
            int right = x + 1 < size ? getLiberties(x + 1, y, other) : 0;
            int below = y + 1 < size ? getLiberties(x, y + 1, other) : 0;

            boolean atari = leftBefore > 1 && left == 1
                    || aboveBefore > 1 && above == 1
                    || rightBefore > 1 && right == 1
                    || belowBefore > 1 && below == 1;

            int capturedStones = tryCapture(x - 1, y, other)
                    + tryCapture(x, y - 1, other)
                    + tryCapture(x + 1, y, other)
                    + tryCapture(x, y + 1, other);

            boolean selfAtari = getLiberties(x, y, turn) == 1 && !selfAtariBefore;

            captured[turn.ordinal()] += capturedStones;
            prevMove = capturedStones == 1 && selfAtari ? new Point(x, y) : new Point(-1, -1);
            turn = turn.other();
            undo = false;

            return RefereeResult.ok(atari, selfAtari, capturedStones);
        } finally {
            if (undo) {
                pieces[x][y] = null;
            }
        }
    }

    public RefereeResult pass() {
        if (gameOver) {
            throw new IllegalStateException("GameOver");
        }
        gameOver = passed;
        passed = true;
        turn = turn.other();
        if (updateListener != null) {
            updateListener.onUpdate();
        }
        return RefereeResult.pass();
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public Player getTurn() {
        return turn;
    }

    @VisibleForTesting
    public void setup(String... rows) {
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                char ch = rows[y].charAt(2 * x);
                pieces[x][y] = parseChar(ch);
            }
        }
    }

    private static Player parseChar(char ch) {
        if (ch == 'W') return Player.WHITE;
        if (ch == 'B') return Player.BLACK;
        if (ch == '.') return null;
        throw new IllegalStateException();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                Player player = get(x, y);
                sb.append(player != null ? "BW".charAt(player.ordinal()) : '.');
                sb.append(x == size - 1 ? "\n" : " ");
            }
        }
        return sb.toString();
    }

    private static class Point {
        private final int x;
        private final int y;

        private Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Point point = (Point) obj;
            return x == point.x && y == point.y;
        }

        @Override
        public int hashCode() {
            return 31 * x + y;
        }
    }
}
