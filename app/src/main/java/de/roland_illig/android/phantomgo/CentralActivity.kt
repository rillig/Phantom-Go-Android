package de.roland_illig.android.phantomgo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import de.roland_illig.android.phantomgo.torus.ToroidalPlayActivity
import de.roland_illig.phantomgo.PhantomState

class CentralActivity : AppCompatActivity() {

    private lateinit var state: PhantomState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_central)
    }

    override fun onResume() {
        super.onResume()
        state = Persistence.loadPhantomGo(this)
    }

    fun onPlayToroidalGoClick(view: View) = ToroidalPlayActivity.start(this)

    fun onPlayPhantomGoClick(view: View) = onPlayPhantomGoClick()

    private fun onPlayPhantomGoClick() {
        when {
            state.isGameOver -> CountingActivity.start(this)
            state.isInitial -> PlayerActivity.start(this)
            else -> HandOverActivity.start(this)
        }
    }
}
