package de.roland_illig.phantomgo

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test

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

        assertThat(result.toString()).isEqualTo("atari")
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

        assertThat(result.toString()).isEqualTo("suicide")
    }

    @Test
    fun testGetLiberties() {
        val board = Board(9)
        board.setup(
                "X + + X + + X + +",
                "+ + + + + X + + +",
                "+ + + + + + + + +",
                "+ + X X + X X X +",
                "X + + + + X + X +",
                "X X + + + X X X +",
                "+ X X + + + + + +",
                "+ + X X + + O + +",
                "+ + + X + O X + +")

        assertThat(board.getLiberties(0, 0)).isEqualTo(2)
        assertThat(board.getLiberties(3, 0)).isEqualTo(3)
        assertThat(board.getLiberties(5, 1)).isEqualTo(4)
        assertThat(board.getLiberties(2, 3)).isEqualTo(6)
        assertThat(board.getLiberties(5, 3)).isEqualTo(13)
        assertThat(board.getLiberties(3, 8)).isEqualTo(9)
        assertThat(board.getLiberties(6, 8)).isEqualTo(1)

        assertThatThrownBy { board.getLiberties(1, 0) }
                .isInstanceOf(KotlinNullPointerException::class.java)
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

        assertThat(result.toString()).isEqualTo("captured 1")
        assertThat(board.getCaptured(Player.BLACK)).isEqualTo(1)
        assertThat(board.getCaptured(Player.WHITE)).isEqualTo(0)
        assertThat(board.toString()).isEqualTo(""
                + "+ + + + + + + + +\n"
                + "+ + + + + + + + +\n"
                + "+ + + + + + + + +\n"
                + "+ + + + X + + + +\n"
                + "+ + + X + X + + +\n"
                + "+ + + + X + + + +\n"
                + "+ + + + + + + + +\n"
                + "+ + + + + + + + +\n"
                + "+ + + + + + + + +\n")
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

            assertThat(result.toString()).isEqualTo("captured 48")
            assertThat(board.getCaptured(Player.BLACK)).isEqualTo(48)
            assertThat(board.getCaptured(Player.WHITE)).isEqualTo(0)
            assertThat(board.toString()).isEqualTo(""
                    + "+ + + + + + + + +\n"
                    + "X X X X X X X X +\n"
                    + "+ + + + + + + X +\n"
                    + "+ X X X X X + X +\n"
                    + "+ X + + X X + X +\n"
                    + "+ X + X X X + X +\n"
                    + "+ X + + + + + X +\n"
                    + "+ X X X X X X X +\n"
                    + "+ + + + + + + + +\n")
        }

        board.copy().also { board ->
            board.turn = Player.WHITE
            val result = board.play(4, 4)

            assertThat(result.toString()).isEqualTo("captured 32")
            assertThat(board.getCaptured(Player.BLACK)).isEqualTo(0)
            assertThat(board.getCaptured(Player.WHITE)).isEqualTo(32)
            assertThat(board.toString()).isEqualTo(""
                    + "O O O O O O O O O\n"
                    + "+ + + + + + + + O\n"
                    + "O O O O O O O + O\n"
                    + "O + + + + + O + O\n"
                    + "O + O O O + O + O\n"
                    + "O + O + + + O + O\n"
                    + "O + O O O O O + O\n"
                    + "O + + + + + + + O\n"
                    + "O O O O O O O O O\n")
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

        assertThat(result.toString()).isEqualTo("atari, selfAtari, captured 1")
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

        assertThat(result.toString()).isEqualTo("atari, selfAtari, captured 1")
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

        assertThat(result.toString()).isEqualTo("selfAtari")

        val capture = board.play(4, 4)

        assertThat(capture.toString()).isEqualTo("selfAtari, captured 1")

        val ko = board.play(3, 4)

        assertThat(ko.toString()).isEqualTo("ko")
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

        assertThat(result.toString()).isEqualTo("ok")
    }
}
