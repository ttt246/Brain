package com.matthaigh27.chatgptwrapper.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.matthaigh27.chatgptwrapper.data.local.entity.SettingEntity
import java.util.UUID

@Dao
interface SettingDao {
    @Insert
    suspend fun insert(image: SettingEntity)

    @Update
    suspend fun update(image: SettingEntity)

    @Delete
    suspend fun delete(image: SettingEntity)

    @Query("SELECT * FROM settings")
    suspend fun getAllData(): List<SettingEntity>

    @Query("SELECT * FROM settings WHERE uuid = :uuid")
    suspend fun getDataByUUID(uuid: String): SettingEntity
}