package de.roland_illig.phantomgo;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BoardTest {

    @Test
    public void testAtari() {
        Board board = new Board(9);
        board.setup(
                ". . . . . . . . .",
                ". . . . . . . . .",
                ". . . . B . . . .",
                ". . . B W . . . .",
                ". . . . . . . . .",
                ". . . . . . . . .",
                ". . . . . . . . .",
                ". . . . . . . . .",
                ". . . . . . . . .");

        RefereeResult result = board.play(5, 3);

        assertThat(result.toString(), is("atari"));
    }

    @Test
    public void testSuicide() {
        Board board = new Board(9);
        board.setup(
                ". . . . . . . . .",
                ". . . . . . . . .",
                ". . . . W . . . .",
                ". . . W . W . . .",
                ". . . . W . . . .",
                ". . . . . . . . .",
                ". . . . . . . . .",
                ". . . . . . . . .",
                ". . . . . . . . .");

        RefereeResult result = board.play(4, 3);

        assertThat(result.toString(), is("suicide"));
    }

    @Test
    public void testGetLiberties() {
        Board board = new Board(9);
        board.setup(
                "B . . B . . . . .",
                ". . . . . B . . .",
                ". . . . . . . . .",
                ". . B B . B B B .",
                "B . . . . B . B .",
                "B B . . . B B B .",
                ". B B . . . . . .",
                ". . B B . . . . .",
                ". . . B . . . . .");

        assertThat(board.getLiberties(0, 0), is(2));
        assertThat(board.getLiberties(3, 0), is(3));
        assertThat(board.getLiberties(5, 1), is(4));
        assertThat(board.getLiberties(2, 3), is(6));
        assertThat(board.getLiberties(5, 3), is(13));
        assertThat(board.getLiberties(3, 8), is(9));
    }

    @Test
    public void testCapture() {
        Board board = new Board(9);
        board.setup(
                ". . . . . . . . .",
                ". . . . . . . . .",
                ". . . . . . . . .",
                ". . . . B . . . .",
                ". . . B W . . . .",
                ". . . . B . . . .",
                ". . . . . . . . .",
                ". . . . . . . . .",
                ". . . . . . . . .");

        RefereeResult result = board.play(5, 4);

        assertThat(result.toString(), is("captured 1"));
        assertThat(board.getCaptured(Player.BLACK), is(1));
        assertThat(board.getCaptured(Player.WHITE), is(0));
        assertThat(board.toString(), is(""
                + ". . . . . . . . .\n"
                + ". . . . . . . . .\n"
                + ". . . . . . . . .\n"
                + ". . . . B . . . .\n"
                + ". . . B . B . . .\n"
                + ". . . . B . . . .\n"
                + ". . . . . . . . .\n"
                + ". . . . . . . . .\n"
                + ". . . . . . . . .\n"));
    }

    @Test
    public void testCaptureInKo() {
        Board board = new Board(9);
        board.setup(
                ". . . . . . . . .",
                ". . . . . . . . .",
                ". . . . . B . . .",
                ". . . . B W . . .",
                ". . . B W . W . .",
                ". . . . B W . . .",
                ". . . . . . . . .",
                ". . . . . . . . .",
                ". . . . . . . . .");

        RefereeResult result = board.play(5, 4);

        assertThat(result.toString(), is("atari, selfAtari, captured 1"));
    }

    @Test
    public void testSelfAtari() {
        Board board = new Board(9);
        board.setup(
                ". . . . . . . W .",
                ". . . . . . W B W",
                ". . . . . . W B B",
                ". . . . . . . W .",
                ". . . . . . . . .",
                ". . . . . . . . .",
                ". . . . . . . . .",
                ". . . . . . . . .",
                ". . . . . . . . .");

        RefereeResult result = board.play(8, 0);

        assertThat(result.toString(), is("atari, selfAtari, captured 1"));
    }

    @Test
    public void testKo() {
        Board board = new Board(9);
        board.setup(
                ". . . . . . . . .",
                ". . . . . . . . .",
                ". . . . . . . . .",
                ". . . W B . . . .",
                ". . W . . B . . .",
                ". . . W B . . . .",
                ". . . . . . . . .",
                ". . . . . . . . .",
                ". . . . . . . . .");

        RefereeResult result = board.play(3, 4);

        assertThat(result.toString(), is("selfAtari"));

        RefereeResult capture = board.play(4, 4);

        assertThat(capture.toString(), is("selfAtari, captured 1"));

        RefereeResult ko = board.play(3, 4);

        assertThat(ko.toString(), is("ko"));
    }

    @Test
    public void testStayInAtari() {
        Board board = new Board(9);
        board.setup(
                ". . . . . . . W B",
                ". . . . . . . W .",
                ". . . . . . . W .",
                ". . . . . . . . .",
                ". . . . . . . . .",
                ". . . . . . . . .",
                ". . . . . . . . .",
                ". . . . . . . . .",
                ". . . . . . . . .");

        RefereeResult result = board.play(8, 1);

        assertThat(result.toString(), is("ok"));
    }
}
