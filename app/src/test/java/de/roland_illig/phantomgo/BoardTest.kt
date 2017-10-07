package de.roland_illig.phantomgo

import org.junit.Test

import org.hamcrest.CoreMatchers.`is` as eq
import org.junit.Assert.assertThat

class BoardTest {

    @Test
    fun testAtari() {
        val board = Board(9)
        board.setup(
                ". . . . . . . . .",
                ". . . . . . . . .",
                ". . . . B . . . .",
                ". . . B W . . . .",
                ". . . . . . . . .",
                ". . . . . . . . .",
                ". . . . . . . . .",
                ". . . . . . . . .",
                ". . . . . . . . .")

        val result = board.play(5, 3)

        assertThat(result.toString(), eq("atari"))
    }

    @Test
    fun testSuicide() {
        val board = Board(9)
        board.setup(
                ". . . . . . . . .",
                ". . . . . . . . .",
                ". . . . W . . . .",
                ". . . W . W . . .",
                ". . . . W . . . .",
                ". . . . . . . . .",
                ". . . . . . . . .",
                ". . . . . . . . .",
                ". . . . . . . . .")

        val result = board.play(4, 3)

        assertThat(result.toString(), eq("suicide"))
    }

    @Test
    fun testGetLiberties() {
        val board = Board(9)
        board.setup(
                "B . . B . . . . .",
                ". . . . . B . . .",
                ". . . . . . . . .",
                ". . B B . B B B .",
                "B . . . . B . B .",
                "B B . . . B B B .",
                ". B B . . . . . .",
                ". . B B . . . . .",
                ". . . B . . . . .")

        assertThat(board.getLiberties(0, 0), eq(2))
        assertThat(board.getLiberties(3, 0), eq(3))
        assertThat(board.getLiberties(5, 1), eq(4))
        assertThat(board.getLiberties(2, 3), eq(6))
        assertThat(board.getLiberties(5, 3), eq(13))
        assertThat(board.getLiberties(3, 8), eq(9))
    }

    @Test
    fun testCapture() {
        val board = Board(9)
        board.setup(
                ". . . . . . . . .",
                ". . . . . . . . .",
                ". . . . . . . . .",
                ". . . . B . . . .",
                ". . . B W . . . .",
                ". . . . B . . . .",
                ". . . . . . . . .",
                ". . . . . . . . .",
                ". . . . . . . . .")

        val result = board.play(5, 4)

        assertThat(result.toString(), eq("captured 1"))
        assertThat(board.getCaptured(Player.BLACK), eq(1))
        assertThat(board.getCaptured(Player.WHITE), eq(0))
        assertThat(board.toString(), eq(""
                + ". . . . . . . . .\n"
                + ". . . . . . . . .\n"
                + ". . . . . . . . .\n"
                + ". . . . B . . . .\n"
                + ". . . B . B . . .\n"
                + ". . . . B . . . .\n"
                + ". . . . . . . . .\n"
                + ". . . . . . . . .\n"
                + ". . . . . . . . .\n"))
    }

    @Test
    fun testCaptureInKo() {
        val board = Board(9)
        board.setup(
                ". . . . . . . . .",
                ". . . . . . . . .",
                ". . . . . B . . .",
                ". . . . B W . . .",
                ". . . B W . W . .",
                ". . . . B W . . .",
                ". . . . . . . . .",
                ". . . . . . . . .",
                ". . . . . . . . .")

        val result = board.play(5, 4)

        assertThat(result.toString(), eq("atari, selfAtari, captured 1"))
    }

    @Test
    fun testSelfAtari() {
        val board = Board(9)
        board.setup(
                ". . . . . . . W .",
                ". . . . . . W B W",
                ". . . . . . W B B",
                ". . . . . . . W .",
                ". . . . . . . . .",
                ". . . . . . . . .",
                ". . . . . . . . .",
                ". . . . . . . . .",
                ". . . . . . . . .")

        val result = board.play(8, 0)

        assertThat(result.toString(), eq("atari, selfAtari, captured 1"))
    }

    @Test
    fun testKo() {
        val board = Board(9)
        board.setup(
                ". . . . . . . . .",
                ". . . . . . . . .",
                ". . . . . . . . .",
                ". . . W B . . . .",
                ". . W . . B . . .",
                ". . . W B . . . .",
                ". . . . . . . . .",
                ". . . . . . . . .",
                ". . . . . . . . .")

        val result = board.play(3, 4)

        assertThat(result.toString(), eq("selfAtari"))

        val capture = board.play(4, 4)

        assertThat(capture.toString(), eq("selfAtari, captured 1"))

        val ko = board.play(3, 4)

        assertThat(ko.toString(), eq("ko"))
    }

    @Test
    fun testStayInAtari() {
        val board = Board(9)
        board.setup(
                ". . . . . . . W B",
                ". . . . . . . W .",
                ". . . . . . . W .",
                ". . . . . . . . .",
                ". . . . . . . . .",
                ". . . . . . . . .",
                ". . . . . . . . .",
                ". . . . . . . . .",
                ". . . . . . . . .")

        val result = board.play(8, 1)

        assertThat(result.toString(), eq("ok"))
    }
}
