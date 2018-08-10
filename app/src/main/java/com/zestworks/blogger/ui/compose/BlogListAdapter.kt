package com.zestworks.blogger.ui.compose

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.api.services.blogger.model.Blog

class BlogListAdapter(private val blogList: List<Blog>, private val blogListCallback: BlogListCallback) : RecyclerView.Adapter<BlogListAdapter.BlogListHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogListHolder {
        val textView = TextView(parent.context)
        return BlogListHolder(textView)
    }

    override fun onBindViewHolder(holder: BlogListHolder, position: Int) {
        val blog = blogList[position]
        val layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, 100)
        holder.itemView.layoutParams = layoutParams

        holder.index = position
        (holder.itemView as TextView).text = blog.name
        holder.itemView.textSize = 20f
    }

    override fun getItemCount(): Int = blogList.size

    inner class BlogListHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        var index: Int = 0

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            blogListCallback.onBlogSelected(blogList[index].id)
        }
    }
}