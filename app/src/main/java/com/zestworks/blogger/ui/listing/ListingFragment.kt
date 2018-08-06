package com.zestworks.blogger.ui.listing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.zestworks.blogger.CreateBlogFragment
import com.zestworks.blogger.R
import com.zestworks.blogger.auth.AuthManager
import com.zestworks.blogger.model.Blog
import com.zestworks.blogger.ui.compose.ComposeFragment
import kotlinx.android.synthetic.main.listing_fragment.*

class ListingFragment : Fragment() {

    private lateinit var listingAdapter: ListingAdapter

    companion object {
        fun newInstance() = ListingFragment()
    }

    private lateinit var viewModel: BloggerViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.listing_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListing()
        create_new.setOnClickListener {
            openCreateNew()
        }
    }

    private fun setupListing() {
        listingAdapter = ListingAdapter(context!!, object : DiffUtil.ItemCallback<Blog>() {
            override fun areItemsTheSame(oldItem: Blog, newItem: Blog): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }

            override fun areContentsTheSame(oldItem: Blog, newItem: Blog): Boolean {
                return oldItem.columnID == newItem.columnID
            }
        })
        listing_recycler_view.adapter = listingAdapter
        listing_recycler_view.layoutManager = GridLayoutManager(context!!, 2)//LinearLayoutManager(context!!, LinearLayoutManager.VERTICAL, false)
    }

    private fun openCreateNew() {
        val createBlogFragment = CreateBlogFragment.newInstance()
        activity!!.supportFragmentManager.beginTransaction().replace(R.id.container, createBlogFragment, "CREATE_BLOG_FRAGMENT").addToBackStack(null).commit()
        activity!!.supportFragmentManager.executePendingTransactions()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(BloggerViewModel::class.java)

        viewModel.getBlogList().observe(this, Observer {
            listingAdapter.submitList(it)
        })
    }


}
