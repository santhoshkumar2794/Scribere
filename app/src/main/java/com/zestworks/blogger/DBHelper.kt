package com.zestworks.blogger

import android.content.Context
import androidx.room.Room
import com.zestworks.blogger.model.BlogDB
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object DBHelper {
    lateinit var blogDatabase: BlogDB

    fun initializeDB(context: Context) {
        GlobalScope.launch {
            blogDatabase = Room.databaseBuilder(context, BlogDB::class.java, "BlogDB").build()
        }.start()
    }
}