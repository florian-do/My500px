package com.do_f.my500px.ui.fragment

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.text.Html
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
import com.do_f.my500px.*

import com.do_f.my500px.adapters.TagAdapter
import com.do_f.my500px.api.model.Photo
import com.do_f.my500px.api.model.VotesResponse
import com.do_f.my500px.api.service.PhotosService
import com.do_f.my500px.base.BDialogFragment
import com.do_f.my500px.base.BFragment
import com.do_f.my500px.databinding.FragmentPhotoDetailHostBinding
import com.do_f.my500px.listener.DismissEvent
import com.do_f.my500px.singleton.DataHolder
import com.do_f.my500px.ui.MainActivity
import com.do_f.my500px.ui.dialogfragment.CommentsFragment
import com.do_f.my500px.ui.dialogfragment.VotesFragment
import com.do_f.my500px.viewmodel.PhotoDetailViewModel
import com.do_f.my500px.viewmodel.SharedViewModel
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import kotlinx.android.synthetic.main.fragment_photo_detail_host.*
import kotlinx.android.synthetic.main.fragment_photo_detail_host.motionLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PhotoDetailHostFragment : BFragment(), DismissEvent {

    private val UI_STATE = "ui_state"
    private val ARG_ITEM = "arg_item"
    private var count: Int = 0
    private var position: Int = 0
    private val ROTATION_END_MOTION = 180

    private lateinit var binding : FragmentPhotoDetailHostBinding
    private lateinit var viewModel : PhotoDetailViewModel
    private lateinit var item: Photo
    private lateinit var data : PagedList<Photo>

    private var mSectionsPagerAdapter : SectionsPagerAdapter? = null
    private var isMotionLayoutLocked : Boolean = false
    private var motionLayoutState : Int = 0
    private var sharedViewModel : SharedViewModel? = null
    private var itemPosition = 0
    private var mListener : OnFragmentInteractionListener? = null
    private val api : PhotosService = App.retrofit.create(PhotosService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            itemPosition = it.getInt(ARG_ITEM)
            DataHolder.instance.data[itemPosition]?.let { item ->
                this.item = item
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_photo_detail_host, container, false)
        binding.motionLayout.visibility = GONE
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

        binding.votesInformation.setOnClickListener {
            fragmentManager?.let {
                val votesFragment = VotesFragment.newInstance(item.id, item.votes_count)
                votesFragment.mListener = {
                    systemUIListener?.isSystemUIHidden(true)
                }
                votesFragment.show(it, BDialogFragment.TAG)
            }
        }

        binding.commentInformation.setOnClickListener {
            fragmentManager?.let{ fm ->
                val commentsFragment = CommentsFragment.newInstance(item.id, item.comments_count)
                commentsFragment.mListener = {
                    systemUIListener?.isSystemUIHidden(true)
                }
                commentsFragment.show(fm, BDialogFragment.TAG)
            }
        }

        mSectionsPagerAdapter = SectionsPagerAdapter(childFragmentManager)
        container.adapter = mSectionsPagerAdapter
        container.currentItem = data.indexOf(item)
        container.pageMargin = resources.getDimension(R.dimen.viewpager_margin).toInt()
        container.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) { }
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) { }
            override fun onPageSelected(p0: Int) {
                this@PhotoDetailHostFragment.position = p0
                data[p0]?.let {
                    initView(it)
                }
            }
        })

        this.position = data.indexOf(item)

        sharedViewModel = activity?.run {
            ViewModelProviders.of(this).get(SharedViewModel::class.java)
        }

        viewModel = ViewModelProviders.of(this).get(PhotoDetailViewModel::class.java)
        binding.vm = viewModel
        initView(item)
        viewModel.showUI.observe(this, Observer {
            binding.showUI = it
            if (it)
                binding.motionLayout.visibility = VISIBLE
            else
                binding.motionLayout.visibility = GONE
        })

        binding.pictureInformation.afterMeasured {
            MainActivity.bottomContentHeight = height
        }

        setMotionLayoutTransitionListener()
        mListener?.onDataReady()
    }

    private fun initView(item: Photo) {
        this.item = item
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Glide.with(this).load(item.user.avatars.default.https)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.toolbarAvatar!!)
        }
        updateVotes()
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
        viewModel.datetime.set(item.created_at.parseDate(binding.datetime.resources))

        setDeviceAndExifData()

        val adapter = TagAdapter()
        adapter.items = item.tags
        val layoutManager = object : FlexboxLayoutManager(context) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.justifyContent = JustifyContent.FLEX_START

        binding.tagsFeed.adapter = adapter
        binding.tagsFeed.layoutManager = layoutManager
        binding.tagsFeed.isNestedScrollingEnabled = true
        viewModel.showUI.value = true
    }

    private fun setDeviceAndExifData() {
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

    private fun updateVotes() {
        api.getVotes(item.id, BuildConfig.FIVEPX_API_KEY, 0).enqueue(object : Callback<VotesResponse> {
            override fun onFailure(call: Call<VotesResponse>, t: Throwable) { }
            override fun onResponse(call: Call<VotesResponse>, response: Response<VotesResponse>) {
                response.body()?.let { item ->
                    when(item.total_items) {
                        0 -> binding.votesInformation.visibility = GONE
                        1 -> updateVoteUIForOneLike(item)
                        else -> updateVotesUIForMoreLikes(item)
                    }
                }
            }
        })
    }

    private fun updateVoteUIForOneLike(item: VotesResponse) {
        activity?.let {
            Glide.with(it).load(item.users[0].avatars.default.https)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.votesAvatarForeground)

            val str = getString(R.string.vote_label)
            binding.votesText.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml("<b>${item.users[0].fullname}</b> "+str, Html.FROM_HTML_MODE_COMPACT)
            } else {
                Html.fromHtml("<b>${item.users[0].fullname}</b> "+str)
            }
        }
    }

    private fun updateVotesUIForMoreLikes(item: VotesResponse) {
        activity?.let {
            Glide.with(it).load(item.users[1].avatars.default.https)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.votesAvatarBackground)

            Glide.with(it).load(item.users[0].avatars.default.https)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.votesAvatarForeground)

            val str = getString(R.string.votes_label, item.total_items - 2)
            binding.votesText.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml("<b>${item.users[0].fullname}, ${item.users[1].fullname}</b> "+str, Html.FROM_HTML_MODE_COMPACT)
            } else {
                Html.fromHtml("<b>${item.users[0].fullname}, ${item.users[1].fullname}</b> "+str)
            }
        }
    }

    private fun setMotionLayoutTransitionListener() {
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
                        // Portrait
                        mSectionsPagerAdapter?.getRegisteredFragment(this@PhotoDetailHostFragment.position)?.let {
                            if (it.motionLayoutReady) {
                                it.motionLayout.progress = Math.abs(p3)
                            }
                        }
                    } else {
                        // Landscape
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
        fun onDataReady()
    }

    companion object {
        @JvmStatic
        fun newInstance(position: Int) =
            PhotoDetailHostFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_ITEM, position)
                }
            }
    }
}