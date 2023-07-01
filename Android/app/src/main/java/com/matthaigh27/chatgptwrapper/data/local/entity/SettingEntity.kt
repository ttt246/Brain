package com.matthaigh27.chatgptwrapper.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class SettingEntity (
    @PrimaryKey(autoGenerate = false) val uuid: String,
    val setting: String
)