package com.zestworks.blogger.ui.blog_uploader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.MainThread
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.blogger.Blogger
import com.google.api.services.blogger.BloggerScopes
import com.google.api.services.blogger.model.Blog
import com.zestworks.blogger.R
import com.zestworks.blogger.auth.AuthManager
import com.zestworks.blogger.ui.compose.BlogListCallback
import kotlinx.android.synthetic.main.blog_selection_fragment.*
import kotlinx.android.synthetic.main.blog_selection_fragment.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationService

class BlogSelectionFragment : BottomSheetDialogFragment() {

    private lateinit var authorizationService: AuthorizationService
    private lateinit var authManager: AuthManager

    var blogListCallback: BlogListCallback? = null

    companion object {
        fun newInstance() = BlogSelectionFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.blog_selection_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        authManager = AuthManager.getInstance(context!!)
        authorizationService = AuthorizationService(context!!)

        fetchBlogList()
    }

    private fun fetchBlogList() {

        blog_selection_container.visibility = View.GONE
        blog_selection_loader.visibility = View.VISIBLE
        blog_selection_loader.progress_message.text = getString(R.string.fetching_list_blog)

        authManager.getCurrent().performActionWithFreshTokens(authorizationService) { accessToken, idToken, ex ->
            val googleCredential = GoogleCredential()
            googleCredential.accessToken = accessToken
            googleCredential.createScoped(arrayListOf(BloggerScopes.BLOGGER))

            val netHttpTransport = NetHttpTransport()
            val jacksonFactory = JacksonFactory()

            val blogger = Blogger.Builder(netHttpTransport, jacksonFactory, googleCredential)
            blogger.applicationName = "Blogger-PostsInsert-Snippet/1.0"


            GlobalScope.launch {
                val listByUser = blogger.build().blogs().listByUser("self")
                val blogList = listByUser.execute()

                view?.post {
                    displayBlogList(blogList.items)
                }
            }
        }
    }

    @MainThread
    private fun displayBlogList(blogList: List<Blog>) {
        blog_selection_listing.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        blog_selection_listing.adapter = BlogListAdapter(blogList, blogListCallback!!)

        blog_selection_container.visibility = View.VISIBLE
        blog_selection_loader.visibility = View.GONE
    }

}