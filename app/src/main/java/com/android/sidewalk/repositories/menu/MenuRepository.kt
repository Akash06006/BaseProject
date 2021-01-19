package com.android.sidewalk.repositories.menu

import androidx.lifecycle.MutableLiveData
import com.android.sidewalk.R
import com.android.sidewalk.api.ApiClient
import com.android.sidewalk.api.ApiResponse
import com.android.sidewalk.api.ApiService
import com.android.sidewalk.application.MyApplication
import com.android.sidewalk.common.UtilsFunctions
import com.android.sidewalk.model.CommonModel
import com.android.sidewalk.model.LoginResponse
import com.android.sidewalk.model.menu.CategoryListsResponse
import com.android.sidewalk.model.menu.ItemDetailResponse
import com.android.sidewalk.model.menu.ItemListResponse
import com.android.sidewalk.model.order.ListsResponse
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.util.HashMap

class MenuRepository {
    private var data : MutableLiveData<LoginResponse>? = null
    private var data1 : MutableLiveData<LoginResponse>? = null
    private var data2 : MutableLiveData<CommonModel>? = null
    private var addCateogry : MutableLiveData<CommonModel>? = null
    private val gson = GsonBuilder().serializeNulls().create()
    private var data3 : MutableLiveData<CategoryListsResponse>? = null
    private var itemList : MutableLiveData<ItemListResponse>? = null
    private var itemDetail : MutableLiveData<ItemDetailResponse>? = null

    init {
        data = MutableLiveData()
        data1 = MutableLiveData()
        data2 = MutableLiveData()
        data3 = MutableLiveData()
        addCateogry = MutableLiveData()
        itemList = MutableLiveData()
        itemDetail = MutableLiveData()

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
                            MyApplication.instance.getString(R.string.internal_server_error)
                        )
                        data1!!.postValue(null)
                    }

                }, ApiClient.getApiInterface().getProfile(/*jsonObject*/)
            )

        }
        return data1!!

    }

    fun getLogoutResonse(jsonObject : JsonObject?) : MutableLiveData<CommonModel> {
        if (jsonObject != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse : Response<JsonObject>) {
                        val logoutResponse = if (mResponse.body() != null)
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

                        data2!!.postValue(logoutResponse)

                    }

                    override fun onError(mKey : String) {
                        UtilsFunctions.showToastError(
                            MyApplication.instance.getString(R.string.internal_server_error)
                        )
                        data1!!.postValue(null)

                    }

                }, ApiClient.getApiInterface().callLogout(jsonObject)
            )

        }
        return data2!!

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
                            MyApplication.instance.getString(R.string.internal_server_error)
                        )
                        data!!.postValue(null)

                    }

                }, ApiClient.getApiInterface().callUpdateProfile(hashMap, image)
            )
        }
        return data!!

    }

    fun getCategoryLists(
    ) : MutableLiveData<CategoryListsResponse> {
        //if (hashMap != null) {
        val mApiService = ApiService<JsonObject>()
        mApiService.get(
            object : ApiResponse<JsonObject> {
                override fun onResponse(mResponse : Response<JsonObject>) {
                    val loginResponse = if (mResponse.body() != null)
                        gson.fromJson<CategoryListsResponse>(
                            "" + mResponse.body(),
                            CategoryListsResponse::class.java
                        )
                    else {
                        gson.fromJson<CategoryListsResponse>(
                            mResponse.errorBody()!!.charStream(),
                            CategoryListsResponse::class.java
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

            }, ApiClient.getApiInterface().getCategoryLists()
        )
        // }
        return data3!!

    }

    fun addCategory(
        mHashMap : HashMap<String, RequestBody>?,
        catImagePart : MultipartBody.Part?
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


                        addCateogry!!.postValue(loginResponse)

                    }

                    override fun onError(mKey : String) {
                        UtilsFunctions.showToastError(
                            MyApplication.instance.getString(R.string.internal_server_error)
                        )
                        addCateogry!!.postValue(null)

                    }

                }, ApiClient.getApiInterface().addCategory(mHashMap, catImagePart)
            )
        }
        return addCateogry!!
    }

    fun addItem(
        mHashMap : HashMap<String, RequestBody>?,
        catImagePart : MultipartBody.Part?
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


                        addCateogry!!.postValue(loginResponse)

                    }

                    override fun onError(mKey : String) {
                        UtilsFunctions.showToastError(
                            MyApplication.instance.getString(R.string.internal_server_error)
                        )
                        addCateogry!!.postValue(null)

                    }

                }, ApiClient.getApiInterface().addItem(mHashMap, catImagePart)
            )
        }
        return addCateogry!!
    }

    fun getItemsLists(
        catId : String?
    ) : MutableLiveData<ItemListResponse> {
        if (catId != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse : Response<JsonObject>) {
                        val loginResponse = if (mResponse.body() != null)
                            gson.fromJson<ItemListResponse>(
                                "" + mResponse.body(),
                                ItemListResponse::class.java
                            )
                        else {
                            gson.fromJson<ItemListResponse>(
                                mResponse.errorBody()!!.charStream(),
                                ItemListResponse::class.java
                            )
                        }


                        itemList!!.postValue(loginResponse)

                    }

                    override fun onError(mKey : String) {
                        UtilsFunctions.showToastError(
                            MyApplication.instance.getString(R.string.internal_server_error)
                        )
                        itemList!!.postValue(null)

                    }

                }, ApiClient.getApiInterface().getItemsList(catId)
            )
        }
        return itemList!!

    }

    fun getItemDetail(
        catId : String?
    ) : MutableLiveData<ItemDetailResponse> {
        if (catId != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse : Response<JsonObject>) {
                        val loginResponse = if (mResponse.body() != null)
                            gson.fromJson<ItemDetailResponse>(
                                "" + mResponse.body(),
                                ItemDetailResponse::class.java
                            )
                        else {
                            gson.fromJson<ItemDetailResponse>(
                                mResponse.errorBody()!!.charStream(),
                                ItemDetailResponse::class.java
                            )
                        }

                        itemDetail!!.postValue(loginResponse)

                    }

                    override fun onError(mKey : String) {
                        UtilsFunctions.showToastError(
                            MyApplication.instance.getString(R.string.internal_server_error)
                        )
                        itemDetail!!.postValue(null)

                    }

                }, ApiClient.getApiInterface().itemDetail(catId)
            )
        }
        return itemDetail!!

    }
}