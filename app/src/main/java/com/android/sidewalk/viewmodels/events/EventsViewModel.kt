package com.android.sidewalk.viewmodels.events

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.sidewalk.common.UtilsFunctions
import com.android.sidewalk.model.CommonModel
import com.android.sidewalk.model.events.EventListResponse
import com.android.sidewalk.model.truck.GalleryListResponse
import com.android.sidewalk.model.truck.TruckDetailResponse
import com.android.sidewalk.model.truck.TruckListResponse
import com.android.sidewalk.repositories.truck.AddGalleryModel
import com.android.sidewalk.viewmodels.BaseViewModel
import com.example.services.repositories.home.EventsRepository

class EventsViewModel : BaseViewModel() {
    private val mIsUpdating = MutableLiveData<Boolean>()
    private val isClick = MutableLiveData<String>()
    private var truckRepository = EventsRepository()
    private var addGallery =
        MutableLiveData<AddGalleryModel>()
    private var eventsList =
        MutableLiveData<EventListResponse>()
    private var truckDetail =
        MutableLiveData<TruckDetailResponse>()
    private var viewGallery =
        MutableLiveData<GalleryListResponse>()
    private var addUpdateTruck = MutableLiveData<CommonModel>()

    init {
        if (UtilsFunctions.isNetworkConnectedReturn()) {
            addUpdateTruck = truckRepository.addUpdateImage(null, null)
            eventsList = truckRepository.eventsList("0")
            truckDetail = truckRepository.truckDetail(null)
        }

    }

    fun getEventListRes() : LiveData<EventListResponse> {
        return eventsList
    }

    fun getEventDetail() : LiveData<TruckDetailResponse> {
        return truckDetail
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

    fun eventList(type : String) {
        if (UtilsFunctions.isNetworkConnected()) {
            eventsList = truckRepository.eventsList(type)
            mIsUpdating.postValue(true)
        }

    }

    fun eventDetail(id : String) {
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