package com.matthaigh27.chatgptwrapper.data.local.dao

import androidx.room.*
import com.matthaigh27.chatgptwrapper.data.local.entity.ContactEntity

@Dao
interface ContactDao {
    @Insert
    suspend fun insert(contact: ContactEntity)

    @Update
    suspend fun update(contact: ContactEntity)

    @Delete
    suspend fun delete(contact: ContactEntity)

    @Query("SELECT * FROM contacts")
    suspend fun getAllData(): List<ContactEntity>
}