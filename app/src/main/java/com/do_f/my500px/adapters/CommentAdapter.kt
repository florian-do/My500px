package com.do_f.my500px.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.do_f.my500px.R
import com.do_f.my500px.api.model.Comment
import com.do_f.my500px.databinding.AdapterCommentBinding
import com.do_f.my500px.parseDate

class CommentAdapter(private val glide: RequestManager)
    : PagedListAdapter<Comment, CommentAdapter.ViewHolder>(diffCallback) {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val binding : AdapterCommentBinding = DataBindingUtil.inflate(
            LayoutInflater.from(p0.context),
            R.layout.adapter_comment,
            null,
            false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        getItem(p1)?.let { item ->
            glide.load(item.user.avatars.default.https)
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                .apply(RequestOptions.circleCropTransform())
                .into(p0.binding.avatar)

            p0.binding.comment.text = item.body
            p0.binding.name.text = item.user.fullname
            p0.binding.timestamp.text = item.created_at.parseDate(p0.binding.timestamp.resources)
        }
    }

    class ViewHolder(val binding : AdapterCommentBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        var diffCallback = object : DiffUtil.ItemCallback<Comment>() {
            override fun areItemsTheSame(p0: Comment, p1: Comment): Boolean {
                return p0 == p1
            }

            override fun areContentsTheSame(p0: Comment, p1: Comment): Boolean {
                return p0.id == p1.id
            }
        }
    }
}