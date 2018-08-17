package com.zestworks.blogger

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.zestworks.blogger.ui.launch.LoginFragment
import com.zestworks.blogger.ui.listing.ListingFragment


class ListingActivity : AppCompatActivity() {

    companion object {
        public const val USED_INTENT = "USED_INTENT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.listing_activity)

        val signedInAccount = GoogleSignIn.getLastSignedInAccount(this)
        if (savedInstanceState == null) {
            if (signedInAccount == null) {
                supportFragmentManager.beginTransaction().replace(R.id.container, LoginFragment.newInstance(), "LOGIN_FRAGMENT").commitNow()
            } else {
                supportFragmentManager.beginTransaction().replace(R.id.container, ListingFragment.newInstance(), "LISTING_FRAGMENT").commitNow()
            }
        }
    }


}
