package com.do_f.my500px.ui.fragment

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.res.Resources
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.constraint.ConstraintSet
import android.support.transition.ChangeBounds
import android.support.transition.TransitionManager
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
import com.bumptech.glide.request.RequestOptions
import com.do_f.my500px.R
import com.do_f.my500px.afterMeasured
import com.do_f.my500px.api.model.Photo
import com.do_f.my500px.base.BFragment
import com.do_f.my500px.databinding.FragmentPhotoDetailBinding
import com.do_f.my500px.setSizeFromRatio
import com.do_f.my500px.viewmodel.PhotoDetailViewModel
import com.do_f.my500px.viewmodel.SharedViewModel
import kotlinx.android.synthetic.main.fragment_photo_detail.*

class PhotoDetailFragment : BFragment() {

    private lateinit var item: Photo
    private lateinit var viewModel: PhotoDetailViewModel
    private var sharedViewModel: SharedViewModel? = null
    private lateinit var binding : FragmentPhotoDetailBinding
    private val windowWidth: Float = Resources.getSystem().displayMetrics.widthPixels.toFloat()
    private var mListener: OnFragmentInteractionListener? = null
    private var position: Int = 0
    private var titleLines: Int = 0
    private var descriptionLines: Int = 0

    private val constraint1 = ConstraintSet()
    private val constraint2 = ConstraintSet()
    private var showContent = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            item = it.get(ARG_ITEM) as Photo
            position = it.getInt(ARG_POSITION)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_photo_detail,
            null,
            false)

        sharedViewModel = activity?.run {
            ViewModelProviders.of(this).get(SharedViewModel::class.java)
        }

        viewModel = ViewModelProviders.of(this).get(PhotoDetailViewModel::class.java)
        binding.vm = viewModel
        binding.root.setOnClickListener {
            if (!showContent) {
                mListener?.let {
                    it.setUIVisibility(!it.getUIVisibility())
                    val isUIHidden : Boolean = it.getUIVisibility()
                    binding.showUI = isUIHidden
                    it.onViewRootClick(isUIHidden)
                }
            }
        }

        binding.back.setOnClickListener {
            mListener?.myOnBackPress()
            sharedViewModel?.set(position)
        }

        binding.expandContent.setOnClickListener {
            doAnimation()
        }

        binding.picture.setSizeFromRatio(windowWidth, item)
        Glide.with(this)
            .load(item.images[0].https_url)
            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE))
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
        binding.showUI = mListener?.getUIVisibility()
    }

    override fun onStop() {
        super.onStop()
        sharedViewModel?.set(position)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        constraint1.clone(root)
        constraint2.clone(context, R.layout.fragment_photo_detail_alt)

        Glide.with(this).load(item.images[0].https_url).into(binding.picture)
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
    }

    private fun doAnimation() {
        when (showContent) {
            true -> {
                updateConstraint(R.layout.fragment_photo_detail)
                binding.picture.setSizeFromRatio(windowWidth, item)
                binding.extraContent.animate().alpha(0F).setDuration(300).start()
                binding.informationSperator.visibility = GONE
                binding.expandContent.setImageResource(R.drawable.ic_expand_less)
                binding.title.setLines(1)
            }
            false -> {
                updateConstraint(R.layout.fragment_photo_detail_alt)

                binding.extraContent.alpha = 0F
                binding.extraContent.visibility = VISIBLE
                binding.extraContent.animate().alpha(1F).setDuration(300).start()
                binding.title.setLines(titleLines)
                binding.description.afterMeasured {
                    descriptionLines = lineCount
                    setLines(3)
                }
                binding.description.ellipsize = TextUtils.TruncateAt.END

                binding.informationSperator.visibility = VISIBLE
                binding.expandContent.setImageResource(R.drawable.ic_expand_more)
                binding.picture.scaleType = ImageView.ScaleType.CENTER_CROP
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