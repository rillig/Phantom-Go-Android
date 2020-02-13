package de.roland_illig.phantomgo

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test

class BoardTest {

    @Test
    fun atari() {
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
            "+ + + + + + + + +"
        )

        val result = board.play(5, 3)

        assertThat(result.toString()).isEqualTo("atari")
    }

    @Test
    fun suicide() {
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
            "+ + + + + + + + +"
        )

        val result = board.play(4, 3)

        assertThat(result.toString()).isEqualTo("suicide")
    }

    @Test
    fun getLiberties() {
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
            "+ + + X + O X + +"
        )

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
    fun `capture one stone`() {
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
            "+ + + + + + + + +"
        )

        val result = board.play(5, 4)

        assertThat(result.toString()).isEqualTo("captured 1")
        assertThat(board.getCaptured(Player.BLACK)).isEqualTo(1)
        assertThat(board.getCaptured(Player.WHITE)).isEqualTo(0)
        assertThat(board.toStringLines()).containsExactly(
            "+ + + + + + + + +",
            "+ + + + + + + + +",
            "+ + + + + + + + +",
            "+ + + + X + + + +",
            "+ + + X + X + + +",
            "+ + + + X + + + +",
            "+ + + + + + + + +",
            "+ + + + + + + + +",
            "+ + + + + + + + +"
        )
    }

    @Test
    fun `capture snake`() {
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
            "O O O O O O O O O"
        )

        board.copy().apply {
            val result = play(4, 4)

            assertThat(result.toString()).isEqualTo("captured 48")
            assertThat(getCaptured(Player.BLACK)).isEqualTo(48)
            assertThat(getCaptured(Player.WHITE)).isEqualTo(0)
            assertThat(toStringLines()).containsExactly(
                "+ + + + + + + + +",
                "X X X X X X X X +",
                "+ + + + + + + X +",
                "+ X X X X X + X +",
                "+ X + + X X + X +",
                "+ X + X X X + X +",
                "+ X + + + + + X +",
                "+ X X X X X X X +",
                "+ + + + + + + + +"
            )
        }

        board.copy().apply {
            turn = Player.WHITE
            val result = play(4, 4)

            assertThat(result.toString()).isEqualTo("captured 32")
            assertThat(getCaptured(Player.BLACK)).isEqualTo(0)
            assertThat(getCaptured(Player.WHITE)).isEqualTo(32)
            assertThat(toStringLines()).containsExactly(
                "O O O O O O O O O",
                "+ + + + + + + + O",
                "O O O O O O O + O",
                "O + + + + + O + O",
                "O + O O O + O + O",
                "O + O + + + O + O",
                "O + O O O O O + O",
                "O + + + + + + + O",
                "O O O O O O O O O"
            )
        }
    }

    @Test
    fun `capture in ko`() {
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
            "+ + + + + + + + +"
        )

        val result = board.play(5, 4)

        assertThat(result.toString()).isEqualTo("atari, selfAtari, captured 1")
    }

    @Test
    fun `self-atari`() {
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
            "+ + + + + + + + +"
        )

        val result = board.play(8, 0)

        assertThat(result.toString()).isEqualTo("atari, selfAtari, captured 1")
    }

    @Test
    fun ko() {
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
            "+ + + + + + + + +"
        )

        val result = board.play(3, 4)

        assertThat(result.toString()).isEqualTo("selfAtari")

        val capture = board.play(4, 4)

        assertThat(capture.toString()).isEqualTo("selfAtari, captured 1")

        val ko = board.play(3, 4)

        assertThat(ko.toString()).isEqualTo("ko")
    }

    @Test
    fun `stay in atari`() {
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
            "+ + + + + + + + +"
        )

        val result = board.play(8, 1)

        assertThat(result.toString()).isEqualTo("ok")
    }

    @Test
    fun `pass then continue playing`() {
        val board = Board(9)
        board.play(0, 0)
        board.pass()
        board.play(1, 1)
        board.pass()

        assertThat(board.gameOver).isFalse()

        board.pass()

        assertThat(board.gameOver).isTrue()
    }

    /**
     * Until 2020-02-13, saving the state had thrown a NotSerializableException
     * because the Intersection was not serializable.
     *
     * Since [Board.copy] is implemented using serialization, this test ensures
     * that all involved classes are indeed serializable.
     */
    @Test
    fun `copy board via serialization`() {
        Board(9).copy()
    }

    @Test
    fun `toroidal, wrap-around at the edges`() {
        val board = Board(9)
        board.rules = Rules.Toroidal
        board.setup(
            "O + + + + + + + X",
            "+ + + + + + + + +",
            "+ + + + + + + + +",
            "+ + + + + + + + +",
            "+ + + + + + + + +",
            "+ + + + + + + + +",
            "+ + + + + + + + +",
            "O + + + + + + + X",
            "+ O + + + + + X O"
        )

        val result = board.play(0, 8)

        assertThat(result.toString()).isEqualTo("selfAtari, captured 1")
    }
}
