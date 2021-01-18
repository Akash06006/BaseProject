package com.android.sidewalk.viewmodels.home

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.sidewalk.common.UtilsFunctions
import com.android.sidewalk.model.CommonModel
import com.android.sidewalk.model.home.HomeListResponse
import com.android.sidewalk.viewmodels.BaseViewModel
import com.example.services.repositories.home.HomeRepository
import okhttp3.MultipartBody

class HomeViewModel : BaseViewModel() {
    private val mIsUpdating = MutableLiveData<Boolean>()
    private val isClick = MutableLiveData<String>()
    private var homeRepository = HomeRepository()
    private var clearCart =
        MutableLiveData<CommonModel>()
    private var addBanner = MutableLiveData<CommonModel>()
    private var homeList = MutableLiveData<HomeListResponse>()

    init {
        if (UtilsFunctions.isNetworkConnectedReturn()) {
            addBanner = homeRepository.addBannerImage(null)
            homeList = homeRepository.getHomeList()
        }

    }

    /*

   fun getGetSubServices(): LiveData<CategoriesListResponse> {
       return subServicesList
   }*/
    fun addBannerResponse() : LiveData<CommonModel> {
        return addBanner
    }

    fun getHomeListRes() : LiveData<HomeListResponse> {
        return homeList
    }

    override fun isLoading() : LiveData<Boolean> {
        return mIsUpdating
    }

    override fun isClick() : LiveData<String> {
        return isClick
    }

    override fun clickListener(v : View) {
        isClick.value = v.resources.getResourceName(v.id).split("/")[1]
    }

    fun addBannerImage(mJsonObject : MultipartBody.Part?) {
        if (UtilsFunctions.isNetworkConnected()) {
            addBanner = homeRepository.addBannerImage(mJsonObject)
            mIsUpdating.postValue(true)
        }

    }

    fun homeList() {
        if (UtilsFunctions.isNetworkConnected()) {
            homeList = homeRepository.getHomeList()
            mIsUpdating.postValue(true)
        }

    }

}