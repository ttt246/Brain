package com.matthaigh27.chatgptwrapper.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.matthaigh27.chatgptwrapper.data.local.dao.ContactDao
import com.matthaigh27.chatgptwrapper.data.local.dao.ImageDao
import com.matthaigh27.chatgptwrapper.data.local.dao.SettingDao
import com.matthaigh27.chatgptwrapper.data.local.entity.ContactEntity
import com.matthaigh27.chatgptwrapper.data.local.entity.ImageEntity
import com.matthaigh27.chatgptwrapper.data.local.entity.SettingEntity

@Database(entities = [ImageEntity::class, ContactEntity::class, SettingEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun imageDao(): ImageDao
    abstract fun contactDao(): ContactDao
    abstract fun settingDao(): SettingDao

    companion object {
        private val DATABASE_NAME = "RisingPhone"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context = context.applicationContext,
                    klass = AppDatabase::class.java,
                    name = DATABASE_NAME
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}