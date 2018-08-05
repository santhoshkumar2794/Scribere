package com.zestworks.blogger

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.zestworks.blogger.auth.AuthManager
import com.zestworks.blogger.ui.listing.ListingFragment
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration


class ListingActivity : AppCompatActivity() {

    companion object {
        public const val USED_INTENT = "USED_INTENT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.listing_activity)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.container, ListingFragment.newInstance(),"LISTING_FRAGMENT")
                    .commitNow()
        }
    }

}
