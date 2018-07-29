package com.zestworks.blogger.ui.compose

import android.content.Context
import android.graphics.Typeface
import android.text.ParcelableSpan
import android.text.Spanned
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.zestworks.blogger.ui.SpanData
import com.zestworks.blogger.ui.StyleData
import kotlinx.android.synthetic.main.compose_fragment.*

class Composer(context: Context, attributeSet: AttributeSet) : AppCompatEditText(context, attributeSet) {

    var composerCallback: ComposerCallback? = null

    enum class PROPS {
        BOLD, ITALICS, UNDERLINE

    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        composerCallback?.onSelectionChanged(selStart, selEnd)
    }

    fun getStyle(selStart: Int, selEnd: Int): StyleData {
        val styleData = StyleData()
        val styleSpans = text?.getSpans(selStart, selEnd, StyleSpan::class.java)!!
        styleSpans.forEach {
            when (it.style) {
                Typeface.BOLD -> styleData.bold = true
                Typeface.ITALIC -> styleData.italics = true
            }
        }
        return styleData
    }

    private fun applyProps(propType: PROPS, selStart: Int, selEnd: Int) {
        val span: ParcelableSpan = when (propType) {
            Composer.PROPS.BOLD -> StyleSpan(Typeface.BOLD)
            Composer.PROPS.ITALICS -> StyleSpan(Typeface.ITALIC)
            Composer.PROPS.UNDERLINE -> UnderlineSpan()
        }
        text?.setSpan(span, selStart, selEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
    }

    internal fun applyProps(propType: PROPS) {
        applyProps(propType, selectionStart, selectionEnd)
        composerCallback?.onSelectionChanged(selectionStart, selectionEnd)
    }

    internal fun removeProps(propType: PROPS) {
        when (propType) {
            Composer.PROPS.BOLD -> removeStyleSpan(Typeface.BOLD)
            Composer.PROPS.ITALICS -> removeStyleSpan(Typeface.ITALIC)
            Composer.PROPS.UNDERLINE -> removeUnderlineSpan()
        }
        composerCallback?.onSelectionChanged(selectionStart, selectionEnd)
    }

    private fun removeStyleSpan(style: Int) {
        val spanDataList = ArrayList<SpanData>()

        val styleSpans = text?.getSpans(selectionStart, selectionEnd, StyleSpan::class.java)!!
        styleSpans.forEach { styleSpan ->
            when (style) {
                styleSpan.style -> {
                    val spanStart = text?.getSpanStart(styleSpan)!!
                    val spanEnd = text?.getSpanEnd(styleSpan)!!

                    val propType = when (style) {
                        Typeface.BOLD -> PROPS.BOLD
                        Typeface.ITALIC -> PROPS.ITALICS
                        else -> null
                    } ?: return@forEach

                    spanDataList.add(SpanData(propType, spanStart, spanEnd))
                    text?.removeSpan(styleSpan)
                }
            }
        }
        reApplyRemovedProps(spanDataList)
    }

    private fun removeUnderlineSpan() {
        val spanDataList = ArrayList<SpanData>()

        val styleSpans = text?.getSpans(selectionStart, selectionEnd, UnderlineSpan::class.java)!!
        for (styleSpan in styleSpans) {
            val spanStart = text?.getSpanStart(styleSpan)!!
            val spanEnd = text?.getSpanEnd(styleSpan)!!
            spanDataList.add(SpanData(PROPS.UNDERLINE, spanStart, spanEnd))

            text?.removeSpan(styleSpan)
        }

        reApplyRemovedProps(spanDataList)
    }

    private fun reApplyRemovedProps(spanDataList : ArrayList<SpanData>){
        for (spanData in spanDataList) {
            if (spanData.startIndex < selectionStart) {
                applyProps(spanData.propType, spanData.startIndex, selectionStart)
            }

            if (spanData.endIndex > selectionEnd) {
                applyProps(spanData.propType, selectionEnd, spanData.endIndex)
            }
        }
    }
}