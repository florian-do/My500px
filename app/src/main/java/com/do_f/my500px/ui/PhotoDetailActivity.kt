package com.do_f.my500px.ui

import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.view.View

import com.do_f.my500px.R
import com.do_f.my500px.api.model.Photo
import com.do_f.my500px.ui.fragment.PhotoDetailFragment
import kotlinx.android.synthetic.main.activity_photo_detail.*

class PhotoDetailActivity : AppCompatActivity(), PhotoDetailFragment.OnFragmentInteractionListener {

    companion object {
        val TAG = PhotoDetailActivity::class.java.simpleName
        const val ARG_ITEM = "arg_item"
        fun newInstance(a: FragmentActivity?, item: Photo) {
            a?.let {
                val intent = Intent(it, PhotoDetailActivity::class.java)
                intent.putExtra(ARG_ITEM, item)
                it.startActivity(intent)
            }
        }
    }

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private lateinit var item: Photo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_detail)
        item = intent.getSerializableExtra(ARG_ITEM) as Photo
        hideSystemUI()

        window.statusBarColor = Color.argb(0, 255, 255, 255)

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        container.adapter = mSectionsPagerAdapter
    }

    override fun onViewRootClick(isHidden: Boolean) {
        when(isHidden) {
            false -> {
                hideSystemUI()
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        Log.d(TAG, "onWindowFocusChanged: $hasFocus")
        if (hasFocus) {
//            hideSystemUI()
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
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private fun showSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return PhotoDetailFragment.newInstance(item)
        }

        override fun getCount(): Int {
            return 3
        }
    }
}
