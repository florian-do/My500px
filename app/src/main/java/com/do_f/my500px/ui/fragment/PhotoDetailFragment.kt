package com.do_f.my500px.ui.fragment

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.do_f.my500px.api.model.Photo
import com.do_f.my500px.base.BFragment
import com.do_f.my500px.databinding.FragmentPhotoDetailBinding
import com.do_f.my500px.viewmodel.PhotoDetailViewModel
import com.do_f.my500px.viewmodel.SharedViewModel
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.annotation.LayoutRes
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.ViewModelProviders
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.target.Target
import com.do_f.my500px.R
import com.do_f.my500px.afterMeasured
import com.do_f.my500px.listener.DismissEvent
import com.do_f.my500px.setImageSizeFromRatioByHeight
import com.do_f.my500px.setImageSizeFromRatioByWidth
import com.do_f.my500px.ui.MainActivity
import kotlinx.android.synthetic.main.fragment_photo_detail.*

class PhotoDetailFragment : BFragment(), DismissEvent {

    private val UI_STATE = "ui_state"

    private lateinit var item: Photo
    private lateinit var viewModel: PhotoDetailViewModel
    private var windowWidth = Resources.getSystem().displayMetrics.widthPixels.toFloat()
    private lateinit var binding : FragmentPhotoDetailBinding
    private var mListener: OnFragmentInteractionListener? = null
    private var position: Int = 0
    private var titleLines: Int = 0
    private var descriptionLines: Int = 0

    private var showContent = false
    private var TAG = "PhotoDetail"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            item = it.get(ARG_ITEM) as Photo
            position = it.getInt(ARG_POSITION)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(UI_STATE, showContent)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_photo_detail,
            null,
            false)

        binding.loading = true
        MainActivity.isSwipeDismissEnable = true

        when(resources.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                windowWidth = Resources.getSystem().displayMetrics.heightPixels.toFloat()
            }
            Configuration.ORIENTATION_PORTRAIT -> {
                windowWidth = Resources.getSystem().displayMetrics.widthPixels.toFloat()
            }
        }

        viewModel = ViewModelProviders.of(this).get(PhotoDetailViewModel::class.java)
        binding.vm = viewModel
        binding.showUI = false
//        binding.root.setOnClickListener {
//            if (!showContent) {
//                mListener?.let {
//                    it.setUIVisibility(!it.getUIVisibility())
//                    val isUIHidden : Boolean = it.getUIVisibility()
//                    binding.showUI = isUIHidden
//                    it.onViewRootClick(isUIHidden)
//                }
//            }
//        }

        binding.back.setOnClickListener {
            mListener?.myOnBackPress()
        }

        binding.expandContent.setOnClickListener {
            doAnimation()
        }

        updateImageSize()
        Glide.with(this)
            .load(item.images[0].https_url)
            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
            .preload()

        binding.title.afterMeasured {
            titleLines = lineCount
            setLines(1)
        }

        binding.description.setOnClickListener {
            binding.description.setLines(descriptionLines)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
//        binding.showUI = mListener?.getUIVisibility()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Glide.with(this).load(item.images[0].https_url)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?, model: Any?,
                    target: Target<Drawable>?, isFirstResource: Boolean): Boolean  = false

                override fun onResourceReady(
                    resource: Drawable?, model: Any?,
                    target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    binding.loading = false
                    return false
                }
            })
            .into(binding.picture)

        Glide.with(this).load(item.user.avatars.default.https)
            .apply(RequestOptions.circleCropTransform())
            .into(binding.avatar)

        when(resources.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                Glide.with(this).load(item.user.avatars.default.https)
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.toolbarAvatar!!)
            }
        }

        viewModel.author.set(item.user.fullname)
        viewModel.title.set(item.name)
        viewModel.commentsCounts.set(item.comments_count.toString())
        viewModel.likesCounts.set(item.votes_count.toString())
        viewModel.pulse.set(item.rating.toString())
        viewModel.views.set(item.times_viewed.toString())
        viewModel.description.set(item.description)

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

        if (item.focal_length == null
            || item.aperture == null
            || item.shutter_speed == null
            || item.iso == null) {
            binding.exifInformation.visibility = GONE
        } else {
            val exif = item.focal_length+"mm f/"+item.aperture+" "+item.shutter_speed+"s ISO"+item.iso
            viewModel.exif.set(exif)
        }

        if (savedInstanceState != null) {
//            showContent = !savedInstanceState.getBoolean(UI_STATE)
//            Log.d(TAG, "performClick${binding.expandContent.performClick()}")
//            doAnimation(true)
        }
    }

    private fun doAnimation(isFromInstanceState: Boolean = false) {
        when (showContent) {
            true -> {
                MainActivity.isSwipeDismissEnable = true
                updateConstraint(R.layout.fragment_photo_detail)
                updateImageSize()

                binding.extraContent.animate().alpha(0F).setDuration(300).start()
                binding.informationSperator.visibility = GONE
                binding.expandContent.setImageResource(R.drawable.ic_expand_less)
                binding.title.setLines(1)
                binding.loading = false
            }
            false -> {
                MainActivity.isSwipeDismissEnable = false

                if (isFromInstanceState) {
                    binding.toolbar.visibility = GONE
                }
                updateConstraint(R.layout.fragment_photo_detail_alt)

                binding.extraContent.alpha = 0F
                binding.extraContent.visibility = VISIBLE
                binding.extraContent.animate().alpha(1F).setDuration(300).start()
                binding.title.setLines(titleLines)
                binding.description.afterMeasured {
                    descriptionLines = lineCount
                    if (lineCount > 3)
                        setLines(3)
                    else
                        setLines(lineCount)
                }

                binding.description.ellipsize = TextUtils.TruncateAt.END
                binding.informationSperator.visibility = VISIBLE
                binding.expandContent.setImageResource(R.drawable.ic_expand_more)

                when(resources.configuration.orientation) {
                    Configuration.ORIENTATION_LANDSCAPE -> {
                        binding.toolbar.visibility = GONE
                    }
                    Configuration.ORIENTATION_PORTRAIT -> {
                        binding.picture.scaleType = ImageView.ScaleType.CENTER_CROP
                    }
                }
            }
        }

        showContent = !showContent
    }

    private fun updateConstraint(@LayoutRes id: Int) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(context, id)
        constraintSet.applyTo(root)
        val transition = ChangeBounds()
        transition.interpolator = DecelerateInterpolator()
        TransitionManager.beginDelayedTransition(root, transition)
    }

    private fun updateImageSize() {
        when(resources.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                binding.picture.setImageSizeFromRatioByHeight(windowWidth, item)
            }
            Configuration.ORIENTATION_PORTRAIT -> {
                binding.picture.setImageSizeFromRatioByWidth(windowWidth, item)
            }
        }
    }

    override fun prepareForDismissEvent() {
        mListener?.let {
//            Log.d(TAG, "prepareForDismissEvent: ${it.getUIVisibility()}")
//            if (it.getUIVisibility())
//                it.setUIVisibility(!it.getUIVisibility())
//            val isUIHidden : Boolean = it.getUIVisibility()
//            binding.showUI = false
        }
    }

    override fun resetDismissEvent() {
//        Log.d(TAG, "resetDismissEvent: ")
        mListener?.let {
//            binding.showUI = it.getUIVisibility()
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
        fun onViewRootClick(isHidden: Boolean)
        fun getUIVisibility() : Boolean
        fun setUIVisibility(isUIHidden: Boolean)
        fun myOnBackPress()
    }

    companion object {
        private const val ARG_ITEM = "arg_item"
        const val ARG_POSITION = "arg_position"

        @JvmStatic
        fun newInstance(item: Photo, position: Int) =
            PhotoDetailFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_ITEM, item)
                    putInt(ARG_POSITION, position)
                }
            }
    }
}