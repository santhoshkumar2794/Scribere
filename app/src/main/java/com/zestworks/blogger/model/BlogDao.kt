package com.zestworks.blogger.model

import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BlogDao {

    @Query("SELECT * FROM blog")
    fun getBlogList(): DataSource.Factory<Int, Blog>

    @Insert
    fun insert(blog: Blog)
}