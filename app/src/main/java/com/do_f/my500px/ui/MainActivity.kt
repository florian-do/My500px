package com.do_f.my500px.ui

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.graphics.ColorUtils
import android.support.v4.view.GestureDetectorCompat
import android.support.v4.view.MotionEventCompat
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import com.do_f.my500px.App

import com.do_f.my500px.R
import com.do_f.my500px.api.model.Photo
import com.do_f.my500px.findBehavior
import com.do_f.my500px.listener.OnSystemUIListener
import com.do_f.my500px.ui.fragment.PhotoDetailFragment
import com.do_f.my500px.ui.fragment.PhotoDetailHostFragment
import com.do_f.my500px.ui.fragment.ShowcaseFragment
import com.do_f.my500px.view.BackdropBehavior
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),
    PhotoDetailFragment.OnFragmentInteractionListener,
    OnSystemUIListener, GestureDetector.OnGestureListener {

    val TAG = "MainActivity"

    private var isUIHidden: Boolean = true
    private var isPictureViewHidden = true

    private lateinit var mDetector: GestureDetectorCompat
    private lateinit var backdropBehavior : BackdropBehavior

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            if (App.defaultSystemUiVisibility == -1)
                App.defaultSystemUiVisibility = window.decorView.systemUiVisibility

            frontContainer.visibility = GONE
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.backContainer, ShowcaseFragment.newInstance())
                .commitNow()

        }

        backdropBehavior = frontContainer.findBehavior()
        with(backdropBehavior) {
            attachBackContainer(R.id.backContainer)
            attachFrontContainer(R.id.frontContainer)
        }

        mDetector = GestureDetectorCompat(this, this)
        // You can retrieve the consumer key with BuildConfig.FIVEPX_API_KEY
    }

    override fun tmp(item: Photo) {
        frontContainer.visibility = VISIBLE
        backContainer.visibility = GONE
        isPictureViewHidden = false

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frontContainer, PhotoDetailHostFragment.newInstance(item), "test")
            .commitNow()
    }

    override fun onBackPressed() {
        if (!isPictureViewHidden) {
            supportFragmentManager.findFragmentByTag("test")?.let {
                backContainer.visibility = VISIBLE
                frontContainer.visibility = GONE
                supportFragmentManager.beginTransaction().remove(it).commit()
                isPictureViewHidden = true
                isSystemUIHidden(false)
            }
        } else {
            super.onBackPressed()
        }
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
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)

        window.statusBarColor = Color.argb(0, 255, 255, 255)
    }

    private fun showSystemUI() {
        window.statusBarColor = Color.argb(0, 0, 0, 0)
        window.decorView.systemUiVisibility = App.defaultSystemUiVisibility
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        mDetector.onTouchEvent(event)

        val action: Int = MotionEventCompat.getActionMasked(event)
        when (action) {
            MotionEvent.ACTION_UP -> {
                if (isScrolling && !isPictureViewHidden) {
//                    backContainer.visibility = GONE
                    beginYPosition = 0F
//                    pictureViewerBackground.visibility = GONE
                    frontContainer.animate().y(0F).setDuration(300).start()
//                    pictureViewerBackground.animate().alpha(1F).setDuration(300).start()
                    isScrolling = false
                }
            }
        }

        return super.dispatchTouchEvent(event)
    }

    /**
     * mGesture implementation
     */

    override fun onShowPress(p0: MotionEvent?) {

    }

    override fun onSingleTapUp(p0: MotionEvent?): Boolean = true

    override fun onDown(p0: MotionEvent?): Boolean = true

    override fun onFling(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean = true

    private var isScrolling = false
    private var beginYPosition : Float = 0F
    private var startThresholdEvend : Float = 0F
    private var THRESHOLD_EVENT_DISMISS = 200F
    private var MAX_ALPHA_VALUE = 255

    override fun onScroll(p0: MotionEvent, p1: MotionEvent, p2: Float, p3: Float): Boolean {
        if (isPictureViewHidden) return true
        if (p0.y > p1.y) return true
//        Log.d(TAG, "${p0.y} / ${p1.y}")
        if (beginYPosition == 0F) beginYPosition = frontContainer.y
        val scrollOffset = p1.y - p0.y

        if (scrollOffset > THRESHOLD_EVENT_DISMISS && !isScrolling) {
            pictureViewerBackground.visibility = VISIBLE
            backContainer.visibility = VISIBLE
            isScrolling = true
        }

        if (isScrolling) {
            val scrollOffsetWithThreshold = beginYPosition + (scrollOffset - THRESHOLD_EVENT_DISMISS)
            val toolbarAlpha = MAX_ALPHA_VALUE - ((scrollOffsetWithThreshold * 255) / 1000).toInt()
            val tmp = ((200 - scrollOffsetWithThreshold) * 1F / THRESHOLD_EVENT_DISMISS)
            Log.d(TAG, "start : $toolbarAlpha + ${tmp}")
            if (toolbarAlpha in 0..255) {
                val color = ColorUtils.setAlphaComponent(resources.getColor(R.color.black), toolbarAlpha)
                pictureViewerBackground.setBackgroundColor(color)
            }
            frontContainer.y = scrollOffsetWithThreshold
        }
        return true
    }

    override fun onLongPress(p0: MotionEvent?) {

    }
}
