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

        game.play(4, 4)

        assertThat(game.turn, eq(Player.BLACK))

        game.finishMove()

        assertThat(game.isInitial, eq(false))
        assertThat(game.turn, eq(Player.WHITE))

        game.pass()

        assertThat(game.turn, eq(Player.WHITE))

        game.finishMove()

        assertThat(game.turn, eq(Player.BLACK))

        game.pass()

        assertThat(game.isGameOver, eq(true))
    }
}
