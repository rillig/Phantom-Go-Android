package de.roland_illig.android.phantomgo

import android.content.Context
import de.roland_illig.phantomgo.Game
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class Persistence {
    companion object {
        fun load(ctx: Context): Game {
            try {
                ctx.openFileInput("state").use {
                    ObjectInputStream(it).use {
                        return it.readObject() as Game
                    }
                }
            } catch (e: Exception) {
                return Game()
            }
        }

        fun save(ctx: Context, state: Game) {
            ctx.openFileOutput("state", Context.MODE_PRIVATE).use {
                ObjectOutputStream(it).use {
                    it.writeObject(state)
                }
            }
        }
    }
}
