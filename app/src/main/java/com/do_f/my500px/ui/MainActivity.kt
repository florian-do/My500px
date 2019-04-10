package com.do_f.my500px.ui

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

import com.do_f.my500px.R
import com.do_f.my500px.listener.OnSystemUIListener
import com.do_f.my500px.ui.fragment.PhotoDetailFragment
import com.do_f.my500px.ui.fragment.ShowcaseFragment

class MainActivity : AppCompatActivity(),
    PhotoDetailFragment.OnFragmentInteractionListener,
    OnSystemUIListener {

    private var isUIHidden: Boolean = true
    private var defaultSystemUiVisibility: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            defaultSystemUiVisibility = window.decorView.systemUiVisibility
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.content, ShowcaseFragment.newInstance())
                .commitNow()
        }
        // You can retrieve the consumer key with BuildConfig.FIVEPX_API_KEY
    }

    /**
     * PhotoDetailFragment Listener Implementation
     */

    override fun onViewRootClick(isHidden: Boolean) {
        when(isHidden) {
            false -> {
                hideSystemUI()
            }
        }
    }

    override fun getUIVisibility(): Boolean {
        return isUIHidden
    }

    override fun setUIVisibility(isUIHidden: Boolean) {
        this.isUIHidden = isUIHidden
    }

    override fun myOnBackPress() {
        supportFragmentManager.popBackStack()
    }

    /**
     * END PhotoDetailFragment Listener Implementation
     */

    override fun isSystemUIHidden(isHidden: Boolean) {
        if (isHidden) {
            hideSystemUI()
        } else {
            showSystemUI()
        }
    }

    private fun hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)

        window.statusBarColor = Color.argb(0, 255, 255, 255)
    }

//     Shows the system bars by removing all the flags
//     except for the ones that make the content appear under the system bars.
    private fun showSystemUI() {
        window.decorView.systemUiVisibility = defaultSystemUiVisibility
        window.statusBarColor = Color.argb(0, 0, 0, 0)
    }
}
