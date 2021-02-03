package com.android.sidewalk.repositories.order

import androidx.lifecycle.MutableLiveData
import com.android.sidewalk.R
import com.android.sidewalk.api.ApiClient
import com.android.sidewalk.api.ApiResponse
import com.android.sidewalk.api.ApiService
import com.android.sidewalk.application.MyApplication
import com.android.sidewalk.common.UtilsFunctions
import com.android.sidewalk.model.CommonModel
import com.android.sidewalk.model.LoginResponse
import com.android.sidewalk.model.order.ListsResponse
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.util.HashMap

class OrderRepository {
    private var data : MutableLiveData<LoginResponse>? = null
    private var data1 : MutableLiveData<LoginResponse>? = null
    private var data2 : MutableLiveData<CommonModel>? = null
    private val gson = GsonBuilder().serializeNulls().create()
    private var data3 : MutableLiveData<ListsResponse>? = null

    init {
        data = MutableLiveData()
        data1 = MutableLiveData()
        data2 = MutableLiveData()
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
                            MyApplication.instance.getString(R.string.internal_server_error))
                        data!!.postValue(null)

                    }

                }, ApiClient.getApiInterface().callLogin(jsonObject)
            )

        }
        return data!!

    }

    fun getUserProfile(jsonObject : JsonObject?) : MutableLiveData<LoginResponse> {
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
                        data1!!.postValue(loginResponse)
                    }

                    override fun onError(mKey : String) {
                       UtilsFunctions.showToastError(
                            MyApplication.instance.getString(R.string.internal_server_error))
                        data1!!.postValue(null)
                    }

                }, ApiClient.getApiInterface().getProfile(/*jsonObject*/)
            )

        }
        return data1!!

    }


    fun updateUserProfile(
        hashMap : HashMap<String, RequestBody>?,
        image : MultipartBody.Part?
    ) : MutableLiveData<LoginResponse> {
        if (hashMap != null) {
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
                            MyApplication.instance.getString(R.string.internal_server_error))
                        data!!.postValue(null)

                    }

                }, ApiClient.getApiInterface().callUpdateProfile(hashMap, image)
            )
        }
        return data!!

    }

    fun getDataLists(
    ) : MutableLiveData< ListsResponse> {
        //if (hashMap != null) {
        val mApiService = ApiService<JsonObject>()
        mApiService.get(
            object : ApiResponse<JsonObject> {
                override fun onResponse(mResponse : Response<JsonObject>) {
                    val loginResponse = if (mResponse.body() != null)
                        gson.fromJson< ListsResponse>(
                            "" + mResponse.body(),
                             ListsResponse::class.java
                        )
                    else {
                        gson.fromJson< ListsResponse>(
                            mResponse.errorBody()!!.charStream(),
                             ListsResponse::class.java
                        )
                    }


                    data3!!.postValue(loginResponse)

                }

                override fun onError(mKey : String) {
                    UtilsFunctions.showToastError(
                        MyApplication.instance.getString(R.string.internal_server_error))
                    data3!!.postValue(null)

                }

            }, ApiClient.getApiInterface().getLists()
        )
        // }
        return data3!!

    }

}