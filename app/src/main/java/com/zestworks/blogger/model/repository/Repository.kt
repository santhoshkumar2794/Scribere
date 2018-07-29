package com.zestworks.blogger.model.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.zestworks.blogger.model.Blog

interface Repository {

    fun getBlogList() : LiveData<PagedList<Blog>>

    fun insertBlog(blog: Blog)
}