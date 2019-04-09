package com.do_f.my500px.ui.fragment

import android.content.Context
import android.content.res.Resources
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.do_f.my500px.R
import com.do_f.my500px.api.model.Photo
import com.do_f.my500px.api.model.getRatio
import com.do_f.my500px.databinding.FragmentPhotoDetailBinding

class PhotoDetailFragment : Fragment() {

    private lateinit var item: Photo
    private var isUIHidden = true
    private val windowWidth: Float = Resources.getSystem().displayMetrics.widthPixels.toFloat()
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            item = it.get(ARG_ITEM) as Photo
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding : FragmentPhotoDetailBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_photo_detail,
            null,
            false)

        binding.root.setOnClickListener {
            when(isUIHidden) {
                true -> {
                    binding.showUI = isUIHidden
//                    showSystemUI()
                }
                false -> {
                    binding.showUI = isUIHidden
//                    hideSystemUI()
                }
            }
            listener?.onViewRootClick(isUIHidden)
            isUIHidden = !isUIHidden
        }

        val imageRatio = item.getRatio()

        if (item.width > item.height) {
            binding.picture.layoutParams.height = (windowWidth / imageRatio).toInt()
            binding.picture.layoutParams.width = windowWidth.toInt()
        } else {
            binding.picture.layoutParams.height = (windowWidth * imageRatio).toInt()
            binding.picture.layoutParams.width = windowWidth.toInt()
        }

        Glide.with(this).load(item.images[0].https_url).into(binding.picture)

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onViewRootClick(isHidden: Boolean)
    }

    companion object {
        private const val ARG_ITEM = "arg_item"

        @JvmStatic
        fun newInstance(item: Photo) =
            PhotoDetailFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_ITEM, item)
                }
            }
    }
}
