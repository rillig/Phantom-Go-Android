package de.roland_illig.android.phantomgo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import de.roland_illig.phantomgo.PhantomState

/** After a game has ended, the players mark and remove the dead stones and count the points. */
class CountingActivity : AppCompatActivity() {

    private lateinit var state: PhantomState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phantom_counting)
    }

    override fun onResume() {
        super.onResume()
        val boardView = findViewById<CountingBoardView>(R.id.countingBoard)
        state = Persistence.loadPhantomGo(this)
        boardView.configure(state)
    }

    override fun onPause() {
        super.onPause()
        Persistence.savePhantomGo(this, state)
    }

    fun onFinishClick(view: View) {
        state = PhantomState()
        finish()
    }

    companion object {
        fun start(ctx: Context) = ctx.startActivity(Intent(ctx, CountingActivity::class.java))
    }
}
