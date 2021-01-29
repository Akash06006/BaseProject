package com.android.sidewalk.viewmodels.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.view.View
import com.android.sidewalk.common.UtilsFunctions
import com.android.sidewalk.common.UtilsFunctions.isNetworkConnectedReturn
import com.android.sidewalk.model.LoginResponse
import com.android.sidewalk.model.profile.ProfileResponse
import com.android.sidewalk.model.profile.RegionResponse
import com.android.sidewalk.repositories.profile.ProfileRepository
import com.android.sidewalk.viewmodels.BaseViewModel
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ProfileViewModel : BaseViewModel() {
    private var regionResponse = MutableLiveData<RegionResponse>()
    private var data =
        MutableLiveData<LoginResponse>()
    private var profileDetail =
        MutableLiveData<ProfileResponse>()
    private var profileRepository =
        ProfileRepository()
    private val mIsUpdating = MutableLiveData<Boolean>()
    private val btnClick = MutableLiveData<String>()

    init {
        if (isNetworkConnectedReturn()) {
            profileDetail = profileRepository.getUserProfile()
            data = profileRepository.updateUserProfile(null, null)
            regionResponse = profileRepository.getRegoins()
        }

    }

    fun getDetail() : LiveData<ProfileResponse> {
        return profileDetail
    }

    fun getUpdateDetail() : LiveData<LoginResponse> {
        return data
    }

    fun getRegionsRes() : LiveData<RegionResponse> {
        return regionResponse
    }

    override fun isLoading() : LiveData<Boolean> {
        return mIsUpdating
    }

    override fun isClick() : LiveData<String> {
        return btnClick
    }

    override fun clickListener(v : View) {
        btnClick.value = v.resources.getResourceName(v.id).split("/")[1]
    }

    fun getProfileDetail(mJsonObject : JsonObject) {
        if (UtilsFunctions.isNetworkConnected()) {
            profileDetail = profileRepository.getUserProfile(/*mJsonObject*/)
            mIsUpdating.postValue(true)
        }

    }

    fun updateProfile(hashMap : HashMap<String, RequestBody>, image : MultipartBody.Part?) {
        if (UtilsFunctions.isNetworkConnected()) {
            data = profileRepository.updateUserProfile(hashMap, image)
            mIsUpdating.postValue(true)

        }
    }

    fun getRegions(hashMap : HashMap<String, RequestBody>, image : MultipartBody.Part?) {
        if (UtilsFunctions.isNetworkConnected()) {
            data = profileRepository.updateUserProfile(hashMap, image)
            mIsUpdating.postValue(true)

        }
    }

}