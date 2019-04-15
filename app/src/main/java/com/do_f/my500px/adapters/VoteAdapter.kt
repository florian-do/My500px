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
import com.do_f.my500px.api.model.User
import com.do_f.my500px.databinding.AdapterVoteBinding

class VoteAdapter(private val glide: RequestManager,
                     private val orientation: Int)
    : PagedListAdapter<User, VoteAdapter.ViewHolder>(diffCallback) {

    val TAG = "Adapter"

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val binding : AdapterVoteBinding = DataBindingUtil.inflate(
            LayoutInflater.from(p0.context),
            R.layout.adapter_vote,
            null,
            false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        getItem(p1)?.let { item ->
            glide.load(item.avatars.default.https)
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                .apply(RequestOptions.circleCropTransform())
                .into(p0.binding.avatar)

            p0.binding.followers.text = p0.binding.followers.resources.getQuantityString(R.plurals.follower, item.followers_count, item.followers_count)
            p0.binding.name.text = item.fullname
        }
    }

    class ViewHolder(val binding : AdapterVoteBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        var diffCallback = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(p0: User, p1: User): Boolean {
                return p0 == p1
            }

            override fun areContentsTheSame(p0: User, p1: User): Boolean {
                return p0.id == p1.id
            }
        }
    }
}