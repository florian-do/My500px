package com.do_f.my500px.ui.dialogfragment


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide

import com.do_f.my500px.R
import com.do_f.my500px.adapters.CommentAdapter
import com.do_f.my500px.base.BDialogFragment
import com.do_f.my500px.base.BFragment
import com.do_f.my500px.viewmodel.CommentsViewModel
import kotlinx.android.synthetic.main.fragment_comments.*

class CommentsFragment : BDialogFragment() {

    private val TAG = "CommentsFragment"
    private val ARG_ID = "arg_id"
    private val ARG_COMMENTS_COUNT = "arg_comments_count"

    private var commentsCount: Int = 0
    private var pictureId: Int = 0
    private lateinit var adapter : CommentAdapter
    private lateinit var viewModel : CommentsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            pictureId = it.getInt(ARG_ID)
            commentsCount = it.getInt(ARG_COMMENTS_COUNT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        adapter = CommentAdapter(Glide.with(this), 0)
        viewModel = ViewModelProviders.of(this).get(CommentsViewModel::class.java)
        var startPage = (commentsCount / 20)
        if ((commentsCount % 20) != 0) startPage++
        Log.d(TAG, "createVIew: ${pictureId}")
        viewModel.init(pictureId, startPage)
        return inflater.inflate(R.layout.fragment_comments, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val lm = LinearLayoutManager(context)
        lm.reverseLayout = true
        rvFeed.layoutManager = lm
        rvFeed.adapter = adapter
        title.text = getString(R.string.number_comments, commentsCount)

        viewModel.data.observe(this, Observer {
            adapter.submitList(it)
        })
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
