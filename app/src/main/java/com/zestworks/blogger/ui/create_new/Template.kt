package com.zestworks.blogger.ui.create_new

import android.os.Bundle
import com.zestworks.blogger.R

enum class Template {
    BLANK_TEMPLATE {
        override fun getID() = R.mipmap.blank_template

        override fun toString(): String = "BLANK_TEMPLATE"
    },
    TITLE_WITH_CONTENT {
        override fun getID() = R.mipmap.title_content_template

        override fun toString(): String = "TITLE_WITH_CONTENT"
    },
    TITLE_IMAGE_CONTENT {
        override fun getID() = R.mipmap.template_locked

        override fun toString(): String = "TITLE_IMAGE_CONTENT"
    },
    DEFAULT {
        override fun getID() = 0

        override fun toString(): String = "DEFAULT"
    };

    abstract fun getID(): Int
    abstract override fun toString(): String
}