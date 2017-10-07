package de.roland_illig.android.phantomgo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import de.roland_illig.phantomgo.Player
import de.roland_illig.phantomgo.Referee
import de.roland_illig.phantomgo.RefereeResult
import java.io.Serializable

class HandOverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hand_over)

        val playerName = getText(if (player() == Player.BLACK) R.string.referee_black else R.string.referee_white)
        (findViewById<TextView>(R.id.handOverText)).text = resources.getString(R.string.hand_over_text, playerName)

        val refereeStrings = refereeResults().map { result -> Referee.comment(result, player().other(), resources) }
        val refereeResultsView = findViewById<ListView>(R.id.refereeResults)
        refereeResultsView.adapter = ArrayAdapter(this, R.layout.string_list_item, refereeStrings)

        // To prevent accidental double-clicks.
        Handler().postDelayed({ findViewById<Button>(R.id.continueButton).isEnabled = true }, 1200)
    }

    fun onContinueClick(view: View) {
        PlayerActivity.start(this, player())
        finish()
    }

    private fun player() = intent.getSerializableExtra("phantomGo.player") as Player
    private fun refereeResults() = intent.getSerializableExtra("phantomGo.refereeResults") as List<RefereeResult>

    companion object {
        fun start(ctx: Context, target: Player, refereeResults: List<RefereeResult>) {
            val intent = Intent(ctx, HandOverActivity::class.java)
            intent.putExtra("phantomGo.player", target)
            intent.putExtra("phantomGo.refereeResults", refereeResults as Serializable)
            ctx.startActivity(intent)
        }
    }
}
