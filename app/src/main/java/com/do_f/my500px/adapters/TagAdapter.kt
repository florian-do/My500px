package com.do_f.my500px.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.do_f.my500px.R
import com.do_f.my500px.databinding.AdapterTagBinding

class TagAdapter : RecyclerView.Adapter<TagAdapter.ViewHolder>() {

    var items : List<String> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding : AdapterTagBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.adapter_tag,
            null,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tag.text = items[position]
    }

    class ViewHolder(val binding: AdapterTagBinding) : RecyclerView.ViewHolder(binding.root)
}