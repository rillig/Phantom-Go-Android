package de.roland_illig.phantomgo

import org.hamcrest.CoreMatchers.nullValue
import org.junit.Assert.assertThat
import org.junit.Test
import org.hamcrest.CoreMatchers.`is` as eq

class GameTest {

    @Test
    fun testShortGame() {
        val game = Game()

        assertThat(game.isInitial, eq(true))
        assertThat(game.turn, eq(Player.BLACK))
        assertThat(game.isReadyToHandOver(), eq(false))

        game.play(4, 4)

        assertThat(game.isInitial, eq(false))
        assertThat(game.turn, eq(Player.BLACK))
        assertThat(game.isReadyToHandOver(), eq(true))

        // At this point, Black may want to edit his board to add opponent stones.

        game.finishMove()

        assertThat(game.isInitial, eq(false))
        assertThat(game.turn, eq(Player.WHITE))

        game.pass()

        assertThat(game.turn, eq(Player.WHITE))

        game.finishMove()

        assertThat(game.turn, eq(Player.BLACK))

        game.pass()

        assertThat(game.isGameOver, eq(true))

        assertThat(game.countingBoard().count().toString(), eq("black=80+0, white=0+0"))
    }

    @Test
    fun testCapture() {
        val game = Game()
        val result = playMoves(game, "c1 b1 b2 a1 c2 a2 a3")

        assertThat(result.toString(), eq("captured 3"))

        // White still thinks his stones are on the board.
        assertThat(game.whiteBoard.toString(), eq(""
                + "+ + + + + + + + +\n"
                + "+ + + + + + + + +\n"
                + "+ + + + + + + + +\n"
                + "+ + + + + + + + +\n"
                + "+ + + + + + + + +\n"
                + "+ + + + + + + + +\n"
                + "+ + + + + + + + +\n"
                + "O + + + + + + + +\n"
                + "O O + + + + + + +\n"))

        // Since the referee said "Black captured 3 stones" and a referee
        // looking at Black's board whould have said the same, it was
        // played there also, removing the 3 white stones.
        assertThat(game.blackBoard.toString(), eq(""
                + "+ + + + + + + + +\n"
                + "+ + + + + + + + +\n"
                + "+ + + + + + + + +\n"
                + "+ + + + + + + + +\n"
                + "+ + + + + + + + +\n"
                + "+ + + + + + + + +\n"
                + "X + + + + + + + +\n"
                + "+ X X + + + + + +\n"
                + "+ + X + + + + + +\n"))
    }

    private fun playMoves(game: Game, moves: String): RefereeResult {
        var lastResult: RefereeResult? = null
        for (move in moves.split(" ")) {
            val x = "abcdefghijklmnopqrstuvwxyz".indexOf(move[0])
            val y = game.size - Integer.parseInt(move.substring(1))
            val result = game.play(x, y)
            assertThat("invalid move $move: $result", result.invalidReason, nullValue())
            game.finishMove()
            lastResult = result
        }
        return lastResult!!
    }
}
