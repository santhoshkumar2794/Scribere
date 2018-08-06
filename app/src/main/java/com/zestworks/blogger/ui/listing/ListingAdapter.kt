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
        holder.index = position
        holder.updateLayoutParams()

        holder.itemView.blog_title.text = blog?.title
    }

    private fun obtainHolderWidth(context: Context): Int {
        val displayMetrics = context.resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels
        return Math.round(dpWidth / 2.5f) //Assuming Number of column is 2 and adding 0.5 as margin
    }

    inner class ListingHolder(view: View) : RecyclerView.ViewHolder(view) {
        var index: Int = 0

        init {
            updateLayoutParams()
        }

        internal fun updateLayoutParams() {
            val dimension = itemView.context.resources.getDimension(R.dimen.listing_title_margin)

            val layoutParams = itemView.blog_holder.layoutParams as RecyclerView.LayoutParams
            layoutParams.width = holderWidth
            //layoutParams.topMargin = (dimension * 1.5f).toInt()
            layoutParams.bottomMargin = (dimension * 1.5f).toInt()
            if (index % 2 == 0) {
                layoutParams.leftMargin = (dimension * 1.5f).toInt()
                layoutParams.rightMargin = 0
            } else {
                layoutParams.leftMargin = (dimension * 1f).toInt()
                layoutParams.rightMargin = 0
            }
            itemView.blog_holder.layoutParams = layoutParams
        }
    }
}