package com.android.sidewalk.repositories.profile

import androidx.lifecycle.MutableLiveData
import com.android.sidewalk.R
import com.android.sidewalk.api.ApiClient
import com.android.sidewalk.api.ApiResponse
import com.android.sidewalk.api.ApiService
import com.android.sidewalk.application.MyApplication
import com.android.sidewalk.common.UtilsFunctions
import com.android.sidewalk.model.CommonModel
import com.android.sidewalk.model.LoginResponse
import com.android.sidewalk.model.profile.ProfileResponse
import com.android.sidewalk.model.profile.RegionResponse
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.util.HashMap

class ProfileRepository {
    private var data : MutableLiveData<LoginResponse>? = null
    private var data1 : MutableLiveData<ProfileResponse>? = null
    private var data2 : MutableLiveData<CommonModel>? = null
    private var logoutResponse : MutableLiveData<CommonModel>? = null
    private var profileUpdate : MutableLiveData<CommonModel>? = null
    private val gson = GsonBuilder().serializeNulls().create()
    private var data3 : MutableLiveData<RegionResponse>? = null

    init {
        data = MutableLiveData()
        data1 = MutableLiveData()
        data2 = MutableLiveData()
        profileUpdate = MutableLiveData()
        logoutResponse = MutableLiveData()
        data3 = MutableLiveData()

    }

    fun getLoginData(jsonObject : JsonObject?) : MutableLiveData<LoginResponse> {
        if (jsonObject != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse : Response<JsonObject>) {
                        val loginResponse = if (mResponse.body() != null)
                            gson.fromJson<LoginResponse>(
                                "" + mResponse.body(),
                                LoginResponse::class.java
                            )
                        else {
                            gson.fromJson<LoginResponse>(
                                mResponse.errorBody()!!.charStream(),
                                LoginResponse::class.java
                            )
                        }


                        data!!.postValue(loginResponse)

                    }

                    override fun onError(mKey : String) {
                        UtilsFunctions.showToastError(
                            MyApplication.instance.getString(R.string.internal_server_error)
                        )
                        data!!.postValue(null)

                    }

                },
                ApiClient.getApiInterface().callLogin(
                    jsonObject
                )
            )

        }
        return data!!

    }

    fun getUserProfile(/*jsonObject : JsonObject?*/) : MutableLiveData<ProfileResponse> {
        //if (jsonObject != null) {
        val mApiService = ApiService<JsonObject>()
        mApiService.get(
            object : ApiResponse<JsonObject> {
                override fun onResponse(mResponse : Response<JsonObject>) {
                    val loginResponse = if (mResponse.body() != null)
                        gson.fromJson<ProfileResponse>(
                            "" + mResponse.body(),
                            ProfileResponse::class.java
                        )
                    else {
                        gson.fromJson<ProfileResponse>(
                            mResponse.errorBody()!!.charStream(),
                            ProfileResponse::class.java
                        )
                    }
                    data1!!.postValue(loginResponse)
                }

                override fun onError(mKey : String) {
                    UtilsFunctions.showToastError(
                        MyApplication.instance.getString(R.string.internal_server_error)
                    )
                    data1!!.postValue(null)
                }

            }, ApiClient.getApiInterface().getProfile(/*jsonObject*/)
        )
        //}
        return data1!!

    }

    fun getLogoutResonse(jsonObject : String?) : MutableLiveData<CommonModel> {
        if (jsonObject != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse : Response<JsonObject>) {
                        val logoutResponse11 = if (mResponse.body() != null)
                            gson.fromJson<CommonModel>(
                                "" + mResponse.body(),
                                CommonModel::class.java
                            )
                        else {
                            gson.fromJson<CommonModel>(
                                mResponse.errorBody()!!.charStream(),
                                CommonModel::class.java
                            )
                        }

                        logoutResponse!!.postValue(logoutResponse11)

                    }

                    override fun onError(mKey : String) {
                        UtilsFunctions.showToastError(
                            MyApplication.instance.getString(R.string.internal_server_error)
                        )
                        logoutResponse!!.postValue(null)

                    }

                }, ApiClient.getApiInterface().callLogout()
            )

        }
        return logoutResponse!!

    }

    fun callUpdateProfile(
        jsonObject : HashMap<String, RequestBody>?,
        userImage : MultipartBody.Part?,
        licenseFront : MultipartBody.Part?,
        licenseBack : MultipartBody.Part?
    ) : MutableLiveData<CommonModel> {
        if (jsonObject != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse : Response<JsonObject>) {
                        val loginResponse = if (mResponse.body() != null)
                            gson.fromJson<CommonModel>(
                                "" + mResponse.body(),
                                CommonModel::class.java
                            )
                        else {
                            gson.fromJson<CommonModel>(
                                mResponse.errorBody()!!.charStream(),
                                CommonModel::class.java
                            )
                        }


                        profileUpdate!!.postValue(loginResponse)

                    }

                    override fun onError(mKey : String) {
                        UtilsFunctions.showToastError(
                            MyApplication.instance.getString(
                                R.string.internal_server_error
                            )
                        )
                        profileUpdate!!.postValue(null)

                    }

                },
                ApiClient.getApiInterface().callUpdateProfile(
                    jsonObject, userImage, licenseFront, licenseBack
                )
            )

        }
        return profileUpdate!!

    }

    fun getRegoins(
    ) : MutableLiveData<RegionResponse> {
        //if (hashMap != null) {
        val mApiService = ApiService<JsonObject>()
        mApiService.get(
            object : ApiResponse<JsonObject> {
                override fun onResponse(mResponse : Response<JsonObject>) {
                    val loginResponse = if (mResponse.body() != null)
                        gson.fromJson<RegionResponse>(
                            "" + mResponse.body(),
                            RegionResponse::class.java
                        )
                    else {
                        gson.fromJson<RegionResponse>(
                            mResponse.errorBody()!!.charStream(),
                            RegionResponse::class.java
                        )
                    }


                    data3!!.postValue(loginResponse)

                }

                override fun onError(mKey : String) {
                    UtilsFunctions.showToastError(
                        MyApplication.instance.getString(R.string.internal_server_error)
                    )
                    data3!!.postValue(null)

                }

            }, ApiClient.getApiInterface().getRegions()
        )
        // }
        return data3!!

    }

}