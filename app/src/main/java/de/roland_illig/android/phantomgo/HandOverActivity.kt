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
import de.roland_illig.phantomgo.GermanReferee
import de.roland_illig.phantomgo.Player
import de.roland_illig.phantomgo.RefereeResult
import java.io.Serializable

class HandOverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hand_over)

        val refereeStrings = refereeResults.map { res -> GermanReferee.comment(res, target.other()) }
        val refereeResultsView = findViewById<ListView>(R.id.refereeResults)
        refereeResultsView.adapter = ArrayAdapter(this, R.layout.string_list_item, refereeStrings)

        // To prevent accidental double-clicks.
        Handler().postDelayed({ findViewById<Button>(R.id.continueButton).isEnabled = true }, 1200)
    }

    fun onContinueClick(view: View) {
        PlayerActivity.start(this, target)
        finish()
    }

    private val target: Player
        get() = intent.getSerializableExtra("phantomGo.target") as Player
    private val refereeResults: List<RefereeResult>
        get() = intent.getSerializableExtra("phantomGo.refereeResults") as List<RefereeResult>

    companion object {
        fun start(ctx: Context, target: Player, refereeResults: List<RefereeResult>) {
            val intent = Intent(ctx, HandOverActivity::class.java)
            intent.putExtra("phantomGo.target", target)
            intent.putExtra("phantomGo.refereeResults", refereeResults as Serializable)
            ctx.startActivity(intent)
        }
    }
}
