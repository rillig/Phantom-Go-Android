package de.roland_illig.phantomgo

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class BoardPlayTest {

    private val board = Board(9)

    @Test
    fun `independent moves`() {
        assertPlay(4, 4, "ok")
        assertPlay(4, 5, "ok")
        assertPlay(5, 5, "ok")
        assertPlay(5, 4, "ok")
    }

    @Test
    fun `ladder at the edge`() {
        assertPlay(0, 0, "ok")
        assertPlay(0, 1, "atari")
        assertPlay(1, 0, "ok")
        assertPlay(1, 1, "atari")
        assertPlay(5, 5, "ok")
        assertPlay(2, 0, "captured 2")
    }

    @Test
    fun `self-atari`() {
        assertPlay(0, 1, "ok")
        assertPlay(0, 0, "selfAtari")
    }

    @Test
    fun `ko at the edge`() {
        assertPlay(3, 0, "ok")
        assertPlay(6, 0, "ok")
        assertPlay(4, 1, "ok")
        assertPlay(5, 1, "ok")
        assertPlay(5, 0, "selfAtari")
        assertPlay(4, 0, "selfAtari, captured 1")

        assertBoard(
                "+ + + X O + O + +",
                "+ + + + X O + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +")
        assertPlay(5, 0, "ko")
        assertBoard(
                "+ + + X O + O + +",
                "+ + + + X O + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +")

        assertPlay(5, 5, "ok")
        assertPlay(5, 6, "ok")
        assertPlay(5, 0, "selfAtari, captured 1")

        assertPlay(5, 7, "ok")
        assertPlay(5, 0, "ownStone")
        assertPlay(4, 0, "ok")
    }

    @Test
    fun `ko with self-atari in the corner`() {
        assertPlay(1, 0, "ok")
        assertPlay(1, 1, "ok")
        assertPlay(0, 1, "ok")
        assertPlay(0, 2, "atari")
        assertPlay(1, 2, "atari")
        assertBoard(
                "+ X + + + + + + +",
                "X O + + + + + + +",
                "O X + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +")
        assertPlay(0, 0, "atari, selfAtari, captured 1")
        assertBoard(
                "O X + + + + + + +",
                "+ O + + + + + + +",
                "O X + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +")
        assertPlay(0, 1, "ko")
        assertBoard(
                "O X + + + + + + +",
                "+ O + + + + + + +",
                "O X + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +")
        assertPlay(2, 1, "atari")
        assertPlay(0, 1, "ok")
        assertPlay(0, 3, "captured 4")
        assertBoard(
                "+ X + + + + + + +",
                "+ + X + + + + + +",
                "+ X + + + + + + +",
                "X + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +")
    }

    @Test
    fun `single stone suicide in the corner`() {
        assertPlay(1, 0, "ok")
        assertPlay(1, 1, "ok")
        assertPlay(0, 1, "ok")
        assertPlay(0, 0, "suicide")
        assertBoard(
                "+ X + + + + + + +",
                "X O + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +")
    }

    @Test
    fun `two stone suicide in the corner`() {
        assertPlay(0, 1, "ok")
        assertPlay(0, 0, "selfAtari")
        assertPlay(2, 0, "ok")
        assertPlay(2, 1, "ok")
        assertPlay(1, 1, "ok")
        assertPlay(1, 0, "suicide")
        assertBoard(
                "O + X + + + + + +",
                "X X O + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +")
    }

    @Test
    fun `two stone suicide in the other corner`() {
        assertPlay(8, 7, "ok")
        assertPlay(8, 8, "selfAtari")
        assertPlay(6, 8, "ok")
        assertPlay(6, 7, "ok")
        assertPlay(7, 7, "ok")
        assertPlay(7, 8, "suicide")
        assertBoard(
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + + + +",
                "+ + + + + + O X X",
                "+ + + + + + X + O")
    }

    @Test
    fun `remove spiral of stones`() {
        setupBoard(
                "O O O O O O O O O",
                "X X X X X X X X O",
                "X O O O O O O X O",
                "X O X X X X O X O",
                "X O X O + X O X O",
                "X O X O X X O X O",
                "X O X O O O O X O",
                "X O X X X X X X O",
                "X O O O O O O O O")
        assertPlay(4, 4, "captured 44")
        assertBoard(
                "+ + + + + + + + +",
                "X X X X X X X X +",
                "X + + + + + + X +",
                "X + X X X X + X +",
                "X + X + X X + X +",
                "X + X + X X + X +",
                "X + X + + + + X +",
                "X + X X X X X X +",
                "X + + + + + + + +")
    }

    @Test
    fun `simple capturing in the corner`() {
        assertPlay(0, 1, "ok")
        assertPlay(0, 0, "selfAtari")
        assertPlay(1, 0, "captured 1")
    }

    /* h1. Helper Methods */

    private fun assertPlay(x: Int, y: Int, expectedResult: String) {
        val result = board.play(x, y)
        assertThat(result.toString()).isEqualTo(expectedResult)
    }

    private fun assertBoard(vararg expectedRows: String) {
        assertThat(board.toString())
                .isEqualTo(expectedRows.reduce { acc, s -> acc + "\n" + s } + "\n")
    }

    private fun setupBoard(vararg setupRows: String) {
        board.setup(*setupRows)
    }
}
