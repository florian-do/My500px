package com.do_f.my500px.ui

import android.arch.paging.PagedList
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.View

import com.do_f.my500px.R
import com.do_f.my500px.api.model.Photo
import com.do_f.my500px.singleton.DataHolder
import com.do_f.my500px.ui.fragment.PhotoDetailFragment
import kotlinx.android.synthetic.main.activity_photo_detail.*

class PhotoDetailActivity : AppCompatActivity(), PhotoDetailFragment.OnFragmentInteractionListener {

    companion object {
        const val ARG_ITEM = "arg_item"
        fun newInstance(a: FragmentActivity?, item: Photo) {
            a?.let {
                val intent = Intent(it, PhotoDetailActivity::class.java)
                intent.putExtra(ARG_ITEM, item)
                it.startActivityForResult(intent, 1337)
            }
        }
    }

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private lateinit var item: Photo
    private var isUIHidden: Boolean = true
    private lateinit var data : PagedList<Photo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_detail)
        item = intent.getSerializableExtra(ARG_ITEM) as Photo
        data = DataHolder.instance.data
        hideSystemUI()

        window.statusBarColor = Color.argb(0, 255, 255, 255)

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        container.adapter = mSectionsPagerAdapter
        container.currentItem = data.indexOf(item)
        container.pageMargin = resources.getDimension(R.dimen.viewpager_margin).toInt()
    }

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

//    override fun onWindowFocusChanged(hasFocus: Boolean) {
//        super.onWindowFocusChanged(hasFocus)
//        if (hasFocus) {
//            hideSystemUI()
//        }
//    }

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
//    private fun showSystemUI() {
//        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
//    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            data[position]?.let {
                return PhotoDetailFragment.newInstance(it, position)
            }
            return PhotoDetailFragment.newInstance(item, position)
        }

        override fun getCount(): Int {
            return data.size
        }
    }
}
