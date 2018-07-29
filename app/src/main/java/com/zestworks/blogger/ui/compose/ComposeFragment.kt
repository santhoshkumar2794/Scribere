package com.zestworks.blogger.ui.compose

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spanned
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.api.client.auth.oauth.OAuthCredentialsResponse
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.auth.oauth2.OAuth2Utils
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.blogger.Blogger
import com.google.api.services.blogger.BloggerScopes
import com.google.api.services.blogger.model.Post
import com.google.api.services.oauth2.Oauth2
import com.google.api.services.oauth2.Oauth2RequestInitializer
import com.zestworks.blogger.R
import com.zestworks.blogger.auth.AuthManager
import com.zestworks.blogger.ui.listing.BloggerViewModel
import kotlinx.android.synthetic.main.compose_fragment.*
import kotlinx.coroutines.experimental.launch
import java.io.InputStreamReader
import java.util.*


class ComposeFragment : Fragment(), ComposerCallback {

    private val selectedColor: Int = Color.RED

    private lateinit var viewModel: BloggerViewModel
    private lateinit var authManager: AuthManager

    companion object {
        fun newInstance() = ComposeFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.compose_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        text_editor.composerCallback = this

        bold_button.setOnClickListener {
            /*if (!it.isSelected) {
                text_editor.applyProps(Composer.PROPS.BOLD)
            } else {
                text_editor.removeProps(Composer.PROPS.BOLD)
            }*/

            uploadBlogs()
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

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        updateEditTools(selStart, selEnd)
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
        //blogger.setHttpRequestInitializer(builder)

        val content = Post()
        content.title = "A test post"
        content.content = "With <code>HTML</code> content"


        launch {
            val postsInsertAction = blogger.build().posts().insert("734820219569993445", content)
            postsInsertAction.isDraft = true
            postsInsertAction.execute()
        }
    }
}

