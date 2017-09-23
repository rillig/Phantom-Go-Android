package de.roland_illig.android.phantomgo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View

class BlackActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_black)
    }

    fun onHandOverClick(view: View) {
        val intent = Intent(this, HandOverActivity::class.java)
        intent.putExtra("phantomGo.handOverTo", Player.WHITE)
        startActivity(intent)
        finish()
    }
}
