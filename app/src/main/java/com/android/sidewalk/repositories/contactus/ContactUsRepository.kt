package com.android.sidewalk.repositories

import androidx.lifecycle.MutableLiveData
import com.android.sidewalk.R
import com.android.sidewalk.api.ApiClient
import com.android.sidewalk.api.ApiResponse
import com.android.sidewalk.api.ApiService
import com.android.sidewalk.application.MyApplication
import com.android.sidewalk.common.UtilsFunctions
import com.android.sidewalk.model.CommonModel
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import retrofit2.Response

class ContactUsRepository {
    private var data3 : MutableLiveData<CommonModel>? = null
    private val gson = GsonBuilder().serializeNulls().create()

    init {
        data3 = MutableLiveData()
    }

    fun addConcern(obj : JsonObject?) : MutableLiveData<CommonModel> {
        if (obj != null) {
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
                        data3!!.postValue(loginResponse)
                    }

                    override fun onError(mKey : String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        data3!!.postValue(null)
                    }

                }, ApiClient.getApiInterface().contactUs(obj)
            )
        }
        return data3!!
    }

}