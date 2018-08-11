package com.zestworks.blogger.ui.compose

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zestworks.blogger.CreateBlogFragment
import com.zestworks.blogger.R

class ComposeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.compose_activity)

        if (savedInstanceState == null) {
            val createBlogFragment = CreateBlogFragment.newInstance()
            supportFragmentManager.beginTransaction().replace(R.id.compose_container, createBlogFragment, "CREATE_BLOG_FRAGMENT").commitNow()
        }
    }
}