package com.zestworks.blogger

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.zestworks.blogger.ui.compose.ComposeFragment
import com.zestworks.blogger.ui.create_new.TemplateChooser
import com.zestworks.blogger.ui.create_new.TemplateSelector
import com.zestworks.blogger.ui.create_new.Template
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

        template_selector.layoutManager = GridLayoutManager(view.context, 2)

        val templateIDs = arrayOf(Template.BLANK_TEMPLATE, Template.TITLE_WITH_CONTENT)
        templateChooser = TemplateChooser(templateIDs, this)
        template_selector.adapter = templateChooser

        enableCreate()
        create_blog.setOnClickListener {
            openComposeFragment()
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

    private fun openComposeFragment() {
        val composeFragment = ComposeFragment.newInstance()

        val bundle = Bundle()
        bundle.putString("title", blog_title.text.toString())
        bundle.putString("template", this.template.toString())
        composeFragment.arguments = bundle

        activity!!.supportFragmentManager.beginTransaction().replace(R.id.container, composeFragment, "COMPOSE_FRAGMENT").commit()
        activity!!.supportFragmentManager.executePendingTransactions()
    }
}