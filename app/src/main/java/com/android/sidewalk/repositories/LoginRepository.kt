package com.android.sidewalk.repositories

import androidx.lifecycle.MutableLiveData
import com.android.sidewalk.R
import com.android.sidewalk.api.ApiClient
import com.android.sidewalk.api.ApiResponse
import com.android.sidewalk.api.ApiService
import com.android.sidewalk.application.MyApplication
import com.android.sidewalk.common.UtilsFunctions
import com.android.sidewalk.model.CommonModel
import com.android.sidewalk.model.LoginResponse
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.util.*

class LoginRepository {
    private var data : MutableLiveData<LoginResponse>? =
        null
    private var signupData : MutableLiveData<LoginResponse>? =
        null
    private var data1 : MutableLiveData<CommonModel>? =
        null
    private var verifyUser : MutableLiveData<CommonModel>? =
        null
    private val gson = GsonBuilder().serializeNulls().create()

    init {
        data = MutableLiveData()
        data1 = MutableLiveData()
        signupData = MutableLiveData()
        verifyUser = MutableLiveData()

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
                            MyApplication.instance.getString(
                                R.string.internal_server_error
                            )
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

    fun callSignupApi(
        jsonObject : HashMap<String, RequestBody>?,
        userImage : MultipartBody.Part?,
        licenseFront : MultipartBody.Part?,
        licenseBack : MultipartBody.Part?
    ) : MutableLiveData<LoginResponse> {
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


                        signupData!!.postValue(loginResponse)

                    }

                    override fun onError(mKey : String) {
                        UtilsFunctions.showToastError(
                            MyApplication.instance.getString(
                                R.string.internal_server_error
                            )
                        )
                        signupData!!.postValue(null)

                    }

                },
                ApiClient.getApiInterface().callSignup(
                    jsonObject, userImage, licenseFront, licenseBack
                )
            )

        }
        return signupData!!

    }

    fun callVerifyUserApi(jsonObject : JsonObject?) : MutableLiveData<CommonModel> {
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

                        verifyUser!!.postValue(loginResponse)
                    }

                    override fun onError(mKey : String) {
                        UtilsFunctions.showToastError(
                            MyApplication.instance.getString(
                                R.string.internal_server_error
                            )
                        )
                        verifyUser!!.postValue(null)

                    }

                },
                ApiClient.getApiInterface().callVerifyUser(
                    jsonObject
                )
            )

        }
        return verifyUser!!

    }

    fun checkSocial(
        mJsonObject : HashMap<String, RequestBody>?,
        profileDetails : MutableLiveData<LoginResponse>?
    ) : MutableLiveData<LoginResponse>? {
        if (UtilsFunctions.isNetworkConnected() && mJsonObject != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse : Response<JsonObject>) {
                        val data = gson.fromJson<LoginResponse>(
                            "" + mResponse.body()!!,
                            LoginResponse::class.java
                        )
                        profileDetails!!.postValue(data)
                    }

                    override fun onError(mKey : String) {
                        profileDetails!!.value = null
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                    }
                }, ApiClient.getApiInterface().checkSocial(mJsonObject)
            )
        }

        return profileDetails
    }
}