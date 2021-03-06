package com.zestworks.blogger

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.zestworks.blogger.ui.compose.ComposeFragment
import com.zestworks.blogger.ui.create_new.Template
import com.zestworks.blogger.ui.create_new.TemplateChooser
import com.zestworks.blogger.ui.create_new.TemplateSelector
import kotlinx.android.synthetic.free.create_blog_fragment.*

class CreateBlogFragment : Fragment(), TemplateSelector {

    var template: Template? = null
    lateinit var templateChooser: TemplateChooser

    companion object {
        fun newInstance() = CreateBlogFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.create_blog_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        template_selector.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)

        val templateIDs = arrayOf(Template.BLANK_TEMPLATE)
        templateChooser = TemplateChooser(templateIDs, this)
        template_selector.adapter = templateChooser

        enableCreate()
        create_blog.setOnClickListener {
            openComposer()
        }

        blog_title.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                enableCreate()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })
    }

    override fun onTemplateSelected(template: Template) {
        this.template = template
        templateChooser.currentTemplate = template
        templateChooser.notifyDataSetChanged()
        enableCreate()
    }

    private fun enableCreate() {
        create_blog.isEnabled = blog_title.text!!.isNotEmpty() && template != null
    }

    private fun openComposer() {
        val bundle = Bundle()
        bundle.putString(Constants.BLOG_TITLE, blog_title.text.toString())
        bundle.putString(Constants.BLOG_TEMPLATE, this.template.toString())


        val composeFragment = ComposeFragment.newInstance()
        composeFragment.arguments = bundle
        activity!!.supportFragmentManager.beginTransaction().replace(R.id.compose_container, composeFragment, Constants.COMPOSE_FRAGMENT).commit()
        activity!!.supportFragmentManager.executePendingTransactions()
    }
}