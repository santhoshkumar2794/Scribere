package com.zestworks.blogger.model

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [(Blog::class)], version = 1)
abstract class BlogDB : RoomDatabase() {
    abstract fun blogDao(): BlogDao
}