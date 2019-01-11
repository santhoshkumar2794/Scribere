package com.zestworks.blogger.model.repository

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.zestworks.blogger.DBHelper
import com.zestworks.blogger.model.Blog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class BlogRepository : Repository {

    override fun getBlogList(): LiveData<PagedList<Blog>> {
        val blogList = DBHelper.blogDatabase.blogDao().getBlogList()
        val config = PagedList.Config.Builder()
                .setPageSize(10)
                .setPrefetchDistance(10)
                .setEnablePlaceholders(true)
                .build()
        return LivePagedListBuilder(blogList, config).build()
    }

    override fun insertBlog(blog: Blog) {
        GlobalScope.launch {
            DBHelper.blogDatabase.blogDao().insert(blog)
        }.start()
    }
}