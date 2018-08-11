package com.zestworks.blogger.ui

import com.zestworks.blogger.ui.compose.Composer

data class SpanData(val propType: Composer.PROPS, val startIndex: Int, val endIndex: Int)


class StyleData {
    internal var bold = false
    internal var italics = false
    internal var underline = false
    internal var strikeThrough = false
    internal var leftAlignment = false
    internal var centerAlignment = false
    internal var rightAlignment = false
    internal var fontSize = 12
}