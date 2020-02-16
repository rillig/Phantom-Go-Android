package de.roland_illig.android.phantomgo

import android.content.Context
import de.roland_illig.android.phantomgo.magnet.MagneticState
import de.roland_illig.android.phantomgo.plain.PlainState
import de.roland_illig.android.phantomgo.torus.ToroidalState
import de.roland_illig.phantomgo.PhantomState
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

object Persistence {

    fun loadPhantomGo(ctx: Context) = load(ctx, "state") { PhantomState() }

    fun savePhantomGo(ctx: Context, state: PhantomState) = save(ctx, "state", state)

    fun loadMagneticGo(ctx: Context) = load(ctx, "magnetic go") { MagneticState() }

    fun saveMagneticGo(ctx: Context, state: MagneticState) =
        save(ctx, "magnetic go", state)

    fun loadPlainGo(ctx: Context) = load(ctx, "plain go") { PlainState() }

    fun savePlainGo(ctx: Context, state: PlainState) =
        save(ctx, "plain go", state)

    fun loadToroidalGo(ctx: Context) = load(ctx, "toroidal go") { ToroidalState() }

    fun saveToroidalGo(ctx: Context, state: ToroidalState) =
        save(ctx, "toroidal go", state)

    private fun <T : Serializable> load(ctx: Context, filename: String, def: () -> T): T {

        @Suppress("UNCHECKED_CAST")
        fun <T> cast(obj: Any): T = obj as T

        try {
            ctx.openFileInput(filename).use { file ->
                ObjectInputStream(file).use { stream ->
                    return cast(stream.readObject())
                }
            }
        } catch (e: Exception) {
            return def()
        }
    }

    private fun save(ctx: Context, filename: String, obj: Serializable) {
        ctx.openFileOutput(filename, Context.MODE_PRIVATE).use { file ->
            ObjectOutputStream(file).use { stream ->
                stream.writeObject(obj)
            }
        }
    }
}
