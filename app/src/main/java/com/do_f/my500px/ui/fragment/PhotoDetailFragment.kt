package com.do_f.my500px.ui.fragment

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestListener
import com.do_f.my500px.api.model.Photo
import com.do_f.my500px.base.BFragment
import com.do_f.my500px.databinding.FragmentPhotoDetailBinding
import com.do_f.my500px.viewmodel.PhotoDetailViewModel
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.constraintlayout.motion.widget.MotionLayout
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.target.Target
import com.do_f.my500px.R
import com.do_f.my500px.listener.DismissEvent
import com.do_f.my500px.setImageSizeFromRatioByHeight
import com.do_f.my500px.setImageSizeFromRatioByWidth

class PhotoDetailFragment : BFragment(), DismissEvent {

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

    var motionLayoutReady = false

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

        binding.loading = false
        when(resources.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                windowWidth = Resources.getSystem().displayMetrics.heightPixels.toFloat()
            }
            Configuration.ORIENTATION_PORTRAIT -> {
                windowWidth = Resources.getSystem().displayMetrics.widthPixels.toFloat()
            }
        }
        updateImageSize()
        binding.motionLayout?.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) { }
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) { }
            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) { }
            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                binding.loading = false
            }
        })

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Glide.with(this).load(item.images[0].https_url)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?, model: Any?,
                    target: Target<Drawable>?, isFirstResource: Boolean): Boolean  {
                    Log.e(TAG, "error : ", e)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?, model: Any?,
                    target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    Log.d(TAG, "onResourceReady")
                    binding.loading = false
                    return false
                }
            })
            .into(binding.picture)

        motionLayoutReady = true
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

    }

    override fun resetDismissEvent() {

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