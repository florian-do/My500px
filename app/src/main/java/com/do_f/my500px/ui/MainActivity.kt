package com.do_f.my500px.ui

import android.animation.Animator
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import androidx.core.view.GestureDetectorCompat
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.do_f.my500px.App
import com.do_f.my500px.R

import com.do_f.my500px.api.model.Photo
import com.do_f.my500px.listener.DismissEvent
import com.do_f.my500px.listener.OnSystemUIListener
import com.do_f.my500px.px
import com.do_f.my500px.singleton.DataHolder
import com.do_f.my500px.ui.fragment.PhotoDetailHostFragment
import com.do_f.my500px.ui.fragment.ShowcaseFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_photo_detail_host.*
import android.content.Context
import android.net.*
import com.do_f.my500px.base.BDialogFragment
import com.do_f.my500px.ui.dialogfragment.CommentsFragment
import com.do_f.my500px.ui.dialogfragment.VotesFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(),
    OnSystemUIListener, GestureDetector.OnGestureListener,
    DismissEvent, PhotoDetailHostFragment.OnFragmentInteractionListener,
    ShowcaseFragment.OnFragmentInteractionListener {

    private val PICTURE_VIEWER_TAG = "picture_viewer_tag"
    private val PICTURE_VIEW_STATE = "picture_viewer_state"
    private val INTERNET_ERROR_STATE = "internet_error_state"
    private val THRESHOLD_EVENT_DISMISS = 200F
    private val MAX_ALPHA_VALUE = 255F
    private val MIN_SWIPE = 200F
    private val MAX_SWIPE = 1000F

    private var isPictureViewHidden = true
    private var beginYPosition : Float = 0F
    private var scrollOffsetWithThreshold : Float = 0F

    private lateinit var mDetector: GestureDetectorCompat

    companion object {
        var isSwipeDismissEnable : Boolean = true
        var bottomContentHeight : Int = 0
        var isScrolling = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            if (App.defaultSystemUiVisibility == -1)
                App.defaultSystemUiVisibility = window.decorView.systemUiVisibility

            frontContainer.visibility = GONE
            initNetwork()
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.backContainer, ShowcaseFragment.newInstance())
                .commitNow()
        }

        mDetector = GestureDetectorCompat(this, this)
        if (savedInstanceState != null) {
            isPictureViewHidden = savedInstanceState.getBoolean(PICTURE_VIEW_STATE)
            if (!isPictureViewHidden) {
                frontContainer.visibility = VISIBLE
                backContainer.visibility = GONE
                pictureViewerBackground.visibility = VISIBLE
            }

            internet_status.visibility = savedInstanceState.getInt(INTERNET_ERROR_STATE)
            initNetwork()
        }
        // You can retrieve the consumer key with BuildConfig.FIVEPX_API_KEY
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(PICTURE_VIEW_STATE, isPictureViewHidden)
        outState.putInt(INTERNET_ERROR_STATE, internet_status.visibility)
    }

    override fun onBackPressed() {
        if (!isPictureViewHidden) {
            closePictureViewer()
        } else {
            super.onBackPressed()
        }
    }

    private fun getPictureViewerFragment() : Fragment? {
        return supportFragmentManager.findFragmentByTag(PICTURE_VIEWER_TAG)
    }

    override fun startPictureViewer(item: Photo) {
        isPictureViewHidden = false
        backContainer.visibility = GONE
        frontContainer.visibility = VISIBLE
        pictureViewerBackground.visibility = VISIBLE
        progress_loading.visibility = VISIBLE
        isSystemUIHidden(true)

        val position = DataHolder.instance.data.indexOf(item)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frontContainer, PhotoDetailHostFragment.newInstance(position), PICTURE_VIEWER_TAG)
            .addToBackStack(null)
            .commit()
    }

    private fun closePictureViewer() {
        backContainer.visibility = VISIBLE
        frontContainer.visibility = GONE
        frontContainer.y = 0F
        pictureViewerBackground.visibility = GONE
        pictureViewerBackground.alpha = 0F
        supportFragmentManager.popBackStack()
        isPictureViewHidden = true
        isSystemUIHidden(false)
    }

    /**
     * PhotoDetailHost OnFragmentInteraction Implementation
     */

    override fun myOnBackPress() {
        closePictureViewer()
    }

    override fun onDataReady() {
        progress_loading.visibility = GONE
    }

    /**
     * BFragment Implementation
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
        if (getPictureViewerFragment() is PhotoDetailHostFragment) {
            val fragment = getPictureViewerFragment() as PhotoDetailHostFragment
            fragment.container.dispatchTouchEvent(event)
        }

        when (event?.action) {
            MotionEvent.ACTION_UP -> {
                if (isScrolling && !isPictureViewHidden) {
                    isScrolling = false

                    if (scrollOffsetWithThreshold > Resources.getSystem().displayMetrics.heightPixels / 3.5)
                        closePictureViewer()
                    else
                        resetDismissEvent()

                    scrollOffsetWithThreshold = 0F
                }
            }
        }

        return super.dispatchTouchEvent(event)
    }

    /**
     * mGesture implementation
     */

    override fun onShowPress(p0: MotionEvent?) { }
    override fun onLongPress(p0: MotionEvent?) { }
    override fun onSingleTapUp(p0: MotionEvent?): Boolean {
        val y : Float = p0?.y ?: 0F
        val maxHeight = Resources.getSystem().displayMetrics.heightPixels.toFloat()
        if (y > getToolbarHeight() && y < (maxHeight - getBottomContentHeight())) {
            if (getPictureViewerFragment() is PhotoDetailHostFragment) {
                (getPictureViewerFragment() as PhotoDetailHostFragment).onClickListener()
            }
        }
        return true
    }
    override fun onDown(p0: MotionEvent?): Boolean = true
    override fun onFling(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean  {
        val deltaX : Float = p0?.x?.minus(p1?.x ?: 0F) ?: 0F
        val deltaAbsX = Math.abs(deltaX)

        // Swipe left or right
        if (deltaAbsX in MIN_SWIPE..MAX_SWIPE) {
            isSwipeDismissEnable = true
            if (getPictureViewerFragment() is PhotoDetailHostFragment) {
                (getPictureViewerFragment() as PhotoDetailHostFragment).unlockMotionLayoutAnimation()
            }
        }
        return true
    }

    override fun onScroll(p0: MotionEvent, p1: MotionEvent, p2: Float, p3: Float): Boolean {
        if (isPictureViewHidden) return true

        return when (getSlope(p0.x, p0.y, p1.x, p1.y)) {
            1 -> true
            2 -> handleViewPagerEvent()
            3 -> handleDismissEvent(p0, p1)
            4 -> handleViewPagerEvent()
            else -> true
        }
    }

    // https://vshivam.wordpress.com/2014/04/28/detecting-up-down-left-right-swipe-on-android/
    private fun getSlope(x1: Float, y1: Float, x2: Float, y2: Float): Int {
        val angle = Math.toDegrees(Math.atan2((y1 - y2).toDouble(), (x2 - x1).toDouble()))

        if (angle > 45 && angle <= 135)
        // top
            return 1
        if (angle >= 135 && angle < 180 || angle < -135 && angle > -180)
        // left
            return 2
        if (angle < -45 && angle >= -135)
        // down
            return 3
        return if (angle > -45 && angle <= 45) 4 else 0
    }

    private fun handleViewPagerEvent() : Boolean {
        if (isSwipeDismissEnable)
            isSwipeDismissEnable = false

        if (getPictureViewerFragment() is PhotoDetailHostFragment) {
            (getPictureViewerFragment() as PhotoDetailHostFragment).lockMotionLayoutAnimation()
        }

        return true
    }

    private fun handleDismissEvent(p0: MotionEvent, p1: MotionEvent) : Boolean {
        if (p0.y <= getToolbarHeight()) return true

        val scrollOffset = p1.y - p0.y
        if (scrollOffset > THRESHOLD_EVENT_DISMISS && !isScrolling && isSwipeDismissEnable) {
            if (beginYPosition == 0F) beginYPosition = frontContainer.y
            prepareForDismissEvent()
        }

        if (isScrolling) {
            scrollOffsetWithThreshold = beginYPosition + (scrollOffset - THRESHOLD_EVENT_DISMISS)
            if (scrollOffsetWithThreshold < 0) return true

            val backgroundAlpha : Float = (MAX_ALPHA_VALUE - ((scrollOffsetWithThreshold * MAX_ALPHA_VALUE) / 1000)) / MAX_ALPHA_VALUE
            if (backgroundAlpha in 0.0..1.0) {
                pictureViewerBackground.alpha = backgroundAlpha
            }

            frontContainer.y = scrollOffsetWithThreshold
        }
        return true
    }


    override fun prepareForDismissEvent() {
        pictureViewerBackground.visibility = VISIBLE
        backContainer.visibility = VISIBLE
        isScrolling = true
        val f = getPictureViewerFragment() as PhotoDetailHostFragment
        f.prepareForDismissEvent()
    }

    override fun resetDismissEvent() {
        beginYPosition = 0F
        frontContainer.animate().y(0F).setDuration(300).start()
        pictureViewerBackground.animate()
            .alpha(1F)
            .setDuration(300)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(p0: Animator?) { }
                override fun onAnimationCancel(p0: Animator?) { }
                override fun onAnimationStart(p0: Animator?) { }
                override fun onAnimationEnd(p0: Animator?) {
                    backContainer.visibility = GONE
                }
            })
            .start()
        val f = getPictureViewerFragment() as PhotoDetailHostFragment
        f.resetDismissEvent()
    }

    private fun getToolbarHeight() : Int {
        val styledAttributes = theme.obtainStyledAttributes(
            intArrayOf(android.R.attr.actionBarSize)
        )
        val mActionBarSize = styledAttributes.getDimension(0, 0f).toInt()
        styledAttributes.recycle()
        return mActionBarSize
    }

    private fun getBottomContentHeight() : Int {
        return bottomContentHeight + 50.px
    }

    private fun initNetwork() {
        val networkRequest = NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build()

        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerNetworkCallback(networkRequest, object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network?) {
                super.onAvailable(network)
                val f = if (supportFragmentManager.findFragmentByTag(BDialogFragment.TAG) != null) {
                    supportFragmentManager.findFragmentByTag(BDialogFragment.TAG)
                } else {
                    supportFragmentManager.findFragmentById(R.id.backContainer)
                }

                if (internet_status.visibility == VISIBLE) {
                    when (f) {
                        is ShowcaseFragment -> f.refreshDatasource()
                        is CommentsFragment -> f.refreshDatasource()
                        is VotesFragment -> f.refreshDatasource()
                        else -> { }
                    }
                }

                GlobalScope.launch(context = Dispatchers.Main) {
                    internet_status.visibility = GONE
                }
            }

            override fun onLost(network: Network?) {
                super.onLost(network)
                GlobalScope.launch(context = Dispatchers.Main) {
                    internet_status.visibility = VISIBLE
                }
            }
        })
    }
}