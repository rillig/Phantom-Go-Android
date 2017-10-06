package de.roland_illig.android.phantomgo

import android.content.Context
import de.roland_illig.phantomgo.Board
import de.roland_illig.phantomgo.CountingBoard
import java.io.FileNotFoundException
import java.io.InvalidClassException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.WriteAbortedException

class GameState : java.io.Serializable {

    val refereeBoard = Board(9)
    val blackBoard = Board(9)
    val whiteBoard = Board(9)
    var countingBoard: CountingBoard? = null

    companion object {
        private const val serialVersionUID = 1L

        fun load(ctx: Context): GameState {
            try {
                ctx.openFileInput("state").use {
                    ObjectInputStream(it).use {
                        return it.readObject() as GameState
                    }
                }
            } catch (e: FileNotFoundException) {
                return GameState()
            } catch (e: WriteAbortedException) {
                return GameState()
            } catch (e: InvalidClassException) {
                return GameState()
            }
        }

        fun save(ctx: Context, state: GameState) {
            ctx.openFileOutput("state", Context.MODE_PRIVATE).use {
                ObjectOutputStream(it).use {
                    it.writeObject(state)
                }
            }
        }
    }
}
