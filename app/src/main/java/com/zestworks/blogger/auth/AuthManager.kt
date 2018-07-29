package com.zestworks.blogger.auth

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import androidx.annotation.NonNull
import net.openid.appauth.*
import org.json.JSONException
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock


class AuthManager private constructor(context: Context) {

    private val mPrefsLock: ReentrantLock = ReentrantLock()
    private val mPrefs: SharedPreferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val TAG = "AuthStateManager"
        private const val STORE_NAME = "AuthState"
        private const val KEY_STATE = "state"

        const val clientID = "358134281219-d56hjmov5jf432mfsrmidj4hjh6o7c3k.apps.googleusercontent.com"
        const val reDirectUriPath = "com.zestworks.blogger:/oauth2redirect"
        const val action = "com.zestworks.blogger.HANDLE_AUTHORIZATION_RESPONSE"

        private val AUTH_MANAGER_REF = AtomicReference(WeakReference<AuthManager>(null))
        private lateinit var mCurrentAuthState: AtomicReference<AuthState>


        fun getInstance(context: Context): AuthManager {
            var manager = AUTH_MANAGER_REF.get().get()
            if (manager == null) {
                manager = AuthManager(context.applicationContext)
                AUTH_MANAGER_REF.set(WeakReference(manager))
            }

            return manager
        }
    }

    init {
        mCurrentAuthState = AtomicReference()
    }

    @AnyThread
    internal fun getCurrent(): AuthState {
        if (mCurrentAuthState.get() != null) {
            return mCurrentAuthState.get()
        }

        val state = readState()
        return if (mCurrentAuthState.compareAndSet(null, state)) {
            state
        } else {
            mCurrentAuthState.get()
        }
    }

    @AnyThread
    private fun updateAfterAuthorization(response: AuthorizationResponse?, ex: AuthorizationException?): AuthState {
        val current = getCurrent()
        current.update(response, ex)
        return replace(current)
    }

    @AnyThread
    fun updateAfterTokenResponse(response: TokenResponse?, ex: AuthorizationException?): AuthState {
        val current = getCurrent()
        current.update(response, ex)
        return replace(current)
    }

    @AnyThread
    private fun replace(state: AuthState): AuthState {
        writeState(state)
        mCurrentAuthState.set(state)
        return state
    }

    @AnyThread
    private fun readState(): AuthState {
        mPrefsLock.lock()
        try {
            val currentState = mPrefs.getString(KEY_STATE, null) ?: return AuthState()

            return try {
                AuthState.fromJson(currentState)
            } catch (ex: JSONException) {
                AuthState()
            }

        } finally {
            mPrefsLock.unlock()
        }
    }

    @AnyThread
    private fun writeState(state: AuthState?) {
        mPrefsLock.lock()
        try {
            val editor = mPrefs.edit()
            if (state == null) {
                editor.remove(KEY_STATE)
            } else {
                editor.putString(KEY_STATE, state.toJsonString())
            }

            if (!editor.commit()) {
                throw IllegalStateException("Failed to write state to shared prefs")
            }
        } finally {
            mPrefsLock.unlock()
        }
    }

    internal fun createAuthorizationService(context: Context) {
        val serviceConfiguration = AuthorizationServiceConfiguration(
                Uri.parse("https://accounts.google.com/o/oauth2/v2/auth") /* auth endpoint */,
                Uri.parse("https://www.googleapis.com/oauth2/v4/token") /* token endpoint */
        )

        val builder = AuthorizationRequest.Builder(serviceConfiguration, clientID, AuthorizationRequest.RESPONSE_TYPE_CODE, Uri.parse(reDirectUriPath))
        builder.setScope("https://www.googleapis.com/auth/blogger")
        val request = builder.build()

        val authorizationService = AuthorizationService(context)

        val postAuthorizationIntent = Intent(action)
        val pendingIntent = PendingIntent.getActivity(context, request.hashCode(), postAuthorizationIntent, 0)
        authorizationService.performAuthorizationRequest(request, pendingIntent)

    }

    internal fun handleAuthorizationResponse(context: Context,intent: Intent) {
        val response = AuthorizationResponse.fromIntent(intent)
        val error = AuthorizationException.fromIntent(intent)

        if (response != null || error != null) {
            updateAfterAuthorization(response, error)
        }

        if (response?.authorizationCode != null) {
            updateAfterAuthorization(response, error)
            performTokenRequest(context,response.createTokenExchangeRequest())
        }
    }

    private fun performTokenRequest(context: Context, tokenRequest: TokenRequest) {
        val authorizationService = AuthorizationService(context)
        authorizationService.performTokenRequest(tokenRequest) { tokenResponse, authException -> updateAfterTokenResponse(tokenResponse, authException) }
    }
}