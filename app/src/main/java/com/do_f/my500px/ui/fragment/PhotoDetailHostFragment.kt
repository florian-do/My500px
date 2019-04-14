package com.do_f.my500px.ui.fragment

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.graphics.ColorUtils
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

import com.do_f.my500px.R
import com.do_f.my500px.adapters.TagAdapter
import com.do_f.my500px.api.model.Photo
import com.do_f.my500px.base.BFragment
import com.do_f.my500px.databinding.FragmentPhotoDetailHostBinding
import com.do_f.my500px.listener.DismissEvent
import com.do_f.my500px.singleton.DataHolder
import com.do_f.my500px.ui.MainActivity
import com.do_f.my500px.viewmodel.PhotoDetailViewModel
import com.do_f.my500px.viewmodel.SharedViewModel
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import kotlinx.android.synthetic.main.fragment_photo_detail_host.*
import kotlinx.android.synthetic.main.fragment_photo_detail_host.motionLayout

class PhotoDetailHostFragment : BFragment(), DismissEvent {

    private val UI_STATE = "ui_state"
    private val ARG_ITEM = "arg_item"
    private val TAG = "PhotoDetailHostFragment"

    private lateinit var item: Photo
    private lateinit var data : PagedList<Photo>
    private var count: Int = 0
    private var position: Int = 0

    private val ROTATION_END_MOTION = 180

    private lateinit var binding : FragmentPhotoDetailHostBinding
    private lateinit var viewModel : PhotoDetailViewModel
    private var mListener : OnFragmentInteractionListener? = null
    private var motionLayoutState : Int = 0
    private var sharedViewModel : SharedViewModel? = null
    private var mSectionsPagerAdapter : SectionsPagerAdapter? = null
    private var isMotionLayoutLocked : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            item = it.getSerializable(ARG_ITEM) as Photo
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_photo_detail_host, container, false)
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.getInt(UI_STATE, motionLayoutState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        data = DataHolder.instance.data
        count = data.size
        motionLayoutState = binding.motionLayout.startState
        binding.back.setOnClickListener {
            mListener?.myOnBackPress()
        }

        mSectionsPagerAdapter = SectionsPagerAdapter(childFragmentManager)
        container.adapter = mSectionsPagerAdapter
        container.currentItem = data.indexOf(item)
        container.pageMargin = resources.getDimension(R.dimen.viewpager_margin).toInt()
        container.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) { }
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) { }
            override fun onPageSelected(p0: Int) {
                data[p0]?.let {
                    initView(it)
                }
                this@PhotoDetailHostFragment.position = p0
            }
        })

        this.position = data.indexOf(item)

        sharedViewModel = activity?.run {
            ViewModelProviders.of(this).get(SharedViewModel::class.java)
        }

        viewModel = ViewModelProviders.of(this).get(PhotoDetailViewModel::class.java)
        binding.vm = viewModel
        viewModel.showUI.value = true
        initView(item)
        viewModel.showUI.observe(this, Observer {
            binding.showUI = it

            if (it)
                binding.motionLayout.visibility = VISIBLE
            else
                binding.motionLayout.visibility = GONE
        })

        binding.motionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) { }
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) { }
            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
                if (isMotionLayoutLocked) {
                    p0?.transitionToState(p1)
                } else {
                    binding.expandContent.rotation = ROTATION_END_MOTION * p3
                    binding.extraContent.alpha = p3
                    binding.informationSeparator.alpha = p3
                    if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                        mSectionsPagerAdapter?.getRegisteredFragment(this@PhotoDetailHostFragment.position)?.let {
                            if (it.motionLayoutReady) {
                                it.motionLayout.progress = Math.abs(p3)
                            }
                        }
                    } else {
                        binding.toolbar.alpha = Math.abs(1 - p3)
                        binding.pictureInformation.alpha = p3

                        val halfAlpha = 256 / 2
                        var toolbarAlpha = halfAlpha + (p3 * halfAlpha).toInt()
                        if (toolbarAlpha > 255) toolbarAlpha = 255
                        val color = ColorUtils.setAlphaComponent(resources.getColor(R.color.black_alpha50), toolbarAlpha)
                        binding.content.setBackgroundColor(color)
                    }
                }
            }

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                motionLayoutState = p1
                if (p1 == p0?.endState) {
                    MainActivity.isSwipeDismissEnable = false
                    binding.container.isSwipeEnable(false)
                } else {
                    MainActivity.isSwipeDismissEnable = true
                    binding.container.isSwipeEnable(true)
                }
            }
        })

        if (savedInstanceState != null) {
            val mlState = savedInstanceState.getInt(UI_STATE)
//            when(mlState) {
//                binding.motionLayout.startState -> binding.motionLayout.progress = 1F
//                binding.motionLayout.endState -> binding.motionLayout.progress = 0F
//            }
        }
    }

    private fun initView(item: Photo) {
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Glide.with(this).load(item.user.avatars.default.https)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.toolbarAvatar!!)
        }

        Glide.with(this).load(item.user.avatars.default.https)
            .apply(RequestOptions.circleCropTransform())
            .into(binding.avatar)

        viewModel.author.set(item.user.fullname)
        viewModel.title.set(item.name)
        viewModel.commentsCounts.set(item.comments_count.toString())
        viewModel.likesCounts.set(item.votes_count.toString())
        viewModel.pulse.set(item.rating.toString())
        viewModel.views.set(item.times_viewed.toString())
        viewModel.description.set(item.description)

        val adapter = TagAdapter()
        adapter.items = item.tags
        val layoutManager = object : FlexboxLayoutManager(context) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.justifyContent = JustifyContent.FLEX_START

        binding.tagsFeed?.adapter = adapter
        binding.tagsFeed?.layoutManager = layoutManager
        binding.tagsFeed?.isNestedScrollingEnabled = true

        if (item.camera == null) {
            binding.cameraInformation.visibility = GONE
        } else {
            viewModel.brand.set(item.camera)
        }

        if (item.lens == null) {
            binding.lensInformation.visibility = GONE
        } else {
            viewModel.lens.set(item.lens)
        }

        if (item.focal_length == null || item.aperture == null
            || item.shutter_speed == null || item.iso == null) {
            binding.exifInformation.visibility = GONE
        } else {
            viewModel.exif.set(getString(R.string.exif, item.focal_length, item.aperture, item.shutter_speed, item.iso))
        }
    }

    override fun onResume() {
        super.onResume()
        systemUIListener?.isSystemUIHidden(true)
    }

    override fun onStop() {
        super.onStop()
        sharedViewModel?.set(position)
    }

    override fun prepareForDismissEvent() {
        viewModel.showUI.value = false
        container.isSwipeEnable(false)
        mSectionsPagerAdapter?.getRegisteredFragment(this.position)?.prepareForDismissEvent()
    }

    override fun resetDismissEvent() {
        viewModel.showUI.value = true
        container.isSwipeEnable(true)
        mSectionsPagerAdapter?.getRegisteredFragment(this.position)?.resetDismissEvent()
    }

    fun onClickListener() {
        if (motionLayoutState == binding.motionLayout.startState) {
            val value : Boolean = viewModel.showUI.value ?: true
            viewModel.showUI.value = !value
        }
    }

    fun lockMotionLayoutAnimation() {
        isMotionLayoutLocked = true
    }

    fun unlockMotionLayoutAnimation() {
        isMotionLayoutLocked = false
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        private var registeredFragments : SparseArray<Fragment> = SparseArray()

        override fun getItem(position: Int): Fragment {
            data[position]?.let {
                Glide.with(this@PhotoDetailHostFragment)
                    .load(it.images[0].https_url)
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .preload()
                return PhotoDetailFragment.newInstance(it, position)
            }
            return PhotoDetailFragment.newInstance(item, position)
        }

        override fun getCount(): Int {
            return this@PhotoDetailHostFragment.count
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val f = super.instantiateItem(container, position) as Fragment
            registeredFragments.put(position, f)
            return f
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            registeredFragments.remove(position)
            super.destroyItem(container, position, `object`)
        }

        fun getRegisteredFragment(position: Int) : PhotoDetailFragment {
            return registeredFragments[position] as PhotoDetailFragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface OnFragmentInteractionListener {
        fun myOnBackPress()
    }

    companion object {
        @JvmStatic
        fun newInstance(item: Photo) =
            PhotoDetailHostFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_ITEM, item)
                }
            }
    }
}