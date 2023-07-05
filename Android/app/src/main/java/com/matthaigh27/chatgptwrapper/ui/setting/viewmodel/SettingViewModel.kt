package com.matthaigh27.chatgptwrapper.ui.setting.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.matthaigh27.chatgptwrapper.data.models.setting.SettingModel
import com.matthaigh27.chatgptwrapper.data.remote.ApiResource
import com.matthaigh27.chatgptwrapper.data.repository.SharedPreferencesRepository

class SettingViewModel : ViewModel() {
    fun setSettingData(model: SettingModel): MutableLiveData<ApiResource<Boolean>> {
        val state: MutableLiveData<ApiResource<Boolean>> = MutableLiveData()
        state.value = ApiResource.Loading()
        SharedPreferencesRepository.saveConfig(model)
        state.value = ApiResource.Success(true)
        return state
    }

    fun getSettingData(): MutableLiveData<ApiResource<SettingModel>> {
        val state: MutableLiveData<ApiResource<SettingModel>> = MutableLiveData()
        state.value = ApiResource.Loading()
        val settingModel = SharedPreferencesRepository.getConfig()
        state.value = ApiResource.Success(settingModel)
        return state
    }
}