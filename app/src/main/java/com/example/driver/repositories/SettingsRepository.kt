package com.example.driver.repositories

import androidx.lifecycle.MutableLiveData
import com.example.driver.R
import com.example.driver.api.ApiClient
import com.example.driver.api.ApiResponse
import com.example.driver.api.ApiService
import com.example.driver.application.MyApplication
import com.example.driver.common.UtilsFunctions
import com.example.driver.model.CommonModel
import com.example.driver.model.ProfileModel
import com.example.driver.sharedpreference.SharedPrefClass
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import retrofit2.Response


class SettingsRepository {
    private var data: MutableLiveData<ProfileModel>? = null
    private var updateSettingData: MutableLiveData<CommonModel>? = null
    private var updateProfileData: MutableLiveData<ProfileModel>? = null
    private val gson = GsonBuilder().serializeNulls().create()

    init {
        data = MutableLiveData()
        updateSettingData = MutableLiveData()
        updateProfileData = MutableLiveData()
    }

    fun getProfileData(): MutableLiveData<ProfileModel> {
        if(UtilsFunctions.isNetworkConnected()) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse : Response<JsonObject>) {
                        val profileResponse = if (mResponse.body() != null)
                            gson.fromJson("" + mResponse.body(), ProfileModel::class.java)
                        else {
                            gson.fromJson(
                                mResponse.errorBody()!!.charStream(),
                                ProfileModel::class.java
                            )
                        }

                        data!!.postValue(profileResponse)
                        if (profileResponse != null) {
                            val message = profileResponse.message
                            if (profileResponse.code == 200) {
                                SharedPrefClass().putObject(
                                    MyApplication.instance,
                                    "isLogin",
                                    true
                                )
                            } else {
                                UtilsFunctions.showToastError(message!!)
                            }
                        } else {
                            UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        }
                    }

                    override fun onError(mKey : String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        data!!.postValue(null)
                    }
                }, ApiClient.getApiInterface().profileData

            )
        }
        return data!!
    }

    fun updateUserSettings(mJsonObject : JsonObject) : MutableLiveData<CommonModel> {
        val mApiService = ApiService<JsonObject>()
        mApiService.get(
            object : ApiResponse<JsonObject> {
                override fun onResponse(mResponse: Response<JsonObject>) {
                    val response = if (mResponse.body() != null)
                        gson.fromJson("" + mResponse.body(), CommonModel::class.java)
                    else {
                        gson.fromJson(mResponse.errorBody()!!.charStream(), CommonModel::class.java
                        )
                    }
                    updateSettingData!!.postValue(response!!)
                    val message = response.message
                    if (response.code != 200) {
                        UtilsFunctions.showToastError(message!!)
                    }
                }

                override fun onError(mKey: String) {
                    UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                }
            }, ApiClient.getApiInterface().updateUserSetting(mJsonObject)

        )
        return updateSettingData!!
    }





}