package com.android.sidewalk.viewmodels.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.view.View
import com.android.sidewalk.common.UtilsFunctions
import com.android.sidewalk.common.UtilsFunctions.isNetworkConnectedReturn
import com.android.sidewalk.model.CommonModel
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
    private var updateProfile =
        MutableLiveData<CommonModel>()
    private var logoutResponse =
        MutableLiveData<CommonModel>()
    private var profileRepository =
        ProfileRepository()
    private val mIsUpdating = MutableLiveData<Boolean>()
    private val btnClick = MutableLiveData<String>()

    init {
        if (isNetworkConnectedReturn()) {
            profileDetail = profileRepository.getUserProfile()
            updateProfile = profileRepository.callUpdateProfile(null, null, null, null)
            regionResponse = profileRepository.getRegoins()
            logoutResponse = profileRepository.getLogoutResonse(null)
        }

    }

    fun getDetail() : LiveData<ProfileResponse> {
        return profileDetail
    }

    fun getLogoutResponse() : LiveData<CommonModel> {
        return logoutResponse
    }

    fun getUpdateDetail() : LiveData<LoginResponse> {
        return data
    }

    fun geUpdateProfileRes() : LiveData<CommonModel> {
        return updateProfile
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

    fun callUpdateProfile(
        mJsonObject : java.util.HashMap<String, RequestBody>,
        userImage : MultipartBody.Part?,
        licenseFront : MultipartBody.Part?,
        licenseBack : MultipartBody.Part?
    ) {
        if (UtilsFunctions.isNetworkConnected()) {
            //emialExistenceResponse = loginRepository.checkPhoneExistence(mJsonObject)
            updateProfile =
                profileRepository.callUpdateProfile(
                    mJsonObject,
                    userImage,
                    licenseFront,
                    licenseBack
                )
            mIsUpdating.postValue(true)
        }

    }

    fun callLogoutApi() {
        if (UtilsFunctions.isNetworkConnected()) {
            //emialExistenceResponse = loginRepository.checkPhoneExistence(mJsonObject)
            updateProfile =
                profileRepository.getLogoutResonse("aaa")
            mIsUpdating.postValue(true)
        }
    }

    fun callProfielApi() {
        if (UtilsFunctions.isNetworkConnected()) {
            profileDetail = profileRepository.getUserProfile()
            mIsUpdating.postValue(true)
        }
    }

}