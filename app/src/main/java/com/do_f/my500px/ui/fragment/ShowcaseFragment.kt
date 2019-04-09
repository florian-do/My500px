package com.do_f.my500px.ui.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

import com.do_f.my500px.R
import com.do_f.my500px.adapters.ShowcaseAdapter
import com.do_f.my500px.api.model.Photo
import com.do_f.my500px.databinding.FragmentShowcaseBinding
import com.do_f.my500px.ui.PhotoDetailActivity
import com.do_f.my500px.viewmodel.ShowcaseViewModel

class ShowcaseFragment : Fragment() {

    private lateinit var binding : FragmentShowcaseBinding
    private lateinit var viewModel : ShowcaseViewModel
    private lateinit var adapter : ShowcaseAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_showcase, container, false)
        viewModel = ViewModelProviders.of(this).get(ShowcaseViewModel::class.java)
        adapter = ShowcaseAdapter(Glide.with(this)) { onPictureClick(it) }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Glide.with(this)
            .load(R.drawable.sample_avatar)
            .apply(RequestOptions.circleCropTransform())
            .into(binding.profileAvatar)
        binding.rvFeed.layoutManager = LinearLayoutManager(context)
        binding.rvFeed.adapter = this.adapter
        viewModel.data.observe(this, Observer {
            adapter.submitList(it)
        })
    }

    private fun onPictureClick(item: Photo) {
        PhotoDetailActivity.newInstance(activity, item)
    }

    companion object {
        fun newInstance() = ShowcaseFragment()
    }
}
