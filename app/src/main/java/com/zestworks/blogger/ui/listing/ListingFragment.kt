package com.zestworks.blogger.ui.listing

import android.media.MediaPlayer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.zestworks.blogger.R
import com.zestworks.blogger.auth.AuthManager
import com.zestworks.blogger.ui.compose.ComposeFragment
import kotlinx.android.synthetic.main.listing_fragment.*
import kotlinx.coroutines.experimental.launch

class ListingFragment : Fragment() {

    companion object {
        fun newInstance() = ListingFragment()
    }

    private lateinit var viewModel: BloggerViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.listing_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        create_new.setOnClickListener {
            val authManager = AuthManager.getInstance(context!!)

            if (!authManager.getCurrent().isAuthorized) {
                authManager.createAuthorizationService(context!!)
            } else {
                openComposeFragment()
            }
        }
    }

    private fun openComposeFragment() {
        val composeFragment = ComposeFragment.newInstance()
        activity!!.supportFragmentManager.beginTransaction().replace(R.id.container, composeFragment, "COMPOSE_FRAGMENT").addToBackStack(null).commit()
        activity!!.supportFragmentManager.executePendingTransactions()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(BloggerViewModel::class.java)

        viewModel.getBlogList().observe(this, Observer {
            Log.e("blog", "LIST  " + it?.size)
        })
    }


}
