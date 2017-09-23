package de.roland_illig.android.phantomgo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View

class HandOverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hand_over)
    }

    fun onContinueClick(view: View) {
        val target = intent.getSerializableExtra("phantomGo.handOverTo") as Player
        val activity = (if (target == Player.BLACK) BlackActivity::class else WhiteActivity::class).java
        startActivity(Intent(this, activity))
        finish()
    }
}
