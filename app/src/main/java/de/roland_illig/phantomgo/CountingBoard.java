package de.roland_illig.phantomgo;

import android.support.annotation.VisibleForTesting;

public class CountingBoard {
    private final int size;
    private final Player[][] color;
    private final int[][] region;
    private final boolean[][] dead;
    private final Player[][] territory;
    private CountResult countResult;
    private final int blackCaptured;
    private final int whiteCaptured;

    public CountingBoard(Board board) {
        this.size = board.getSize();
        this.color = initPieces(board);
        this.region = initRegion(color);
        this.dead = new boolean[size][size];
        this.territory = new Player[size][size];
        this.blackCaptured = board.getCaptured(Player.BLACK);
        this.whiteCaptured = board.getCaptured(Player.WHITE);
    }

    public void toggleDead(int x, int y) {
        if (color[x][y] == null) {
            throw new IllegalStateException(x + "," + y);
        }

        boolean toToggle[][] = new boolean[size][size];
        floodFillStep(color, toToggle, new boolean[size][size], color[x][y], x, y);
        floodFillStep(color, toToggle, new boolean[size][size], null, x, y);
        floodFillStep(color, toToggle, new boolean[size][size], color[x][y], x, y);

        for (int j = 0; j < size; j++) {
            for (int i = 0; i < size; i++) {
                if (toToggle[i][j] && color[i][j] != null) {
                    dead[i][j] = !dead[i][j];
                }
            }
        }

        countResult = null;
    }

    private static Player[][] initPieces(Board board) {
        int size = board.getSize();
        Player[][] pieces = new Player[size][size];
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                pieces[x][y] = board.get(x, y);
            }
        }
        return pieces;
    }

    private static int[][] initRegion(Player[][] pieces) {
        int size = pieces.length;
        int[][] chain = new int[size][size];
        int id = 1;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (chain[x][y] == 0) {
                    floodFill(pieces, chain, pieces[x][y], id, x, y);
                    id++;
                }
            }
        }
        return chain;
    }

    private static void floodFill(Player[][] in, int[][] out, Player from, int to, int x, int y) {
        if (0 <= x && x < out.length && 0 <= y && y < out.length) {
            if (out[x][y] == 0 && in[x][y] == from) {
                out[x][y] = to;
                floodFill(in, out, from, to, x - 1, y);
                floodFill(in, out, from, to, x + 1, y);
                floodFill(in, out, from, to, x, y - 1);
                floodFill(in, out, from, to, x, y + 1);
            }
        }
    }

    private static void floodFillStep(Player[][] in, boolean[][] out, boolean[][] done, Player from, int x, int y) {
        if (0 <= x && x < out.length && 0 <= y && y < out.length) {
            if (!done[x][y] && (out[x][y] || in[x][y] == from)) {
                done[x][y] = true;
                out[x][y] = true;
                floodFillStep(in, out, done, from, x - 1, y);
                floodFillStep(in, out, done, from, x + 1, y);
                floodFillStep(in, out, done, from, x, y - 1);
                floodFillStep(in, out, done, from, x, y + 1);
            }
        }
    }

    @VisibleForTesting
    String regionsToString() {
        String alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                sb.append(alphabet.charAt(region[x][y]));
                sb.append(x == size - 1 ? "\n" : " ");
            }
        }
        return sb.toString();
    }

    public CountResult count() {
        if (countResult != null) {
            return countResult;
        }

        int blackDead = 0;
        int whiteDead = 0;

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (dead[x][y]) {
                    if (color[x][y] == Player.BLACK) {
                        blackDead++;
                    } else {
                        whiteDead++;
                    }
                }
            }
        }

        boolean[][] black = new boolean[size][size];
        boolean[][] white = new boolean[size][size];

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                Player color = dead[x][y] ? null : this.color[x][y];
                if (color == Player.BLACK) {
                    black[x][y] = true;
                }
                if (color == Player.WHITE) {
                    white[x][y] = true;
                }
            }
        }

        while (true) {
            boolean changed = false;
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    if (color[x][y] == null || dead[x][y]) {
                        if (!black[x][y] && hasNeighbor(x, y, black)) {
                            black[x][y] = true;
                            changed = true;
                        }
                        if (!white[x][y] && hasNeighbor(x, y, white)) {
                            white[x][y] = true;
                            changed = true;
                        }
                    }
                }
            }

            if (!changed) {
                int blackTerritory = 0;
                int whiteTerritory = 0;
                for (int y = 0; y < size; y++) {
                    for (int x = 0; x < size; x++) {
                        territory[x][y] = null;
                        if (color[x][y] == null || dead[x][y]) {
                            if (black[x][y] && !white[x][y]) {
                                territory[x][y] = Player.BLACK;
                                blackTerritory++;
                            }
                            if (white[x][y] && !black[x][y]) {
                                territory[x][y] = Player.WHITE;
                                whiteTerritory++;
                            }
                        }
                    }
                }

                countResult = new CountResult(
                        blackTerritory, whiteTerritory,
                        blackCaptured + whiteDead, whiteCaptured + blackDead);
                return countResult;
            }
        }
    }

    private boolean hasNeighbor(int x, int y, boolean[][] color) {
        return x > 0 && color[x - 1][y]
                || y > 0 && color[x][y - 1]
                || x + 1 < size && color[x + 1][y]
                || y + 1 < size && color[x][y + 1];
    }

    public boolean isDead(int x, int y) {
        return dead[x][y];
    }

    public Player getTerritory(int x, int y) {
        count();
        return territory[x][y];
    }

    @Override
    public String toString() {
        count();
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (territory[x][y] == Player.WHITE) {
                    sb.append(dead[x][y] ? '#' : 'w');
                } else if (territory[x][y] == Player.BLACK) {
                    sb.append(dead[x][y] ? '#' : 'b');
                } else {
                    sb.append(color[x][y] == Player.BLACK ? 'B' : color[x][y] == Player.WHITE ? 'W' : '.');
                }
                sb.append(x == size - 1 ? "\n" : " ");
            }
        }
        return sb.toString();
    }
}
