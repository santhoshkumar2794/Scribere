package com.zestworks.blogger.ui.blog_uploader

import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.blogger.Blogger
import com.google.api.services.blogger.BloggerScopes
import com.google.api.services.blogger.model.Blog
import com.zestworks.blogger.Constants
import com.zestworks.blogger.ListingActivity
import com.zestworks.blogger.R
import com.zestworks.blogger.auth.AuthManager
import com.zestworks.blogger.ui.compose.BlogListCallback
import kotlinx.android.synthetic.main.blog_select_activity.*
import kotlinx.coroutines.experimental.launch
import net.openid.appauth.*
import java.util.concurrent.Executors

class BlogSelectActivity : AppCompatActivity(), BlogListCallback {

    private lateinit var authorizationService: AuthorizationService
    private lateinit var authManager: AuthManager

    private var executorService = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.blog_select_activity)

        authManager = AuthManager.getInstance(this)
        authorizationService = AuthorizationService(this)

        if (!authManager.getCurrent().isAuthorized) {
            executorService.submit(this::performAuthRequest)
        }else{
            fetchBlogList()
        }
    }

    @WorkerThread
    private fun performAuthRequest() {
        val serviceConfiguration = AuthorizationServiceConfiguration(
                Uri.parse("https://accounts.google.com/o/oauth2/v2/auth") /* auth endpoint */,
                Uri.parse("https://www.googleapis.com/oauth2/v4/token") /* token endpoint */
        )


        val builder = AuthorizationRequest.Builder(serviceConfiguration, AuthManager.clientID, AuthorizationRequest.RESPONSE_TYPE_CODE, Uri.parse(AuthManager.reDirectUriPath))
        builder.setScope("https://www.googleapis.com/auth/blogger")

        val authorizationRequest = builder.build()
        authManager.replace(authorizationRequest)

        val postAuthorizationIntent = Intent(AuthManager.action)
        val pendingIntent = PendingIntent.getActivity(this, authorizationRequest.hashCode(), postAuthorizationIntent, 0)
        authorizationService.performAuthorizationRequest(authorizationRequest, pendingIntent)
    }

    override fun onStart() {
        super.onStart()
        if (executorService.isShutdown) {
            executorService = Executors.newSingleThreadExecutor()
        }
        checkIntent(intent)
    }

    override fun onStop() {
        super.onStop()
        authorizationService.dispose()
        executorService.shutdown()
    }

    override fun onNewIntent(intent: Intent?) {
        checkIntent(intent)
    }

    private fun checkIntent(intent: Intent?) {
        when (intent!!.action) {
            AuthManager.action -> {
                if (!intent.hasExtra(ListingActivity.USED_INTENT)) {
                    handleAuthorizationResponse(intent)
                    intent.putExtra(ListingActivity.USED_INTENT, true)
                }
            }
        }
    }

    private fun handleAuthorizationResponse(intent: Intent) {
        val response = AuthorizationResponse.fromIntent(intent)
        val error = AuthorizationException.fromIntent(intent)

        if (response != null || error != null) {
            authManager.updateAfterAuthorization(response, error)
        }

        if (response?.authorizationCode != null) {
            authManager.updateAfterAuthorization(response, error)
            performTokenRequest(response.createTokenExchangeRequest())
        }
    }

    private fun performTokenRequest(tokenRequest: TokenRequest) {
        val authorizationService = AuthorizationService(this)
        authorizationService.performTokenRequest(tokenRequest) { tokenResponse, authException ->
            authManager.updateAfterTokenResponse(tokenResponse, authException)

            runOnUiThread {
                fetchBlogList()
            }
        }
    }

    private fun fetchBlogList() {
        loader.visibility = View.VISIBLE
        progress_message.text = getString(R.string.fetching_list_blog)

        authManager.getCurrent().performActionWithFreshTokens(authorizationService) { accessToken, idToken, ex ->
            val googleCredential = GoogleCredential()
            googleCredential.accessToken = accessToken
            googleCredential.createScoped(arrayListOf(BloggerScopes.BLOGGER))

            val netHttpTransport = NetHttpTransport()
            val jacksonFactory = JacksonFactory()

            val blogger = Blogger.Builder(netHttpTransport, jacksonFactory, googleCredential)
            blogger.applicationName = "Blogger-PostsInsert-Snippet/1.0"


            launch {
                val listByUser = blogger.build().blogs().listByUser("self")
                val blogList = listByUser.execute()
                runOnUiThread {
                    displayBlogList(blogList.items)
                }
            }
        }
    }

    private fun displayBlogList(blogList: List<Blog>) {
        blog_list_view.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        blog_list_view.adapter = BlogListAdapter(blogList, this)

        loader.visibility = View.GONE
    }

    override fun onBlogSelected(blogID: String) {
        val intent = Intent()
        intent.putExtra(Constants.BLOG_ID, blogID)
        setResult(200, intent)
        finish()
    }
}