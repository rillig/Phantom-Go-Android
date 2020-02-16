package de.roland_illig.android.phantomgo.magnet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import de.roland_illig.android.phantomgo.Persistence
import de.roland_illig.android.phantomgo.R

/**
 * After a game of Magnetic Go has ended,
 * the players mark and remove the dead stones and count the points.
 */
class MagneticCountingActivity : AppCompatActivity() {

    private lateinit var state: MagneticState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_magnetic_counting)
    }

    override fun onResume() {
        super.onResume()
        val boardView = findViewById<MagneticCountingBoardView>(R.id.countingBoard)
        state = Persistence.loadMagneticGo(this)
        boardView.configure(state)
    }

    override fun onPause() {
        super.onPause()
        Persistence.saveMagneticGo(this, state)
    }

    fun onFinishClick(view: View) {
        state = MagneticState()
        finish()
    }

    companion object {
        fun start(ctx: Context) =
            ctx.startActivity(Intent(ctx, MagneticCountingActivity::class.java))
    }
}
