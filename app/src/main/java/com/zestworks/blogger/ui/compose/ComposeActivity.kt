package com.zestworks.blogger.ui.compose

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AppCompatActivity
import com.zestworks.blogger.Constants
import com.zestworks.blogger.R
import com.zestworks.blogger.auth.AuthManager
import kotlinx.coroutines.experimental.launch
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference

class ComposeActivity : AppCompatActivity() {

    private lateinit var authManager: AuthManager
    private var executor: ExecutorService = Executors.newSingleThreadExecutor()

    private val mAuthRequest = AtomicReference<AuthorizationRequest>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.compose_activity)

        if (savedInstanceState == null) {
            val composeFragment = ComposeFragment.newInstance()
            composeFragment.arguments = intent.extras
            supportFragmentManager.beginTransaction().replace(R.id.compose_container, composeFragment, Constants.COMPOSE_FRAGMENT)
                    .commitNow()
        }

        /*val authManager = AuthManager.getInstance(applicationContext)

        if (!authManager.getCurrent().isAuthorized) {
            executor.submit(this::createAuthorizationService)
        }*/
    }

    @WorkerThread
    private fun createAuthorizationService() {
        val serviceConfiguration = AuthorizationServiceConfiguration(
                Uri.parse("https://accounts.google.com/o/oauth2/v2/auth") /* auth endpoint */,
                Uri.parse("https://www.googleapis.com/oauth2/v4/token") /* token endpoint */
        )


        val builder = AuthorizationRequest.Builder(serviceConfiguration, AuthManager.clientID, AuthorizationRequest.RESPONSE_TYPE_CODE, Uri.parse(AuthManager.reDirectUriPath))
        builder.setScope("https://www.googleapis.com/auth/blogger")

        mAuthRequest.set(builder.build())
    }
}