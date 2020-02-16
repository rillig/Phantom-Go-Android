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
        val result = playMoves(state, "c1 b1 b2 a1 c2 a2 a3")

        assertThat("$result").isEqualTo("captured 3")

        // White still thinks his stones are on the board.
        assertThat(state.whiteBoard.toString()).isEqualTo(""
                + "+ + + + + + + + +\n"
                + "+ + + + + + + + +\n"
                + "+ + + + + + + + +\n"
                + "+ + + + + + + + +\n"
                + "+ + + + + + + + +\n"
                + "+ + + + + + + + +\n"
                + "+ + + + + + + + +\n"
                + "O + + + + + + + +\n"
                + "O O + + + + + + +\n")

        // Since the referee said "Black captured 3 stones" and a referee
        // looking at Black's board whould have said the same, it was
        // played there also, removing the 3 white stones.
        assertThat(state.blackBoard.toString()).isEqualTo(""
                + "+ + + + + + + + +\n"
                + "+ + + + + + + + +\n"
                + "+ + + + + + + + +\n"
                + "+ + + + + + + + +\n"
                + "+ + + + + + + + +\n"
                + "+ + + + + + + + +\n"
                + "X + + + + + + + +\n"
                + "+ X X + + + + + +\n"
                + "+ + X + + + + + +\n")
    }

    private fun playMoves(state: PhantomState, moves: String): RefereeResult {
        var lastResult: RefereeResult? = null
        for (move in moves.split(" ")) {
            val x = "abcdefghijklmnopqrstuvwxyz".indexOf(move[0])
            val y = state.size - Integer.parseInt(move.substring(1))
            val result = state.play(x, y)
            assertThat(result is RefereeResult.Ok).withFailMessage("invalid move %s: %s", move, result).isTrue()
            state.finishMove()
            lastResult = result
        }
        return lastResult!!
    }
}
