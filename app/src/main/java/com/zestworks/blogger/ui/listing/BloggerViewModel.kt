package com.zestworks.blogger.ui.listing

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.zestworks.blogger.model.Blog
import com.zestworks.blogger.model.repository.BlogRepository
import com.zestworks.blogger.model.repository.Repository

class BloggerViewModel : ViewModel() {
    private var blogRepository: Repository = BlogRepository()

    fun getBlogList(): LiveData<PagedList<Blog>> {
        return blogRepository.getBlogList()
    }

    fun insertBlog(blog: Blog){
        blogRepository.insertBlog(blog)
    }
}
