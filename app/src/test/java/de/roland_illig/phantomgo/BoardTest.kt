package de.roland_illig.phantomgo

import org.junit.Assert.assertThat
import org.junit.Test
import org.hamcrest.CoreMatchers.`is` as eq

class BoardTest {

    @Test
    fun testAtari() {
        val board = Board(9)
        board.setup(
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + X + + + +",
                "+ + + X O + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +")

        val result = board.play(5, 3)

        assertThat(result.toString(), eq("atari"))
    }

    @Test
    fun testSuicide() {
        val board = Board(9)
        board.setup(
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + O + + + +",
                "+ + + O + O + + +",
                "+ + + + O + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +")

        val result = board.play(4, 3)

        assertThat(result.toString(), eq("suicide"))
    }

    @Test
    fun testGetLiberties() {
        val board = Board(9)
        board.setup(
                "X + + X + + + + +",
                "+ + + + + X + + +",
                "+ + + + + + + + +",
                "+ + X X + X X X +",
                "X + + + + X + X +",
                "X X + + + X X X +",
                "+ X X + + + + + +",
                "+ + X X + + + + +",
                "+ + + X + + + + +")

        assertThat(board.getLiberties(0, 0), eq(2))
        assertThat(board.getLiberties(3, 0), eq(3))
        assertThat(board.getLiberties(5, 1), eq(4))
        assertThat(board.getLiberties(2, 3), eq(6))
        assertThat(board.getLiberties(5, 3), eq(13))
        assertThat(board.getLiberties(3, 8), eq(9))
    }

    @Test
    fun testCaptureOneStone() {
        val board = Board(9)
        board.setup(
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + X + + + +",
                "+ + + X O + + + +",
                "+ + + + X + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +")

        val result = board.play(5, 4)

        assertThat(result.toString(), eq("captured 1"))
        assertThat(board.getCaptured(Player.BLACK), eq(1))
        assertThat(board.getCaptured(Player.WHITE), eq(0))
        assertThat(board.toString(), eq(""
                + "+ + + + + + + + +\n"
                + "+ + + + + + + + +\n"
                + "+ + + + + + + + +\n"
                + "+ + + + X + + + +\n"
                + "+ + + X + X + + +\n"
                + "+ + + + X + + + +\n"
                + "+ + + + + + + + +\n"
                + "+ + + + + + + + +\n"
                + "+ + + + + + + + +\n"))
    }

    @Test
    fun testCaptureSnake() {
        val board = Board(9)
        board.setup(
                "O O O O O O O O O",
                "X X X X X X X X O",
                "O O O O O O O X O",
                "O X X X X X O X O",
                "O X O O + X O X O",
                "O X O X X X O X O",
                "O X O O O O O X O",
                "O X X X X X X X O",
                "O O O O O O O O O")

        board.copy().also { board ->
            val result = board.play(4, 4)

            assertThat(result.toString(), eq("captured 48"))
            assertThat(board.getCaptured(Player.BLACK), eq(48))
            assertThat(board.getCaptured(Player.WHITE), eq(0))
            assertThat(board.toString(), eq(""
                    + "+ + + + + + + + +\n"
                    + "X X X X X X X X +\n"
                    + "+ + + + + + + X +\n"
                    + "+ X X X X X + X +\n"
                    + "+ X + + X X + X +\n"
                    + "+ X + X X X + X +\n"
                    + "+ X + + + + + X +\n"
                    + "+ X X X X X X X +\n"
                    + "+ + + + + + + + +\n"))
        }

        board.copy().also { board ->
            board.turn = Player.WHITE
            val result = board.play(4, 4)

            assertThat(result.toString(), eq("captured 32"))
            assertThat(board.getCaptured(Player.BLACK), eq(0))
            assertThat(board.getCaptured(Player.WHITE), eq(32))
            assertThat(board.toString(), eq(""
                    + "O O O O O O O O O\n"
                    + "+ + + + + + + + O\n"
                    + "O O O O O O O + O\n"
                    + "O + + + + + O + O\n"
                    + "O + O O O + O + O\n"
                    + "O + O + + + O + O\n"
                    + "O + O O O O O + O\n"
                    + "O + + + + + + + O\n"
                    + "O O O O O O O O O\n"))
        }
    }

    @Test
    fun testCaptureInKo() {
        val board = Board(9)
        board.setup(
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + X + + +",
                "+ + + + X O + + +",
                "+ + + X O + O + +",
                "+ + + + X O + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +")

        val result = board.play(5, 4)

        assertThat(result.toString(), eq("atari, selfAtari, captured 1"))
    }

    @Test
    fun testSelfAtari() {
        val board = Board(9)
        board.setup(
                "+ + + + + + + O +",
                "+ + + + + + O X O",
                "+ + + + + + O X X",
                "+ + + + + + + O +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +")

        val result = board.play(8, 0)

        assertThat(result.toString(), eq("atari, selfAtari, captured 1"))
    }

    @Test
    fun testKo() {
        val board = Board(9)
        board.setup(
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + O X + + + +",
                "+ + O + + X + + +",
                "+ + + O X + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +")

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
                "+ + + + + + + O X",
                "+ + + + + + + O +",
                "+ + + + + + + O +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +")

        val result = board.play(8, 1)

        assertThat(result.toString(), eq("ok"))
    }
}
