package com.zestworks.blogger.ui.compose

interface ComposerCallback {
    fun onSelectionChanged(selStart : Int, selEnd : Int)
}

interface BlogListCallback{
    fun onBlogSelected(blogID : String)
}