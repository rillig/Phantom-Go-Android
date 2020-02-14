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

        assertThat("$result").isEqualTo("atari")
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

        assertThat("$result").isEqualTo("suicide")
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

        fun liberties(x: Int, y: Int) = board.getLiberties(Intersection(x, y))

        assertThat(liberties(0, 0)).isEqualTo(2)
        assertThat(liberties(3, 0)).isEqualTo(3)
        assertThat(liberties(5, 1)).isEqualTo(4)
        assertThat(liberties(2, 3)).isEqualTo(6)
        assertThat(liberties(5, 3)).isEqualTo(13)
        assertThat(liberties(3, 8)).isEqualTo(9)
        assertThat(liberties(6, 8)).isEqualTo(1)

        assertThatThrownBy { liberties(1, 0) }
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

        assertThat("$result").isEqualTo("captured 1")
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

            assertThat("$result").isEqualTo("captured 48")
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

            assertThat("$result").isEqualTo("captured 32")
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

        assertThat("$result").isEqualTo("atari, selfAtari, captured 1")
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

        assertThat("$result").isEqualTo("atari, selfAtari, captured 1")
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

        assertThat("$result").isEqualTo("selfAtari")

        val capture = board.play(4, 4)

        assertThat("$capture").isEqualTo("selfAtari, captured 1")

        val ko = board.play(3, 4)

        assertThat("$ko").isEqualTo("ko")
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

        assertThat("$result").isEqualTo("ok")
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

        assertThat("$result").isEqualTo("selfAtari, captured 1")
    }

    @Test
    fun `electric go, basic moves`() {
        val board = Board(9)
        board.rules = Rules.Electric
        board.setup(
            "+ + + + + + + + +",
            "+ + + + + + + + +",
            "+ + + + O + + + +",
            "+ + + + + + + + +",
            "+ + X + + + X X +",
            "+ + + + + + + + +",
            "+ + + + O + + + +",
            "+ + + + + + + + +",
            "+ + + + O + + + +"
        )

        val result = board.play(4, 4)

        assertThat("$result").isEqualTo("ok")
        assertThat(board.toStringLines()).containsExactly(
            "+ + + + + + + + +",
            "+ + + + + + + + +",
            "+ + + + + + + + +",
            "+ + + + O + + + +",
            "X + + + X + X X +",
            "+ + + + O + + + +",
            "+ + + + + + + + +",
            "+ + + + + + + + +",
            "+ + + + O + + + +"
        )
    }

    @Test
    fun `electric go, capturing order and other subtleties`() {
        val board = Board(9)
        board.rules = Rules.Electric
        board.setup(
            "+ + + O X X X X X",
            "+ + + O X O O O X",
            "+ + + O X O + O X",
            "+ + + O X O + O X",
            "+ + + O + + O X O",
            "+ + + + + X + X O",
            "+ + + + + + + X O",
            "+ + + + + + X X O",
            "+ + + + X O O O O"
        )

        val result = board.play(5, 4)
        //
        // First, the stone at 3,4 is attracted to 4,4,
        // and at the same time, the stone at 5,5 is pushed to 5,7.
        //
        // Then, all captured opponent groups (white) are removed.
        // After that, all captured own groups (black) are removed
        // (none in this case, since the white group has been removed before).

        assertThat("$result").isEqualTo("selfAtari, captured 8")
        assertThat(board.toStringLines()).containsExactly(
            "+ + + O X X X X X",
            "+ + + O X O O O X",
            "+ + + O X O + O X",
            "+ + + O X O + O X",
            "+ + + + O X O X +",
            "+ + + + + + + X +",
            "+ + + + + + + X +",
            "+ + + + + X X X +",
            "+ + + + X + + + +"
        )
    }

    @Test
    fun `electric go, self-atari`() {
        val board = Board(5)
        board.rules = Rules.Electric
        board.setup(
            "+ + O + +",
            "+ + + + +",
            "+ + + + O",
            "+ + + + +",
            "+ + O + +"
        )

        val result = board.play(2, 2)

        assertThat("$result").isEqualTo("selfAtari")
        assertThat(board.toStringLines()).containsExactly(
            "+ + + + +",
            "+ + O + +",
            "+ + X O +",
            "+ + O + +",
            "+ + + + +"
        )
    }

    @Test
    fun `electric go, self-capture`() {
        val board = Board(5)
        board.rules = Rules.Electric
        board.setup(
            "+ + O + +",
            "+ + + + +",
            "O + + + O",
            "+ + + + +",
            "+ + O + +"
        )

        val result = board.play(2, 2)

        // If there should ever be someone who wants to play phantom electric go,
        // the referee result should be extended to say "self-captured 1" here.
        assertThat("$result").isEqualTo("captured 1")
        assertThat(board.toStringLines()).containsExactly(
            "+ + + + +",
            "+ + O + +",
            "+ O + O +",
            "+ + O + +",
            "+ + + + +"
        )
        assertThat(board.getCaptured(Player.BLACK)).isEqualTo(1)
        assertThat(board.getCaptured(Player.WHITE)).isEqualTo(0)
    }

    operator fun Board.get(x: Int, y: Int) = this[Intersection(x, y)]

    private fun Board.play(x: Int, y: Int): RefereeResult = play(Intersection(x, y))
}
