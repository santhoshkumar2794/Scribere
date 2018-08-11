package com.zestworks.blogger.ui.blog_uploader

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.api.services.blogger.model.Blog
import com.zestworks.blogger.R
import com.zestworks.blogger.ui.compose.BlogListCallback
import kotlinx.android.synthetic.main.blog_list_holder.view.*

class BlogListAdapter(private val blogList: List<Blog>, private val blogListCallback: BlogListCallback) : RecyclerView.Adapter<BlogListAdapter.BlogListHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogListHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.blog_list_holder, parent, false)
        return BlogListHolder(view)
    }

    override fun onBindViewHolder(holder: BlogListHolder, position: Int) {
        val blog = blogList[position]

        holder.index = position
        holder.itemView.blog_title.text = blog.name
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