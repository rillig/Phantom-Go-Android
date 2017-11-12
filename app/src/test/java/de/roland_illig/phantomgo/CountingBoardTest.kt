package de.roland_illig.phantomgo

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class CountingBoardTest {

    @Test
    fun testCountSimple() {
        val board = Board(9)
        board.setup(
                "+ + X + + O + + +",
                "+ + X + + O + + +",
                "+ + X + + O + + +",
                "+ + X + + O + + +",
                "+ + X + + O + + +",
                "+ + X + + O + + +",
                "+ + X + + O + + +",
                "+ + X + + O + + +",
                "+ + X + + O + + +")

        val countingBoard = CountingBoard(board)

        assertThat(countingBoard.regionsToString()).isEqualTo(""
                + "1 1 2 3 3 4 5 5 5\n"
                + "1 1 2 3 3 4 5 5 5\n"
                + "1 1 2 3 3 4 5 5 5\n"
                + "1 1 2 3 3 4 5 5 5\n"
                + "1 1 2 3 3 4 5 5 5\n"
                + "1 1 2 3 3 4 5 5 5\n"
                + "1 1 2 3 3 4 5 5 5\n"
                + "1 1 2 3 3 4 5 5 5\n"
                + "1 1 2 3 3 4 5 5 5\n")

        val result = countingBoard.count()
        assertThat(result.blackTerritory).isEqualTo(18)
        assertThat(result.whiteTerritory).isEqualTo(27)
        assertThat(result.blackCaptured).isEqualTo(0)
        assertThat(result.whiteCaptured).isEqualTo(0)
    }

    @Test
    fun testCountBeforeMarkingDeadStones() {
        val board = Board(9)
        board.setup(
                "X + + + O O X + +",
                "X O + O O X X + +",
                "X O + O X X X + +",
                "O O O O O X + + +",
                "X X O X X X + + +",
                "+ X X O O X X X X",
                "X X O + O O X O O",
                "+ X O O + O O X +",
                "+ X O + + + O X +")

        val countingBoard = CountingBoard(board)

        assertThat(countingBoard.regionsToString()).isEqualTo(""
                + "1 2 2 2 3 3 4 5 5\n"
                + "1 3 2 3 3 4 4 5 5\n"
                + "1 3 2 3 4 4 4 5 5\n"
                + "3 3 3 3 3 4 5 5 5\n"
                + "6 6 3 4 4 4 5 5 5\n"
                + "7 6 6 8 8 4 4 4 4\n"
                + "6 6 9 A 8 8 4 B B\n"
                + "C 6 9 9 D 8 8 E F\n"
                + "C 6 9 D D D 8 E F\n")

        val result = countingBoard.count()
        assertThat(result.toString()).isEqualTo("black=15+0, white=5+0")
    }

    @Test
    fun testCountAfterMarkingDeadStones() {
        val board = Board(9)
        board.setup(
                "X + + + O O X + +",
                "X O + O O X X + +",
                "X O + O X X X + +",
                "O O O O O X + + +",
                "X X O X X X + + +",
                "+ X X O O X X X X",
                "X X O + O O X O O",
                "+ X O O + O O X +",
                "+ X O + + + O X +")

        val countingBoard = CountingBoard(board)
        countingBoard.toggleDead(0, 0)
        countingBoard.toggleDead(8, 6)

        val result = countingBoard.count()
        assertThat(result.toString()).isEqualTo("black=19+2, white=13+3")

        assertThat(countingBoard.toString()).isEqualTo(""
                + "# o o o O O X x x\n"
                + "# O o O O X X x x\n"
                + "# O o O X X X x x\n"
                + "O O O O O X x x x\n"
                + "X X O X X X x x x\n"
                + "x X X O O X X X X\n"
                + "X X O o O O X # #\n"
                + "x X O O o O O X x\n"
                + "x X O o o o O X x\n")
    }

    @Test
    fun testBoardViewAfterMarkingDeadStones() {
        val board = Board(9)
        board.setup(
                "X + + + O O X + +",
                "X O + O O X X + +",
                "X O + O X X X + +",
                "O O O O O X + + +",
                "X X O X X X + + +",
                "+ X X O O X X X X",
                "X X O + O O X O O",
                "+ X O O + O O X +",
                "+ X O + + + O X +")

        val countingBoard = CountingBoard(board)
        countingBoard.toggleDead(0, 0)
        countingBoard.toggleDead(8, 6)

        val result = countingBoard.count()
        assertThat(result.toString()).isEqualTo("black=19+2, white=13+3")
    }

    @Test
    fun testToggleDead() {
        val board = Board(9)
        board.setup(
                "O + + X + + + + O",
                "+ + X + + + + O +",
                "+ X + + + + O + +",
                "X + + + + O + + +",
                "+ + + + O + + + +",
                "+ + + O + + + + X",
                "+ + O + + + + X +",
                "+ O + + + + X + +",
                "O + + + + X + + O")
        val countingBoard = CountingBoard(board)

        countingBoard.toggleDead(0, 8)

        assertThat(countingBoard.toString()).isEqualTo(""
                + "O + + X x x x x #\n"
                + "+ + X x x x x # x\n"
                + "+ X x x x x # x x\n"
                + "X x x x x # x x x\n"
                + "x x x x # x x x x\n"
                + "x x x # x x x x X\n"
                + "x x # x x x x X +\n"
                + "x # x x x x X + +\n"
                + "# x x x x X + + O\n")

        countingBoard.toggleDead(0, 8)

        assertThat(countingBoard.toString()).isEqualTo(""
                + "O + + X + + + + O\n"
                + "+ + X + + + + O +\n"
                + "+ X + + + + O + +\n"
                + "X + + + + O + + +\n"
                + "+ + + + O + + + +\n"
                + "+ + + O + + + + X\n"
                + "+ + O + + + + X +\n"
                + "+ O + + + + X + +\n"
                + "O + + + + X + + O\n")
    }
}
