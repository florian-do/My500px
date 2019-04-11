package com.do_f.my500px.ui.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

import com.do_f.my500px.R
import com.do_f.my500px.adapters.ShowcaseAdapter
import com.do_f.my500px.api.model.Photo
import com.do_f.my500px.base.BFragment
import com.do_f.my500px.databinding.FragmentShowcaseBinding
import com.do_f.my500px.singleton.DataHolder
import com.do_f.my500px.viewmodel.SharedViewModel
import com.do_f.my500px.viewmodel.ShowcaseViewModel
import kotlinx.android.synthetic.main.fragment_showcase.*

class ShowcaseFragment : BFragment() {

    private lateinit var binding : FragmentShowcaseBinding
    private lateinit var viewModel : ShowcaseViewModel
    private var sharedViewModel: SharedViewModel? = null
    private lateinit var adapter : ShowcaseAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_showcase, container, false)
        viewModel = ViewModelProviders.of(this).get(ShowcaseViewModel::class.java)
        adapter = ShowcaseAdapter(Glide.with(this), resources.configuration.orientation) { onPictureClick(it) }
        binding.loading = (adapter.itemCount == 0)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Glide.with(this)
            .load(R.drawable.sample_avatar)
            .apply(RequestOptions.circleCropTransform())
            .into(binding.profileAvatar)

        systemUIListener?.isSystemUIHidden(false)
        sharedViewModel = activity?.run {
            ViewModelProviders.of(this).get(SharedViewModel::class.java)
        }

        binding.rvFeed.layoutManager = LinearLayoutManager(context)
        binding.rvFeed.adapter = this.adapter
        viewModel.data.observe(this, Observer {
            adapter.submitList(it)
            binding.loading = (adapter.itemCount == 0)
        })

        sharedViewModel?.position?.observe(this, Observer {
            rvFeed.scrollToPosition(it?.minus(1) ?: 0)
        })
    }

    private fun onPictureClick(item: Photo) {
        adapter.currentList?.let {
            DataHolder.instance.data = it
            systemUIListener?.tmp(item)
//            replace(PhotoDetailHostFragment.newInstance(item))
        }
    }

    companion object {
        fun newInstance() = ShowcaseFragment()
    }
}