package com.do_f.my500px.ui.fragment

import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

import com.do_f.my500px.R
import com.do_f.my500px.api.model.Photo
import com.do_f.my500px.base.BFragment
import com.do_f.my500px.databinding.FragmentPhotoDetailHostBinding
import com.do_f.my500px.listener.DismissEvent
import com.do_f.my500px.singleton.DataHolder
import com.do_f.my500px.ui.MainActivity
import com.do_f.my500px.viewmodel.PhotoDetailViewModel
import com.do_f.my500px.viewmodel.SharedViewModel
import kotlinx.android.synthetic.main.fragment_photo_detail_host.*
import kotlinx.android.synthetic.main.fragment_photo_detail_host.motionLayout

class PhotoDetailHostFragment : BFragment(), DismissEvent {

    private val ARG_ITEM = "arg_item"
    private val TAG = "PhotoDetailHostFragment"

    private lateinit var item: Photo
    private lateinit var data : PagedList<Photo>
    private var count: Int = 0
    private var position: Int = 0

    private val ROTATION_END_MOTION = 180

    private lateinit var binding : FragmentPhotoDetailHostBinding
    private lateinit var viewModel: PhotoDetailViewModel
    private var sharedViewModel: SharedViewModel? = null
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private var oldProgress: Float = 0F

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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        data = DataHolder.instance.data
        count = data.size
        systemUIListener?.isSystemUIHidden(true)

        mSectionsPagerAdapter = SectionsPagerAdapter(childFragmentManager)

        container.adapter = mSectionsPagerAdapter
        container.currentItem = data.indexOf(item)
        this.position = data.indexOf(item)
        container.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) { }
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) { }
            override fun onPageSelected(p0: Int) {
                this@PhotoDetailHostFragment.position = p0
            }
        })
        container.pageMargin = resources.getDimension(R.dimen.viewpager_margin).toInt()

        sharedViewModel = activity?.run {
            ViewModelProviders.of(this).get(SharedViewModel::class.java)
        }

        viewModel = ViewModelProviders.of(this).get(PhotoDetailViewModel::class.java)
        binding.vm = viewModel
        binding.showUI = true
        setupView()

        motionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
                Log.d(TAG, "onTransitionTrigger")
            }

            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
                Log.d(TAG, "onTransitionStarted")
            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
                expandContent.rotation = ROTATION_END_MOTION * p3
                extraContent.alpha = p3
                mSectionsPagerAdapter?.getRegisteredFragment(this@PhotoDetailHostFragment.position)?.let {
                    if (it.motionLayoutReady) {
                        it.motionLayout.progress = Math.abs(p3)
                    }
                }
//                p0?.transitionToState(p1)
            }

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                if (p1 == p0?.endState) {
                    MainActivity.isSwipeDismissEnable = false
                    container.isScrollEnable(false)
                } else {
                    MainActivity.isSwipeDismissEnable = true
                    container.isScrollEnable(true)
                }
            }
        })
    }

    fun triggerMotionSceneAnimation() {

    }

    private fun setupView() {
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

        if (item.camera == null) {
            binding.cameraInformation.visibility = View.GONE
        } else {
            viewModel.brand.set(item.camera)
        }

        if (item.lens == null) {
            binding.lensInformation.visibility = View.GONE
        } else {
            viewModel.lens.set(item.lens)
        }

        if (item.focal_length == null
            || item.aperture == null
            || item.shutter_speed == null
            || item.iso == null) {
            binding.exifInformation.visibility = View.GONE
        } else {
            val exif = item.focal_length+"mm f/"+item.aperture+" "+item.shutter_speed+"s ISO"+item.iso
            viewModel.exif.set(exif)
        }
    }

    override fun onStop() {
        super.onStop()
        sharedViewModel?.set(position)
    }

    override fun prepareForDismissEvent() {
        container.isScrollEnable(false)
        mSectionsPagerAdapter?.getRegisteredFragment(this.position)?.prepareForDismissEvent()
    }

    override fun resetDismissEvent() {
        container.isScrollEnable(true)
        mSectionsPagerAdapter?.getRegisteredFragment(this.position)?.resetDismissEvent()
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