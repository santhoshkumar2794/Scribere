package com.zestworks.blogger.ui.compose

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zestworks.blogger.Constants
import com.zestworks.blogger.CreateBlogFragment
import com.zestworks.blogger.ListingActivity
import com.zestworks.blogger.R
import com.zestworks.blogger.auth.AuthManager
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.TokenRequest

class ComposeActivity : AppCompatActivity() {

    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.compose_activity)

        authManager = AuthManager.getInstance(this)
        if (savedInstanceState == null) {
            val createBlogFragment = CreateBlogFragment.newInstance()
            supportFragmentManager.beginTransaction().replace(R.id.compose_container, createBlogFragment, "CREATE_BLOG_FRAGMENT").commitNow()
        }
    }

    override fun onStart() {
        super.onStart()
        checkIntent(intent)
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

            val blogSelectionFragment = supportFragmentManager.findFragmentByTag(Constants.COMPOSE_FRAGMENT) as? ComposeFragment
            blogSelectionFragment?.showBlogSelectionFragment()
        }
    }
}