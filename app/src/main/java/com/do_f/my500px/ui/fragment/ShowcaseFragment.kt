package com.do_f.my500px.ui.fragment

import android.content.Context
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.do_f.my500px.App
import com.do_f.my500px.BuildConfig

import com.do_f.my500px.R
import com.do_f.my500px.adapters.ShowcaseAdapter
import com.do_f.my500px.api.model.Photo
import com.do_f.my500px.api.service.PhotosService
import com.do_f.my500px.base.BFragment
import com.do_f.my500px.databinding.FragmentShowcaseBinding
import com.do_f.my500px.enumdir.State
import com.do_f.my500px.singleton.DataHolder
import com.do_f.my500px.ui.dialogfragment.CommentsFragment
import com.do_f.my500px.viewmodel.SharedViewModel
import com.do_f.my500px.viewmodel.ShowcaseViewModel
import kotlinx.android.synthetic.main.fragment_showcase.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ShowcaseFragment : BFragment() {

    val TAG = "ShowcaseFragment"

    private lateinit var binding : FragmentShowcaseBinding
    private lateinit var viewModel : ShowcaseViewModel
    private var sharedViewModel: SharedViewModel? = null
    private var mListener: OnFragmentInteractionListener? = null
    private lateinit var adapter : ShowcaseAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_showcase, container, false)
        viewModel = ViewModelProviders.of(this).get(ShowcaseViewModel::class.java)
        adapter = ShowcaseAdapter(Glide.with(this), resources.configuration.orientation, {
            onPictureClick(it)
        }, { id, commentsCount ->
            fragmentManager?.let {
                val f = CommentsFragment.newInstance(id, commentsCount)
                f.show(it, null)
            }
        })
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

        viewModel.getState().observe(this, Observer {
//            if (it == State.DONE)
//                adapter.currentList?.let { it1 -> fetchComments(it1) }
        })

        sharedViewModel?.position?.observe(this, Observer {
            rvFeed.scrollToPosition(it ?: 0)
        })
    }

    private fun fetchComments(items: PagedList<Photo>) {
        Log.d(TAG, "fetchComments")
        GlobalScope.launch {
            val api = App.retrofit.create(PhotosService::class.java)
            repeat(items.size) { i ->
                items[i]?.let {
                    Log.d(TAG, "Fetching from API ${it.id} : ${it.comments_count / 20} ${it.comments_count % 20}")
//                    val response = api.getComments(it.id, BuildConfig.FIVEPX_API_KEY).execute()
//                    if (response.isSuccessful) {
//
//                    } else {
//                        Log.d(TAG, "ERROR")
//                    }
                }
            }
        }
    }

    private fun onPictureClick(item: Photo) {
        adapter.currentList?.let {
            DataHolder.instance.data = it
            mListener?.startPictureViewer(item)
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
        fun startPictureViewer(item: Photo)
    }

    companion object {
        fun newInstance() = ShowcaseFragment()
    }
}