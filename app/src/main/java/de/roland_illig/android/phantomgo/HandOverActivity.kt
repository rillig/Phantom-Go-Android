package de.roland_illig.android.phantomgo

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import de.roland_illig.phantomgo.Player

class HandOverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hand_over)

        // To prevent accidental double-clicks.
        Handler().postDelayed({ findViewById<Button>(R.id.continueButton).isEnabled = true }, 2000)
    }

    fun onContinueClick(view: View) {
        val target = intent.getSerializableExtra("phantomGo.handOverTo") as Player
        val activity = (if (target == Player.BLACK) BlackActivity::class else WhiteActivity::class).java
        startActivity(Intent(this, activity))
        finish()
    }
}
