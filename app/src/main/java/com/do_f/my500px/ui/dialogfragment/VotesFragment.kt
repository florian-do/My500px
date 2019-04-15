package com.do_f.my500px.ui.dialogfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide

import com.do_f.my500px.R
import com.do_f.my500px.adapters.VoteAdapter
import com.do_f.my500px.base.BDialogFragment
import com.do_f.my500px.databinding.FragmentVotesBinding
import com.do_f.my500px.enumdir.State
import com.do_f.my500px.viewmodel.VotesViewModel

private const val ARG_ID = "arg_id"
private const val ARG_VOTES_COUNT = "arg_votes_count"

class VotesFragment : BDialogFragment() {
    private var pictureId: Int = 0
    private var votesCount: Int = 0

    private lateinit var binding : FragmentVotesBinding
    private lateinit var viewModel : VotesViewModel
    private lateinit var adapter : VoteAdapter

    lateinit var mListener: () -> Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            pictureId = it.getInt(ARG_ID)
            votesCount = it.getInt(ARG_VOTES_COUNT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_votes, container, false)
        adapter = VoteAdapter(Glide.with(this), 0)
        viewModel = ViewModelProviders.of(this).get(VotesViewModel::class.java)
        viewModel.init(pictureId)
        binding.loading = true
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val lm = LinearLayoutManager(context)
        binding.rvFeed.layoutManager = lm
        binding.rvFeed.adapter = adapter
        binding.title.text = resources.getQuantityString(R.plurals.like, votesCount, votesCount)
        binding.back.setOnClickListener {
            fragmentManager?.popBackStack()
        }

        viewModel.data.observe(this, Observer {
            binding.loading = false
            adapter.submitList(it)
        })

        viewModel.getState().observe(this, Observer {
            if (it == State.ERROR) {
                //Todo do something
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        mListener.invoke()
    }

    companion object {
        @JvmStatic
        fun newInstance(id: Int, votesCount : Int) =
            VotesFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_ID, id)
                    putInt(ARG_VOTES_COUNT, votesCount)
                }
            }
    }
}
