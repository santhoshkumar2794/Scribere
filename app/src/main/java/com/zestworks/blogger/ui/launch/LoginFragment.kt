package com.zestworks.blogger.ui.launch

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.zestworks.blogger.Constants
import com.zestworks.blogger.R
import com.zestworks.blogger.ui.listing.ListingFragment
import kotlinx.android.synthetic.main.login_frgament.*


class LoginFragment : Fragment() {

    private lateinit var signInClient: GoogleSignInClient

    companion object {
        fun newInstance() = LoginFragment()
        private const val SIGN_IN_REQUEST_CODE = 257
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.login_frgament, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sign_in_button.setStyle(SignInButton.SIZE_WIDE, SignInButton.COLOR_DARK)
        sign_in_button.setOnClickListener {
            startActivityForResult(signInClient.signInIntent, SIGN_IN_REQUEST_CODE)
        }

        skip_button.setOnClickListener {
            val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
            with(sharedPref!!.edit()) {
                putBoolean(Constants.SIGN_IN_SKIP, true)
                commit()
            }
            launchListingFragment()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().requestIdToken("358134281219-1g4qdmnhtbn5bk8b0o73otemjk5ecs6f.apps.googleusercontent.com").build()
        signInClient = GoogleSignIn.getClient(context!!, signInOptions)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            val signedInAccount = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val result = signedInAccount.getResult(ApiException::class.java)
                if (result != null) {
                    launchListingFragment()
                }
            } catch (exception: ApiException) {
                exception.printStackTrace()
            }
        }
    }

    private fun launchListingFragment() {
        val supportFragmentManager = activity!!.supportFragmentManager
        supportFragmentManager.beginTransaction().replace(R.id.container, ListingFragment.newInstance(), "LISTING_FRAGMENT").commit()
        supportFragmentManager.executePendingTransactions()
    }
}