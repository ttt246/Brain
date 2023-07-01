package com.matthaigh27.chatgptwrapper.data.repository

import androidx.lifecycle.MutableLiveData
import com.matthaigh27.chatgptwrapper.RisingApplication
import com.matthaigh27.chatgptwrapper.data.local.AppDatabase
import com.matthaigh27.chatgptwrapper.data.local.entity.ContactEntity
import com.matthaigh27.chatgptwrapper.data.local.entity.ImageEntity
import com.matthaigh27.chatgptwrapper.data.local.entity.SettingEntity


object RoomRepository {
    private var databaseHandler: AppDatabase = AppDatabase.getDatabase(RisingApplication.appContext)

    private var imageDao = databaseHandler.imageDao()
    private var contactDao = databaseHandler.contactDao()
    private var settingDao = databaseHandler.settingDao()

    suspend fun getAllImages(): MutableLiveData<List<ImageEntity>> {
        return MutableLiveData(imageDao.getAllData())
    }

    suspend fun insertImage(entity: ImageEntity) {
        imageDao.insert(entity)
    }

    suspend fun updateImage(entity: ImageEntity) {
        imageDao.update(entity)
    }

    suspend fun deleteImage(entity: ImageEntity) {
        imageDao.delete(entity)
    }

    suspend fun getAllContacts(): MutableLiveData<List<ContactEntity>> {
        return MutableLiveData(contactDao.getAllData())
    }

    suspend fun insertContact(entity: ContactEntity) {
        contactDao.insert(entity)
    }

    suspend fun updateContact(entity: ContactEntity) {
        contactDao.update(entity)
    }

    suspend fun deleteContact(entity: ContactEntity) {
        contactDao.delete(entity)
    }

    suspend fun getAllSettings(): MutableLiveData<List<SettingEntity>> {
        return MutableLiveData(settingDao.getAllData())
    }

    suspend fun getSettingByUUID(uuid: String): SettingEntity {
        settingDao.getDataByUUID(uuid)
        return settingDao.getDataByUUID(uuid)
    }

    suspend fun insertSetting(entity: SettingEntity) {
        settingDao.insert(entity)
    }

    suspend fun updateSetting(entity: SettingEntity) {
        settingDao.update(entity)
    }

    suspend fun deleteSetting(entity: SettingEntity) {
        settingDao.delete(entity)
    }
}