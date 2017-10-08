package de.roland_illig.phantomgo

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
}
