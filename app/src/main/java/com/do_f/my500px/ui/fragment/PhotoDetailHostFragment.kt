package com.do_f.my500px.ui.fragment

import android.arch.paging.PagedList
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

import com.do_f.my500px.R
import com.do_f.my500px.api.model.Photo
import com.do_f.my500px.base.BFragment
import com.do_f.my500px.singleton.DataHolder
import kotlinx.android.synthetic.main.fragment_photo_detail_host.*

class PhotoDetailHostFragment : BFragment() {

    private val ARG_ITEM = "arg_item"

    private lateinit var item: Photo
    private lateinit var data : PagedList<Photo>

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            item = it.getSerializable(ARG_ITEM) as Photo
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_photo_detail_host, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        data = DataHolder.instance.data
        systemUIListener?.isSystemUIHidden(true)

        mSectionsPagerAdapter = SectionsPagerAdapter(childFragmentManager)
        container.adapter = mSectionsPagerAdapter
        container.currentItem = data.indexOf(item)
        container.pageMargin = resources.getDimension(R.dimen.viewpager_margin).toInt()
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            data[position]?.let {
                Glide.with(this@PhotoDetailHostFragment)
                    .load(it.images[0].https_url)
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE))
                    .preload()
                return PhotoDetailFragment.newInstance(it, position)
            }
            return PhotoDetailFragment.newInstance(item, position)
        }

        override fun getCount(): Int {
            return data.size
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(item: Photo) =
            PhotoDetailHostFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_ITEM, item)
                }
            }
    }
}
