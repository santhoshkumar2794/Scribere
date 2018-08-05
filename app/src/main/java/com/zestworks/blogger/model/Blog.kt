package com.zestworks.blogger.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Blog")
class Blog {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    var columnID: Int = 0

    @ColumnInfo
    var blogID : String? = null

    @ColumnInfo
    var postID : String? = null

    @ColumnInfo
    var title: String? = null

    @ColumnInfo
    var content: String? = null
}
