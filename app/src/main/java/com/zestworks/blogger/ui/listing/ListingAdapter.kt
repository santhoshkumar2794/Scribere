package com.zestworks.blogger.ui.listing

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.zestworks.blogger.R
import com.zestworks.blogger.model.Blog
import kotlinx.android.synthetic.main.listing_holder.view.*


class ListingAdapter(context: Context, diffUtil: DiffUtil.ItemCallback<Blog>) : PagedListAdapter<Blog, ListingAdapter.ListingHolder>(diffUtil) {

    private val holderWidth: Int

    init {
        holderWidth = obtainHolderWidth(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListingHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listing_holder, parent, false)
        return ListingHolder(view)
    }

    override fun onBindViewHolder(holder: ListingHolder, position: Int) {
        val blog = getItem(position)
        holder.itemView.blog_content.text = blog!!.content
    }

    private fun obtainHolderWidth(context: Context): Int {
        val displayMetrics = context.resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels
        return Math.round(dpWidth / 2.5f) //Assuming Number of column is 2 and adding 0.5 as margin
    }

    inner class ListingHolder(view: View) : RecyclerView.ViewHolder(view){

        init {
            val layoutParams = view.blog_holder.layoutParams
            layoutParams.width = holderWidth
            view.blog_holder.layoutParams = layoutParams
        }
    }
}