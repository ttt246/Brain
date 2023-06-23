package com.matthaigh27.chatgptwrapper.data.local.dao

import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.matthaigh27.chatgptwrapper.data.local.entity.ImageEntity

@Dao
interface ImageDao {
    @Insert
    fun insert(image: ImageEntity)

    @Update
    fun update(image: ImageEntity)

    @Delete
    fun delete(image: ImageEntity)

    @Query("SELECT * FROM images")
    fun getAllData(): List<ImageEntity>
}