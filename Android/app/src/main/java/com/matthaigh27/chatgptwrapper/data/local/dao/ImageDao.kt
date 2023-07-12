package com.matthaigh27.chatgptwrapper.data.local.dao

import androidx.room.*
import com.matthaigh27.chatgptwrapper.data.local.entity.ImageEntity

@Dao
interface ImageDao {
    @Insert
    suspend fun insert(image: ImageEntity)

    @Update
    suspend fun update(image: ImageEntity)

    @Delete
    suspend fun delete(image: ImageEntity)

    @Query("SELECT * FROM images")
    suspend fun getAllData(): List<ImageEntity>
}