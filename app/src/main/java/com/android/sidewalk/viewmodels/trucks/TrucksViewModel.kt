package com.android.sidewalk.viewmodels.trucks

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.sidewalk.common.UtilsFunctions
import com.android.sidewalk.model.CommonModel
import com.android.sidewalk.model.truck.GalleryListResponse
import com.android.sidewalk.model.truck.TruckDetailResponse
import com.android.sidewalk.model.truck.TruckListResponse
import com.android.sidewalk.repositories.truck.AddGalleryModel
import com.android.sidewalk.viewmodels.BaseViewModel
import com.example.services.repositories.home.TruckRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.HashMap

class TrucksViewModel : BaseViewModel() {
    private val mIsUpdating = MutableLiveData<Boolean>()
    private val isClick = MutableLiveData<String>()
    private var truckRepository = TruckRepository()
    private var addGallery =
        MutableLiveData<AddGalleryModel>()
    private var truckList =
        MutableLiveData<TruckListResponse>()
    private var truckDetail =
        MutableLiveData<TruckDetailResponse>()
    private var viewGallery =
        MutableLiveData<GalleryListResponse>()
    private var addUpdateTruck = MutableLiveData<CommonModel>()

    init {
        if (UtilsFunctions.isNetworkConnectedReturn()) {
            addUpdateTruck = truckRepository.addUpdateImage(null, null)
            addGallery = truckRepository.addGalleryImage(null)
            truckList = truckRepository.truckList(null)
            truckDetail = truckRepository.truckDetail(null)
            viewGallery = truckRepository.viewGallery(null)
        }

    }

    /*

   fun getGetSubServices(): LiveData<CategoriesListResponse> {
       return subServicesList
   }*/
    fun getAddGalleryRes() : LiveData<AddGalleryModel> {
        return addGallery
    }

    fun getAddTruckRes() : LiveData<CommonModel> {
        return addUpdateTruck
    }

    fun getTruckListRes() : LiveData<TruckListResponse> {
        return truckList
    }

    fun getTruckDetail() : LiveData<TruckDetailResponse> {
        return truckDetail
    }

    fun getViewGallery() : LiveData<GalleryListResponse> {
        return viewGallery
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

    fun addUpdateTruck(
        imagesParts : Array<MultipartBody.Part?>?,
        mHashMap : HashMap<String, RequestBody>
    ) {
        if (UtilsFunctions.isNetworkConnected()) {
            addUpdateTruck = truckRepository.addUpdateImage(imagesParts, mHashMap)
            mIsUpdating.postValue(true)
        }

    }

    fun addGalleryImage(mJsonObject : MultipartBody.Part?) {
        if (UtilsFunctions.isNetworkConnected()) {
            addGallery = truckRepository.addGalleryImage(mJsonObject)
            mIsUpdating.postValue(true)
        }

    }

    fun truckList() {
        if (UtilsFunctions.isNetworkConnected()) {
            truckList = truckRepository.truckList("yes")
            mIsUpdating.postValue(true)
        }

    }

    fun truckDetail(id : String) {
        if (UtilsFunctions.isNetworkConnected()) {
            truckDetail = truckRepository.truckDetail(id)
            mIsUpdating.postValue(true)
        }

    }

    fun viewGallery(id : String) {
        if (UtilsFunctions.isNetworkConnected()) {
            viewGallery = truckRepository.viewGallery(id)
            mIsUpdating.postValue(true)
        }

    }
    /* fun getSubServices(mJsonObject: String) {
         if (UtilsFunctions.isNetworkConnected()) {
             subServicesList = homeRepository.getSubServices(mJsonObject)
             mIsUpdating.postValue(true)
         }

     }*/

}