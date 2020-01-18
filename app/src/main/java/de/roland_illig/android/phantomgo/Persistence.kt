package de.roland_illig.android.phantomgo

import android.content.Context
import de.roland_illig.phantomgo.Game
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

object Persistence {
    fun load(ctx: Context): Game {
        try {
            ctx.openFileInput("state").use { file ->
                ObjectInputStream(file).use { stream ->
                    return stream.readObject() as Game
                }
            }
        } catch (e: Exception) {
            return Game()
        }
    }

    fun save(ctx: Context, state: Game) {
        ctx.openFileOutput("state", Context.MODE_PRIVATE).use { file ->
            ObjectOutputStream(file).use { stream ->
                stream.writeObject(state)
            }
        }
    }
}
