package com.example.services.repositories.home

import androidx.lifecycle.MutableLiveData
import com.android.sidewalk.R
import com.android.sidewalk.api.ApiClient
import com.android.sidewalk.api.ApiResponse
import com.android.sidewalk.api.ApiService
import com.android.sidewalk.application.MyApplication
import com.android.sidewalk.common.UtilsFunctions
import com.android.sidewalk.model.CommonModel
import com.android.sidewalk.model.truck.GalleryListResponse
import com.android.sidewalk.model.truck.TruckDetailResponse
import com.android.sidewalk.model.truck.TruckListResponse
import com.android.sidewalk.repositories.truck.AddGalleryModel
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.util.HashMap

class TruckRepository {
    private var data1 : MutableLiveData<CommonModel>? = null
    private var addGallery : MutableLiveData<AddGalleryModel>? = null
    private var truckDetail : MutableLiveData<TruckDetailResponse>? = null
    private var truckList : MutableLiveData<TruckListResponse>? = null
    private var galleryList : MutableLiveData<GalleryListResponse>? = null
    private val gson = GsonBuilder().serializeNulls().create()

    init {
        data1 = MutableLiveData()
        addGallery = MutableLiveData()
        truckList = MutableLiveData()
        truckDetail = MutableLiveData()
        galleryList = MutableLiveData()
    }

    fun addUpdateImage(
        truckImages : Array<MultipartBody.Part?>?,
        mHashMap : HashMap<String, RequestBody>?
    ) : MutableLiveData<CommonModel> {
        if (mHashMap != null) {
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
                ApiClient.getApiInterface().addUpdateTruck(
                    truckImages, mHashMap
                )
            )

        }
        return data1!!

    }

    fun addGalleryImage(
        userImage : MultipartBody.Part?
    ) : MutableLiveData<AddGalleryModel> {
        if (userImage != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse : Response<JsonObject>) {
                        val loginResponse = if (mResponse.body() != null)
                            gson.fromJson<AddGalleryModel>(
                                "" + mResponse.body(),
                                AddGalleryModel::class.java
                            )
                        else {
                            gson.fromJson<AddGalleryModel>(
                                mResponse.errorBody()!!.charStream(),
                                AddGalleryModel::class.java
                            )
                        }


                        addGallery!!.postValue(loginResponse)

                    }

                    override fun onError(mKey : String) {
                        UtilsFunctions.showToastError(
                            MyApplication.instance.getString(
                                R.string.internal_server_error
                            )
                        )
                        addGallery!!.postValue(null)

                    }

                },
                ApiClient.getApiInterface().addGalleryImage(
                    userImage
                )
            )

        }
        return addGallery!!

    }

    fun truckList(isCalled : String?) : MutableLiveData<TruckListResponse> {
        if (isCalled != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse : Response<JsonObject>) {
                        val loginResponse = if (mResponse.body() != null)
                            gson.fromJson<TruckListResponse>(
                                "" + mResponse.body(),
                                TruckListResponse::class.java
                            )
                        else {
                            gson.fromJson<TruckListResponse>(
                                mResponse.errorBody()!!.charStream(),
                                TruckListResponse::class.java
                            )
                        }


                        truckList!!.postValue(loginResponse)

                    }

                    override fun onError(mKey : String) {
                        UtilsFunctions.showToastError(
                            MyApplication.instance.getString(
                                R.string.internal_server_error
                            )
                        )
                        truckList!!.postValue(null)

                    }

                },
                ApiClient.getApiInterface().truckList()
            )
        }
        return truckList!!

    }

    fun truckDetail(id : String?) : MutableLiveData<TruckDetailResponse> {
        if (id != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse : Response<JsonObject>) {
                        val loginResponse = if (mResponse.body() != null)
                            gson.fromJson<TruckDetailResponse>(
                                "" + mResponse.body(),
                                TruckDetailResponse::class.java
                            )
                        else {
                            gson.fromJson<TruckDetailResponse>(
                                mResponse.errorBody()!!.charStream(),
                                TruckDetailResponse::class.java
                            )
                        }


                        truckDetail!!.postValue(loginResponse)

                    }

                    override fun onError(mKey : String) {
                        UtilsFunctions.showToastError(
                            MyApplication.instance.getString(
                                R.string.internal_server_error
                            )
                        )
                        truckDetail!!.postValue(null)

                    }

                },
                ApiClient.getApiInterface().truckDetail(id)
            )
        }
        return truckDetail!!

    }

    fun viewGallery(id : String?) : MutableLiveData<GalleryListResponse> {
        if (id != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse : Response<JsonObject>) {
                        val loginResponse = if (mResponse.body() != null)
                            gson.fromJson<GalleryListResponse>(
                                "" + mResponse.body(),
                                GalleryListResponse::class.java
                            )
                        else {
                            gson.fromJson<GalleryListResponse>(
                                mResponse.errorBody()!!.charStream(),
                                GalleryListResponse::class.java
                            )
                        }


                        galleryList!!.postValue(loginResponse)

                    }

                    override fun onError(mKey : String) {
                        UtilsFunctions.showToastError(
                            MyApplication.instance.getString(
                                R.string.internal_server_error
                            )
                        )
                        galleryList!!.postValue(null)

                    }

                },
                ApiClient.getApiInterface().viewGallery(id)
            )
        }
        return galleryList!!

    }

}