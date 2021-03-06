package com.do_f.my500px.adapters

import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target

import com.do_f.my500px.R
import com.do_f.my500px.api.model.Photo
import com.do_f.my500px.databinding.AdapterShowcaseBinding
import com.do_f.my500px.parseDate
import com.do_f.my500px.px
import com.do_f.my500px.setImageSizeFromRatioByWidth

class ShowcaseAdapter(private val glide: RequestManager,
                      private val orientation: Int,
                      private val mListener: (Photo) -> Unit,
                      private val commentListener: (Int, Int) -> Unit,
                      private val voteListener: (Int, Int) -> Unit)
    : PagedListAdapter<Photo, ShowcaseAdapter.ViewHolder>(diffCallback) {

    var position : Int = 0

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val binding : AdapterShowcaseBinding = DataBindingUtil.inflate(
            LayoutInflater.from(p0.context),
            R.layout.adapter_showcase,
            null,
            false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        position = p1
        getItem(p1)?.let { item ->
            var windowWidth = Resources.getSystem().displayMetrics.widthPixels.toFloat()
            when(orientation) {
                Configuration.ORIENTATION_LANDSCAPE -> {
                    windowWidth -= (32.px) * 2
                }
            }

            p0.binding.datetime.text = item.created_at.parseDate(p0.binding.datetime.resources)
            p0.binding.picture.setImageSizeFromRatioByWidth(windowWidth, item)
            p0.binding.title.text = item.name
            p0.binding.likesCount.text = item.votes_count.toString()
            p0.binding.commentsCount.text = item.comments_count.toString()
            p0.binding.author.apply {
                text = resources.getString(R.string.showcase_author, item.user.fullname)
            }

            glide.load(item.image_url[0])
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?, model: Any?,
                        target: Target<Drawable>?, isFirstResource: Boolean): Boolean = false

                    override fun onResourceReady(
                        resource: Drawable?, model: Any?,
                        target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        p0.binding.picture.animate().alpha(1F).setDuration(250).start()
                        return false
                    }
                })
                .into(p0.binding.picture)
            
            glide.load(item.user.avatars.default.https)
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .apply(RequestOptions.circleCropTransform())
                    .into(p0.binding.avatar)

            p0.binding.picture.setOnClickListener {
                mListener.invoke(getItem(p1)!!)
            }

            p0.binding.commentView.setOnClickListener {
                commentListener.invoke(item.id, item.comments_count)
            }

            p0.binding.likesCount.setOnClickListener {
                voteListener.invoke(item.id, item.votes_count)
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