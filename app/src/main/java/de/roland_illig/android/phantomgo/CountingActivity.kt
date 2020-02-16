package de.roland_illig.android.phantomgo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import de.roland_illig.phantomgo.Game

/** After a game has ended, the players mark and remove the dead stones and count the points. */
class CountingActivity : AppCompatActivity() {

    private lateinit var game: Game

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_counting)
    }

    override fun onResume() {
        super.onResume()
        val game = Persistence.loadPhantomGo(this)
        this.game = game
        findViewById<CountingBoardView>(R.id.countingBoard).configure(game)
    }

    override fun onPause() {
        super.onPause()
        Persistence.savePhantomGo(this, game)
    }

    fun onFinishClick(view: View) {
        game = Game()
        finish()
    }

    companion object {
        fun start(ctx: Context) {
            val intent = Intent(ctx, CountingActivity::class.java)
            ctx.startActivity(intent)
        }
    }
}
