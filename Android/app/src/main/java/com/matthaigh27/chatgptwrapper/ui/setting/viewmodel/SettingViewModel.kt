package com.matthaigh27.chatgptwrapper.ui.setting.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.matthaigh27.chatgptwrapper.RisingApplication
import com.matthaigh27.chatgptwrapper.data.local.entity.SettingEntity
import com.matthaigh27.chatgptwrapper.data.models.setting.SettingModel
import com.matthaigh27.chatgptwrapper.data.remote.ApiResource
import com.matthaigh27.chatgptwrapper.data.repository.RoomRepository
import com.matthaigh27.chatgptwrapper.utils.helpers.chat.SettingHelper.emptySettingModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingViewModel : ViewModel() {
    fun setSettingData(model: SettingModel): MutableLiveData<ApiResource<Boolean>> {
        val state: MutableLiveData<ApiResource<Boolean>> = MutableLiveData()
        state.value = ApiResource.Loading()
        CoroutineScope(Dispatchers.IO).launch {
            val data = RoomRepository.getSettingByUUID(RisingApplication.appContext.getUUID())
            if (model.toString() != data.setting) {
                RoomRepository.updateSetting(
                    SettingEntity(
                        RisingApplication.appContext.getUUID(), model.toString()
                    )
                )
            }

            withContext(Dispatchers.Main) {
                state.value = ApiResource.Success(true)
            }
        }
        return state
    }

    fun getSettingData(): MutableLiveData<ApiResource<SettingModel>> {
        val state: MutableLiveData<ApiResource<SettingModel>> = MutableLiveData()
        state.value = ApiResource.Loading()
        CoroutineScope(Dispatchers.IO).launch {
            val data = RoomRepository.getSettingByUUID(RisingApplication.appContext.getUUID())
            withContext(Dispatchers.Main) {
                if (data == null)
                    state.value = ApiResource.Success(emptySettingModel())
                else
                    state.value = ApiResource.Success(SettingModel.init(data.setting))
            }
        }
        return state
    }
}