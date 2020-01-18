package de.roland_illig.android.phantomgo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import de.roland_illig.phantomgo.Game
import de.roland_illig.phantomgo.Player
import de.roland_illig.phantomgo.Referee

/**
 * After playing a move, the player's board is hidden and the device
 * is handed over to the other player. During this, neither board must
 * be visible to avoid kibitzing.
 */
class HandOverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hand_over)

        val game = Persistence.load(this)
        val playerName = getText(if (game.turn == Player.BLACK) R.string.referee_black else R.string.referee_white)
        (findViewById<TextView>(R.id.handOverText)).text = resources.getString(R.string.hand_over_text, playerName)

        val refereeStrings = game.refereeHistory.map { result -> format(result) }
        val refereeResultsView = findViewById<ListView>(R.id.refereeHistory)
        refereeResultsView.adapter = ArrayAdapter(this, R.layout.string_list_item, refereeStrings)

        // To prevent accidental double-clicks.
        Handler().postDelayed({ findViewById<Button>(R.id.continueButton).isEnabled = true }, 1200)
    }

    fun onContinueClick(view: View) {
        PlayerActivity.start(this)
        finish()
    }

    private fun format(result: Game.RefereeHistoryEntry): String {
        val playerSymbol = if (result.player == Player.BLACK) "⚫" else "⚪"
        val comment = Referee.comment(result.result, result.player, resources)
        return "$playerSymbol\u2004$comment"
    }

    companion object {
        fun start(ctx: Context) {
            ctx.startActivity(Intent(ctx, HandOverActivity::class.java))
        }
    }
}
