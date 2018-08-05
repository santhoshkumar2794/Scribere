package com.zestworks.blogger.ui.compose

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.*
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.blogger.Blogger
import com.google.api.services.blogger.BloggerScopes
import com.google.api.services.blogger.model.Post
import com.zestworks.blogger.Constants
import com.zestworks.blogger.R
import com.zestworks.blogger.auth.AuthManager
import com.zestworks.blogger.model.Blog
import com.zestworks.blogger.ui.create_new.Template
import com.zestworks.blogger.ui.listing.BloggerViewModel
import kotlinx.android.synthetic.main.compose_fragment.*
import kotlinx.coroutines.experimental.launch


class ComposeFragment : Fragment(), ComposerCallback {

    private val selectedColor: Int = Color.RED

    private lateinit var viewModel: BloggerViewModel
    private lateinit var authManager: AuthManager
    private lateinit var template: Template

    private val blog = Blog()

    companion object {
        fun newInstance() = ComposeFragment()
    }

    init {
        setHasOptionsMenu(true)
    }

    override fun setArguments(args: Bundle?) {
        super.setArguments(args)
        blog.title = args?.get(Constants.BLOG_TITLE).toString()
        blog.columnID = args?.getInt(Constants.BLOG_ID, blog.columnID)!!

        template = when (args.get(Constants.BLOG_TEMPLATE)) {
            Template.TITLE_WITH_CONTENT.toString() -> Template.TITLE_WITH_CONTENT
            Template.TITLE_IMAGE_CONTENT.toString() -> Template.TITLE_IMAGE_CONTENT
            else -> Template.BLANK_TEMPLATE
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return when (template) {
            Template.TITLE_WITH_CONTENT -> inflater.inflate(R.layout.compose_fragment, container, false)
            Template.TITLE_IMAGE_CONTENT -> inflater.inflate(R.layout.compose_fragment, container, false)
            else -> inflater.inflate(R.layout.compose_fragment, container, false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()

        text_editor.text = constructBlogContent()
        text_editor.composerCallback = this

        bold_button.setOnClickListener {
            if (!it.isSelected) {
                text_editor.applyProps(Composer.PROPS.BOLD)
            } else {
                text_editor.removeProps(Composer.PROPS.BOLD)
            }
        }

        italic_button.setOnClickListener {
            if (!it.isSelected) {
                text_editor.applyProps(Composer.PROPS.ITALICS)
            } else {
                text_editor.removeProps(Composer.PROPS.ITALICS)
            }
        }

        underline_button.setOnClickListener {
            if (!it.isSelected) {
                text_editor.applyProps(Composer.PROPS.UNDERLINE)
            } else {
                text_editor.removeProps(Composer.PROPS.UNDERLINE)
            }
        }
    }

    private fun setupToolbar() {
        val appCompatActivity = activity as? AppCompatActivity
        appCompatActivity?.setSupportActionBar(composer_toolbar)
        composer_toolbar.title = blog.title
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.composer_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item?.itemId){
            R.id.publish -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(BloggerViewModel::class.java)


        /*viewModel.getBlogList().observe(this, Observer {
            if (it?.size!! >0){
                val blog = it[0]
                val listType = object : TypeToken<ArrayList<StyleSpanData>>() {}.type

                val spanDataList = Gson().fromJson<ArrayList<StyleSpanData>>(blog?.content?.getSpanData(), listType)
                Log.e("SpanData","Size  ${spanDataList.size}")
                text_editor.setText(blog?.content?.getPlainText())

                val spanned = HtmlCompat.fromHtml(blog?.content?.getHtmlText()!!, HtmlCompat.FROM_HTML_MODE_LEGACY)
                val spans = spanned.getSpans(0, spanned.length, StyleSpan::class.java)
                val stringBuilder = SpannableStringBuilder(spanned.toString())
                for (span in spans) {
                    spanned.getSpanStart(span)
                    stringBuilder.setSpan(span,spanned.getSpanStart(span),spanned.getSpanEnd(span),Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                }
                text_editor.text = stringBuilder

            }
        })*/
    }

    override fun onStop() {
        updateBlog()
        viewModel.insertBlog(blog)

        super.onStop()
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        updateEditTools(selStart, selEnd)
    }

    private fun constructBlogContent(): SpannableStringBuilder {
        if (blog.content == null) {
            return SpannableStringBuilder()
        }
        val spanned = HtmlCompat.fromHtml(blog.content!!, HtmlCompat.FROM_HTML_MODE_LEGACY)
        val spans = spanned.getSpans(0, spanned.length, StyleSpan::class.java)
        val stringBuilder = SpannableStringBuilder(spanned.toString())
        for (span in spans) {
            spanned.getSpanStart(span)
            stringBuilder.setSpan(span, spanned.getSpanStart(span), spanned.getSpanEnd(span), Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        }
        return stringBuilder
    }

    private fun updateBlog() {
        blog.content = HtmlCompat.toHtml(text_editor.text!!, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    private fun updateEditTools(selStart: Int, selEnd: Int) {
        val style = text_editor.getStyle(selStart, selEnd)
        setToolState(bold_button, style.bold)
        setToolState(italic_button, style.italics)
        setToolState(underline_button, style.underline)
    }

    private fun setToolState(icon: ImageButton, state: Boolean) {
        if (state) {
            icon.isSelected = true
            icon.setColorFilter(selectedColor)
        } else {
            icon.isSelected = false
            icon.colorFilter = null
        }
    }

    private fun resetEditTools() {
        bold_button.isSelected = false
        bold_button.colorFilter = null

        italic_button.isSelected = false
        italic_button.colorFilter = null

        underline_button.isSelected = false
        underline_button.colorFilter = null
    }

    private fun uploadBlogs() {

        val instance = AuthManager.getInstance(context!!)


        val googleCredential = GoogleCredential()
        googleCredential.accessToken = instance.getCurrent().accessToken
        googleCredential.createScoped(arrayListOf(BloggerScopes.BLOGGER))


        val netHttpTransport = NetHttpTransport()
        val jacksonFactory = JacksonFactory()

        val blogger = Blogger.Builder(netHttpTransport, jacksonFactory, googleCredential)
        blogger.applicationName = "Blogger-PostsInsert-Snippet/1.0"

        val content = Post()
        content.title = blog.title
        content.content = blog.content


        launch {
            val postsInsertAction = blogger.build().posts().insert("734820219569993445", content)
            postsInsertAction.isDraft = true
            postsInsertAction.execute()
        }
    }
}

