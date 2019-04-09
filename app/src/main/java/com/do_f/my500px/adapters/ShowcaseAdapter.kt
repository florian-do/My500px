package com.do_f.my500px.adapters

import android.arch.paging.PagedListAdapter
import android.content.res.Resources
import android.databinding.DataBindingUtil
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.do_f.my500px.R
import com.do_f.my500px.api.model.Photo
import com.do_f.my500px.api.model.getRatio
import com.do_f.my500px.databinding.AdapterShowcaseBinding

class ShowcaseAdapter(private val glide: RequestManager, val mListener: (Photo) -> Unit)
    : PagedListAdapter<Photo, ShowcaseAdapter.ViewHolder>(diffCallback) {

    private val windowWidth: Float = Resources.getSystem().displayMetrics.widthPixels.toFloat()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val binding : AdapterShowcaseBinding = DataBindingUtil.inflate(
            LayoutInflater.from(p0.context),
            R.layout.adapter_showcase,
            null,
            false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        getItem(p1)?.let { item ->
            val imageRatio = item.getRatio()

            if (item.width > item.height) {
                p0.binding.picture.layoutParams.height = (windowWidth / imageRatio).toInt()
                p0.binding.picture.layoutParams.width = windowWidth.toInt()
            } else {
                p0.binding.picture.layoutParams.height = (windowWidth * imageRatio).toInt()
                p0.binding.picture.layoutParams.width = windowWidth.toInt()
            }

            p0.binding.title.text = item.name
            p0.binding.likesCount.text = item.votes_count.toString()
            p0.binding.commentsCount.text = item.comments_count.toString()
            p0.binding.author.apply {
                text = resources.getString(R.string.showcase_author, item.user.fullname)
            }

            glide.load(item.image_url[0]).into(p0.binding.picture)
            glide.load(item.user.avatars.default.https)
                    .apply(RequestOptions.circleCropTransform())
                    .into(p0.binding.avatar)

            p0.binding.picture.setOnClickListener {
                mListener.invoke(item)
            }
        }
    }

    class ViewHolder(val binding : AdapterShowcaseBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        var diffCallback = object : DiffUtil.ItemCallback<Photo>() {
            override fun areItemsTheSame(p0: Photo, p1: Photo): Boolean {
                return p0 == p1
            }

            override fun areContentsTheSame(p0: Photo, p1: Photo): Boolean {
                return p0.id == p1.id
            }
        }
    }
}