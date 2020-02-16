package de.roland_illig.phantomgo

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class PhantomStateTest {

    @Test
    fun testShortGame() {
        val state = PhantomState()

        assertThat(state.isInitial).isEqualTo(true)
        assertThat(state.turn).isEqualTo(Player.BLACK)
        assertThat(state.isReadyToHandOver()).isEqualTo(false)

        state.play(4, 4)

        assertThat(state.isInitial).isEqualTo(false)
        assertThat(state.turn).isEqualTo(Player.BLACK)
        assertThat(state.isReadyToHandOver()).isEqualTo(true)

        // At this point, Black may want to edit his board to add opponent stones.

        state.finishMove()

        assertThat(state.isInitial).isEqualTo(false)
        assertThat(state.turn).isEqualTo(Player.WHITE)

        state.pass()

        assertThat(state.turn).isEqualTo(Player.WHITE)

        state.finishMove()

        assertThat(state.turn).isEqualTo(Player.BLACK)

        state.pass()

        assertThat(state.isGameOver).isEqualTo(true)

        assertThat(state.countingBoard().count().toString()).isEqualTo("black=80+0, white=0+0")
    }

    @Test
    fun testCapture() {
        val state = PhantomState()
        val result = state.playMoves("c1 b1 b2 a1 c2 a2 a3")

        assertThat("$result").isEqualTo("captured 3")

        // White still thinks his stones are on the board.
        assertThat(state.whiteBoard.toStringLines()).containsExactly(
            ". . . . . . . . .",
            ". . . . . . . . .",
            ". . . . . . . . .",
            ". . . . . . . . .",
            ". . . . . . . . .",
            ". . . . . . . . .",
            ". . . . . . . . .",
            "O . . . . . . . .",
            "O O . . . . . . ."
        )

        // Since the referee said "Black captured 3 stones" and a referee
        // looking at Black's board would have said the same, it was
        // played there also, removing the 3 white stones.
        assertThat(state.blackBoard.toStringLines()).containsExactly(
            ". . . . . . . . .",
            ". . . . . . . . .",
            ". . . . . . . . .",
            ". . . . . . . . .",
            ". . . . . . . . .",
            ". . . . . . . . .",
            "X . . . . . . . .",
            ". X X . . . . . .",
            ". . X . . . . . ."
        )
    }

    private fun PhantomState.playMoves(moves: String): RefereeResult {
        var lastResult: RefereeResult? = null
        for (move in moves.split(" ")) {
            val x = "abcdefghijklmnopqrstuvwxyz".indexOf(move[0])
            val y = size - Integer.parseInt(move.substring(1))
            val result = play(x, y)
            assertThat(result is RefereeResult.Ok)
                .withFailMessage("invalid move %s: %s", move, result)
                .isTrue()
            finishMove()
            lastResult = result
        }
        return lastResult!!
    }
}
