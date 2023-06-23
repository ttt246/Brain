package com.matthaigh27.chatgptwrapper.data.local.dao

import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.matthaigh27.chatgptwrapper.data.local.entity.ContactEntity

@Dao
interface ContactDao {
    @Insert
    fun insert(contact: ContactEntity)

    @Update
    fun update(contact: ContactEntity)

    @Delete
    fun delete(contact: ContactEntity)

    @Query("SELECT * FROM contacts")
    fun getAllData(): List<ContactEntity>
}