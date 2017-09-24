package de.roland_illig.android.phantomgo

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

class HandOverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hand_over)

        val (target, refereeComments) = getExtra()
        val refereeResults = findViewById<ListView>(R.id.refereeResults)
        val refereeStrings = refereeComments.map { res -> GermanReferee.comment(res, target.other()) }
        refereeResults.adapter = ArrayAdapter(this, R.layout.string_list_item, refereeStrings)

        // To prevent accidental double-clicks.
        Handler().postDelayed({ findViewById<Button>(R.id.continueButton).isEnabled = true }, 1200)
    }

    fun onContinueClick(view: View) {
        val activity = (if (getExtra().target == Player.BLACK) BlackActivity::class else WhiteActivity::class).java
        startActivity(Intent(this, activity))
        finish()
    }

    private fun getExtra(): IntentExtra {
        return intent.getSerializableExtra("phantomGo.extra") as IntentExtra
    }

    data class IntentExtra(
            val target: Player,
            val refereeResults: List<RefereeResult>)
        : java.io.Serializable
}
