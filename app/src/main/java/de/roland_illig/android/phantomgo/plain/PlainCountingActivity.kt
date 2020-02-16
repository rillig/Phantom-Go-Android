package de.roland_illig.android.phantomgo.plain

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import de.roland_illig.android.phantomgo.Persistence
import de.roland_illig.android.phantomgo.R

/**
 * After a game of Go has ended,
 * the players mark and remove the dead stones and count the points.
 */
class PlainCountingActivity : AppCompatActivity() {

    private lateinit var state: PlainState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plain_counting)
    }

    override fun onResume() {
        super.onResume()
        val boardView = findViewById<PlainCountingBoardView>(R.id.countingBoard)
        state = Persistence.loadPlainGo(this)
        boardView.configure(state)
    }

    override fun onPause() {
        super.onPause()
        Persistence.savePlainGo(this, state)
    }

    fun onFinishClick(view: View) {
        state = PlainState()
        finish()
    }

    companion object {
        fun start(ctx: Context) =
            ctx.startActivity(Intent(ctx, PlainCountingActivity::class.java))
    }
}
