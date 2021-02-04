package com.android.sidewalk.viewmodels

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.view.View
import com.android.sidewalk.R
import com.android.sidewalk.common.UtilsFunctions
import com.android.sidewalk.model.CommonModel
import com.android.sidewalk.repositories.ContactUsRepository
import com.google.gson.JsonObject

class ContactUsViewModel : BaseViewModel() {
    private var addConcern = MutableLiveData<CommonModel>()
    private val mIsUpdating = MutableLiveData<Boolean>()
    private val btnClick = MutableLiveData<String>()
    private var contactUsRepository = ContactUsRepository()

    init {
        if (UtilsFunctions.isNetworkConnected()) {
            addConcern = contactUsRepository.addConcern(null)
            //clearAllNotifications = notificationRepository.clearAllNotifications("")
        }
    }

    override fun isLoading() : LiveData<Boolean> {
        return mIsUpdating
    }

    override fun isClick() : LiveData<String> {
        return btnClick
    }

    override fun clickListener(v : View) {
        btnClick.value = v.resources.getResourceName(v.id).split("/")[1]
    }

    fun addConcernRes() : LiveData<CommonModel> {
        return addConcern
    }

    @SuppressLint("HardwareIds")
    fun clickListener(
        v : View,
        name : String,
        email : String,
        phoneNumber : String,
        message : String
    ) {
        // Toast.makeText(MyApplication.instance, "$email $password", Toast.LENGTH_LONG).show()
        when (v.id) {
            R.id.btn_send -> {
            }
        }
    }

    fun addConcern(obj : JsonObject) {
        if (UtilsFunctions.isNetworkConnected()) {
            addConcern = contactUsRepository.addConcern(obj)
            mIsUpdating.postValue(true)
        }
    }

}