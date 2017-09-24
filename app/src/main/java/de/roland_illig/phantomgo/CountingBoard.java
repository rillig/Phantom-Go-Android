package de.roland_illig.phantomgo;

import android.support.annotation.VisibleForTesting;

public class CountingBoard {
    private final int size;
    private final Player[][] color;
    private final int[][] region;
    private final boolean[][] dead;
    private final Player[][] territory;

    public CountingBoard(Board board) {
        this.size = board.getSize();
        this.color = initPieces(board);
        this.region = initRegion(color);
        this.dead = new boolean[size][size];
        this.territory = new Player[size][size];

        count();
    }

    public void toggleDead(int x, int y) {
        if (color[x][y] == null) {
            throw new IllegalStateException(x + "," + y);
        }

        int regionId = region[x][y];
        for (int j = 0; j < size; j++) {
            for (int i = 0; i < size; i++) {
                if (region[i][j] == regionId) {
                    dead[i][j] = !dead[i][j];
                }
            }
        }

        count();
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
                    floodFill(chain, pieces, pieces[x][y], id, x, y);
                    id++;
                }
            }
        }
        return chain;
    }

    private static void floodFill(int[][] chain, Player[][] pieces, Player player, int id, int x, int y) {
        if (0 <= x && x < chain.length && 0 <= y && y < chain.length) {
            if (chain[x][y] == 0 && pieces[x][y] == player) {
                chain[x][y] = id;
                floodFill(chain, pieces, player, id, x - 1, y);
                floodFill(chain, pieces, player, id, x + 1, y);
                floodFill(chain, pieces, player, id, x, y - 1);
                floodFill(chain, pieces, player, id, x, y + 1);
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
                return new CountResult(blackTerritory, whiteTerritory, blackDead, whiteDead);
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
        return territory[x][y];
    }

    @Override
    public String toString() {
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
