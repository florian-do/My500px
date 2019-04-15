package com.do_f.my500px.ui.fragment

import android.content.Context
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

import com.do_f.my500px.R
import com.do_f.my500px.adapters.ShowcaseAdapter
import com.do_f.my500px.api.model.Photo
import com.do_f.my500px.base.BDialogFragment
import com.do_f.my500px.base.BFragment
import com.do_f.my500px.databinding.FragmentShowcaseBinding
import com.do_f.my500px.enumdir.State
import com.do_f.my500px.singleton.DataHolder
import com.do_f.my500px.ui.dialogfragment.CommentsFragment
import com.do_f.my500px.viewmodel.SharedViewModel
import com.do_f.my500px.viewmodel.ShowcaseViewModel
import kotlinx.android.synthetic.main.fragment_showcase.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ShowcaseFragment : BFragment() {

    private lateinit var binding : FragmentShowcaseBinding
    private lateinit var adapter : ShowcaseAdapter

    private var sharedViewModel: SharedViewModel? = null
    private var mListener: OnFragmentInteractionListener? = null
    private var viewModel : ShowcaseViewModel? = null

    private val ADAPTER_POSITION_STATE = "adapter_position_state"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_showcase, container, false)
        viewModel = ViewModelProviders.of(this).get(ShowcaseViewModel::class.java)
        adapter = ShowcaseAdapter(Glide.with(this), resources.configuration.orientation, {
            onPictureClick(it)
        }, { id, commentsCount ->
            fragmentManager?.let {
                val f = CommentsFragment.newInstance(id, commentsCount)
                f.show(it, BDialogFragment.TAG)
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
        viewModel?.data?.observe(this, Observer {
            adapter.submitList(it)
            binding.loading = (adapter.itemCount == 0)
        })

        viewModel?.getState()?.observe(this, Observer {
            GlobalScope.launch(context = Dispatchers.Main) {
                when (it) {
                    State.LOADING -> binding.loading = true
                    State.ERROR -> {
                        binding.loading = false
                        binding.swipeRefresh.isRefreshing = false
                        binding.error.visibility = VISIBLE
                    }
                    State.DONE -> {
                        binding.loading = false
                        binding.swipeRefresh.isRefreshing = false
                        binding.error.visibility = GONE
                        rvFeed.scrollToPosition(0)
                    }
                    else -> { }
                }
            }
        })

        sharedViewModel?.position?.observe(this, Observer {
            rvFeed.scrollToPosition(it ?: 0)
        })

        binding.swipeRefresh.setOnRefreshListener {
            binding.error.visibility = GONE
            refreshDatasource()
        }

        if (savedInstanceState != null) {
            binding.rvFeed.scrollToPosition(savedInstanceState.getInt(ADAPTER_POSITION_STATE))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(ADAPTER_POSITION_STATE, adapter.position)

    }

    fun refreshDatasource() {
        viewModel?.dataSourceFactory?.source?.invalidate()
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