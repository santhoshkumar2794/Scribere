package com.zestworks.blogger.ui.compose

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.*
import android.widget.ImageButton
import android.widget.Toast
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
import com.zestworks.blogger.ui.blog_uploader.BlogSelectActivity
import com.zestworks.blogger.ui.create_new.Template
import com.zestworks.blogger.ui.listing.BloggerViewModel
import kotlinx.android.synthetic.main.compose_fragment.*
import kotlinx.coroutines.experimental.launch
import net.openid.appauth.AuthorizationService
import java.util.concurrent.Executors


class ComposeFragment : Fragment(), ComposerCallback, BlogListCallback {

    private val selectedColor: Int = Color.RED

    private lateinit var viewModel: BloggerViewModel
    private lateinit var template: Template
    private var executorService = Executors.newSingleThreadExecutor()
    private val blog = com.zestworks.blogger.model.Blog()

    private var publishInProgress: Boolean = false

    private lateinit var authorizationService: AuthorizationService
    private lateinit var authManager: AuthManager

    companion object {
        fun newInstance() = ComposeFragment()
        const val BLOG_UPLOAD_REQUEST_CODE: Int = 123
    }

    init {
        retainInstance = true
        setHasOptionsMenu(true)
    }

    override fun setArguments(args: Bundle?) {
        super.setArguments(args)
        blog.title = args?.get(Constants.BLOG_TITLE).toString()

        template = when (args?.get(Constants.BLOG_TEMPLATE)) {
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

        setupEditToolbar()
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

    private fun setupEditToolbar() {
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

        strike_through_button.setOnClickListener {
            if (!it.isSelected) {
                text_editor.applyProps(Composer.PROPS.STRIKE_THROUGH)
            } else {
                text_editor.removeProps(Composer.PROPS.STRIKE_THROUGH)
            }
        }
    }

    private fun setupToolbar() {
        activity!!.title = blog.title
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.composer_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.publish -> {
                publishInProgress = true

                val intent = Intent(context!!, BlogSelectActivity::class.java)
                intent.putExtra(Constants.BLOG_COLUMN_ID, blog.columnID)
                startActivityForResult(intent, BLOG_UPLOAD_REQUEST_CODE)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(BloggerViewModel::class.java)
        authManager = AuthManager.getInstance(context!!)
        authorizationService = AuthorizationService(context!!)
    }

    override fun onStart() {
        super.onStart()
        if (executorService.isShutdown) {
            executorService = Executors.newSingleThreadExecutor()
        }
    }

    override fun onStop() {
        updateBlog()
        authorizationService.dispose()
        super.onStop()
    }

    override fun onDestroy() {
        viewModel.insertBlog(blog)
        executorService.shutdown()
        super.onDestroy()
    }

    private fun updateBlog() {
        blog.content = HtmlCompat.toHtml(text_editor.text!!, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    private fun updateEditTools(selStart: Int, selEnd: Int) {
        val style = text_editor.getStyle(selStart, selEnd)
        setToolState(bold_button, style.bold)
        setToolState(italic_button, style.italics)
        setToolState(underline_button, style.underline)
        setToolState(strike_through_button, style.strikeThrough)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == BLOG_UPLOAD_REQUEST_CODE) {
            onBlogSelected(data?.getStringExtra(Constants.BLOG_ID)!!)
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        updateEditTools(selStart, selEnd)
    }

    override fun onBlogSelected(blogID: String) {

        blog_list_view.visibility = View.GONE

        authManager.getCurrent().performActionWithFreshTokens(authorizationService) { accessToken, idToken, ex ->
            val googleCredential = GoogleCredential()
            googleCredential.accessToken = accessToken
            googleCredential.createScoped(arrayListOf(BloggerScopes.BLOGGER))

            val netHttpTransport = NetHttpTransport()
            val jacksonFactory = JacksonFactory()

            val blogger = Blogger.Builder(netHttpTransport, jacksonFactory, googleCredential)
            blogger.applicationName = "Blogger-PostsInsert-Snippet/1.0"

            val content = Post()
            content.title = blog.title
            content.content = blog.content

            val postsInsertAction = blogger.build().posts().insert(blogID, content)
            postsInsertAction.fields = "id,blog,author/displayName,content,published,title,url"
            postsInsertAction.isDraft = true

            Toast.makeText(context, getString(R.string.publishing_the_blog), Toast.LENGTH_SHORT).show()
            launch {
                val post = postsInsertAction.execute()

                blog.blogID = post.blog.id
                blog.postID = post.id

                view?.post {
                    Toast.makeText(view?.context!!, getString(R.string.published_success_draft), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}

