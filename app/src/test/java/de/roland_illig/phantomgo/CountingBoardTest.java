package de.roland_illig.phantomgo;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CountingBoardTest {

    @Test
    public void testCountSimple() {
        Board board = new Board(9);
        board.setup(
                ". . B . . W . . .",
                ". . B . . W . . .",
                ". . B . . W . . .",
                ". . B . . W . . .",
                ". . B . . W . . .",
                ". . B . . W . . .",
                ". . B . . W . . .",
                ". . B . . W . . .",
                ". . B . . W . . .");

        CountingBoard countingBoard = new CountingBoard(board);

        assertThat(countingBoard.regionsToString(), is(""
                + "1 1 2 3 3 4 5 5 5\n"
                + "1 1 2 3 3 4 5 5 5\n"
                + "1 1 2 3 3 4 5 5 5\n"
                + "1 1 2 3 3 4 5 5 5\n"
                + "1 1 2 3 3 4 5 5 5\n"
                + "1 1 2 3 3 4 5 5 5\n"
                + "1 1 2 3 3 4 5 5 5\n"
                + "1 1 2 3 3 4 5 5 5\n"
                + "1 1 2 3 3 4 5 5 5\n"));

        CountResult result = countingBoard.count();
        assertThat(result.blackTerritory, is(18));
        assertThat(result.whiteTerritory, is(27));
        assertThat(result.blackCaptured, is(0));
        assertThat(result.whiteCaptured, is(0));
    }

    @Test
    public void testCountBeforeMarkingDeadStones() {
        Board board = new Board(9);
        board.setup(
                "B . . . W W B . .",
                "B W . W W B B . .",
                "B W . W B B B . .",
                "W W W W W B . . .",
                "B B W B B B . . .",
                ". B B W W B B B B",
                "B B W . W W B W W",
                ". B W W . W W B .",
                ". B W . . . W B .");

        CountingBoard countingBoard = new CountingBoard(board);

        assertThat(countingBoard.regionsToString(), is(""
                + "1 2 2 2 3 3 4 5 5\n"
                + "1 3 2 3 3 4 4 5 5\n"
                + "1 3 2 3 4 4 4 5 5\n"
                + "3 3 3 3 3 4 5 5 5\n"
                + "6 6 3 4 4 4 5 5 5\n"
                + "7 6 6 8 8 4 4 4 4\n"
                + "6 6 9 A 8 8 4 B B\n"
                + "C 6 9 9 D 8 8 E F\n"
                + "C 6 9 D D D 8 E F\n"));

        CountResult result = countingBoard.count();
        assertThat(result.toString(), is("black=15+0, white=5+0"));
    }

    @Test
    public void testCountAfterMarkingDeadStones() {
        Board board = new Board(9);
        board.setup(
                "B . . . W W B . .",
                "B W . W W B B . .",
                "B W . W B B B . .",
                "W W W W W B . . .",
                "B B W B B B . . .",
                ". B B W W B B B B",
                "B B W . W W B W W",
                ". B W W . W W B .",
                ". B W . . . W B .");

        CountingBoard countingBoard = new CountingBoard(board);
        countingBoard.toggleDead(0, 0);
        countingBoard.toggleDead(8, 6);

        CountResult result = countingBoard.count();
        assertThat(result.toString(), is("black=19+2, white=13+3"));

        assertThat(countingBoard.toString(), is(""
                + "# w w w W W B b b\n"
                + "# W w W W B B b b\n"
                + "# W w W B B B b b\n"
                + "W W W W W B b b b\n"
                + "B B W B B B b b b\n"
                + "b B B W W B B B B\n"
                + "B B W w W W B # #\n"
                + "b B W W w W W B b\n"
                + "b B W w w w W B b\n"));
    }

    @Test
    public void testBoardViewAfterMarkingDeadStones() {
        Board board = new Board(9);
        board.setup(
                "B . . . W W B . .",
                "B W . W W B B . .",
                "B W . W B B B . .",
                "W W W W W B . . .",
                "B B W B B B . . .",
                ". B B W W B B B B",
                "B B W . W W B W W",
                ". B W W . W W B .",
                ". B W . . . W B .");

        CountingBoard countingBoard = new CountingBoard(board);
        countingBoard.toggleDead(0, 0);
        countingBoard.toggleDead(8, 6);

        CountResult result = countingBoard.count();
        assertThat(result.toString(), is("black=19+2, white=13+3"));
    }

    @Test
    public void testToggleDead() {
        Board board = new Board(9);
        board.setup(
                "W . . B . . . . W",
                ". . B . . . . W .",
                ". B . . . . W . .",
                "B . . . . W . . .",
                ". . . . W . . . .",
                ". . . W . . . . B",
                ". . W . . . . B .",
                ". W . . . . B . .",
                "W . . . . B . . W");
        CountingBoard countingBoard = new CountingBoard(board);

        countingBoard.toggleDead(0, 8);

        assertThat(countingBoard.toString(), is(""
                + "W . . B b b b b #\n"
                + ". . B b b b b # b\n"
                + ". B b b b b # b b\n"
                + "B b b b b # b b b\n"
                + "b b b b # b b b b\n"
                + "b b b # b b b b B\n"
                + "b b # b b b b B .\n"
                + "b # b b b b B . .\n"
                + "# b b b b B . . W\n"));

        countingBoard.toggleDead(0, 8);

        assertThat(countingBoard.toString(), is(""
                + "W . . B . . . . W\n"
                + ". . B . . . . W .\n"
                + ". B . . . . W . .\n"
                + "B . . . . W . . .\n"
                + ". . . . W . . . .\n"
                + ". . . W . . . . B\n"
                + ". . W . . . . B .\n"
                + ". W . . . . B . .\n"
                + "W . . . . B . . W\n"));
    }
}
