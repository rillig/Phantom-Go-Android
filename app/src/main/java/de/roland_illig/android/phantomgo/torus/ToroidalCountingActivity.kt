package de.roland_illig.android.phantomgo.torus

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import de.roland_illig.android.phantomgo.Persistence
import de.roland_illig.android.phantomgo.R

/**
 * After a game of Toroidal Go has ended,
 * the players mark and remove the dead stones and count the points.
 */
class ToroidalCountingActivity : AppCompatActivity() {

    private lateinit var state: ToroidalState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toroidal_counting)
    }

    override fun onResume() {
        super.onResume()
        val boardView = findViewById<ToroidalCountingBoardView>(R.id.countingBoard)
        state = Persistence.loadToroidalGo(this)
        boardView.configure(state)
    }

    override fun onPause() {
        super.onPause()
        Persistence.saveToroidalGo(this, state)
    }

    fun onFinishClick(view: View) {
        state = ToroidalState()
        finish()
    }

    companion object {
        fun start(ctx: Context) =
            ctx.startActivity(Intent(ctx, ToroidalCountingActivity::class.java))
    }
}
