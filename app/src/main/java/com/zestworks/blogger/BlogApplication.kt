package com.zestworks.blogger

import android.app.Application

class BlogApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        DBHelper.initializeDB(this)
    }
}