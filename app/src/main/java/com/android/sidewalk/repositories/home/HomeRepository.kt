package com.example.services.repositories.home

import androidx.lifecycle.MutableLiveData
import com.android.sidewalk.R
import com.android.sidewalk.api.ApiClient
import com.android.sidewalk.api.ApiResponse
import com.android.sidewalk.api.ApiService
import com.android.sidewalk.application.MyApplication
import com.android.sidewalk.common.UtilsFunctions
import com.android.sidewalk.model.CommonModel
import com.android.sidewalk.model.LoginResponse
import com.android.sidewalk.model.home.HomeListResponse
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.util.HashMap

class HomeRepository {
    private var data1 : MutableLiveData<CommonModel>? = null
    private var homeList : MutableLiveData<HomeListResponse>? = null
    private val gson = GsonBuilder().serializeNulls().create()

    init {
        data1 = MutableLiveData()
        homeList = MutableLiveData()
    }

    fun addBannerImage(
        userImage : MultipartBody.Part?
    ) : MutableLiveData<CommonModel> {
        if (userImage != null) {
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


                        data1!!.postValue(loginResponse)

                    }

                    override fun onError(mKey : String) {
                        UtilsFunctions.showToastError(
                            MyApplication.instance.getString(
                                R.string.internal_server_error
                            )
                        )
                        data1!!.postValue(null)

                    }

                },
                ApiClient.getApiInterface().addBannerImage(
                    userImage
                )
            )

        }
        return data1!!

    }

    fun getHomeList(
    ) : MutableLiveData<HomeListResponse> {
        // if (userImage != null) {
        val mApiService = ApiService<JsonObject>()
        mApiService.get(
            object : ApiResponse<JsonObject> {
                override fun onResponse(mResponse : Response<JsonObject>) {
                    val loginResponse = if (mResponse.body() != null)
                        gson.fromJson<HomeListResponse>(
                            "" + mResponse.body(),
                            HomeListResponse::class.java
                        )
                    else {
                        gson.fromJson<HomeListResponse>(
                            mResponse.errorBody()!!.charStream(),
                            HomeListResponse::class.java
                        )
                    }


                    homeList!!.postValue(loginResponse)

                }

                override fun onError(mKey : String) {
                    UtilsFunctions.showToastError(
                        MyApplication.instance.getString(
                            R.string.internal_server_error
                        )
                    )
                    homeList!!.postValue(null)

                }

            },
            ApiClient.getApiInterface().getHomeList()
        )
        // }
        return homeList!!

    }
}