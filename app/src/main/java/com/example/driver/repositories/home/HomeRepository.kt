package com.example.services.repositories.home

import androidx.lifecycle.MutableLiveData
import com.example.driver.model.CommonModel
import com.google.gson.GsonBuilder

class HomeRepository {
    private var data1 : MutableLiveData<CommonModel>? = null
    private val gson = GsonBuilder().serializeNulls().create()

    init {
        data1 = MutableLiveData()
    }

}