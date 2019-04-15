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
import com.do_f.my500px.adapters.CommentAdapter
import com.do_f.my500px.base.BDialogFragment
import com.do_f.my500px.databinding.FragmentCommentsBinding
import com.do_f.my500px.enumdir.State
import com.do_f.my500px.viewmodel.CommentsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CommentsFragment : BDialogFragment() {

    private val ARG_ID = "arg_id"
    private val ARG_COMMENTS_COUNT = "arg_comments_count"

    private var commentsCount: Int = 0
    private var pictureId: Int = 0
    private lateinit var adapter : CommentAdapter
    private lateinit var viewModel : CommentsViewModel
    private lateinit var binding : FragmentCommentsBinding
    var mListener: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            pictureId = it.getInt(ARG_ID)
            commentsCount = it.getInt(ARG_COMMENTS_COUNT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_comments, container, false)
        adapter = CommentAdapter(Glide.with(this))
        viewModel = ViewModelProviders.of(this).get(CommentsViewModel::class.java)
        var startPage = (commentsCount / 20)
        if ((commentsCount % 20) != 0) startPage++
        viewModel.init(pictureId, startPage)
        binding.loading = true
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val lm = LinearLayoutManager(context)
        lm.reverseLayout = true
        binding.rvFeed.layoutManager = lm
        binding.rvFeed.adapter = adapter
        binding.title.text = resources.getQuantityString(R.plurals.comment, commentsCount, commentsCount)
        binding.back.setOnClickListener {
            dismiss()
        }
        viewModel.data.observe(this, Observer {
            adapter.submitList(it)
        })

        viewModel.getState().observe(this, Observer {
            GlobalScope.launch(context = Dispatchers.Main) {
                when (it) {
                    State.LOADING -> binding.loading = true
                    State.ERROR -> {
                        binding.loading = false
                        binding.error.visibility = View.VISIBLE
                    }
                    State.DONE -> {
                        binding.loading = false
                        binding.error.visibility = View.GONE
                    }
                    else -> { }
                }
            }
        })
    }

    fun refreshDatasource() {
        viewModel.dataSourceFactory.source?.invalidate()
    }

    override fun onDestroy() {
        super.onDestroy()
        mListener?.invoke()
    }

    companion object {
        @JvmStatic
        fun newInstance(id: Int, commentsCount: Int) =
            CommentsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_ID, id)
                    putInt(ARG_COMMENTS_COUNT, commentsCount)
                }
            }
    }
}
