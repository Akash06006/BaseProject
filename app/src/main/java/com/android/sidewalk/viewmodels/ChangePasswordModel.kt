package com.android.sidewalk.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.view.View
import com.android.sidewalk.common.UtilsFunctions
import com.android.sidewalk.model.CommonModel
import com.android.sidewalk.repositories.ResetPasswordRepository
import com.android.sidewalk.sharedpreference.SharedPrefClass
import com.google.gson.JsonObject

class ChangePasswordModel : BaseViewModel() {
    private var data : MutableLiveData<CommonModel>? = null
    private var resetPasswordRepository : ResetPasswordRepository? = null
    private var sharedPrefClass : SharedPrefClass? = null
    private val mIsUpdating = MutableLiveData<Boolean>()
    private val postClick = MutableLiveData<String>()

    init {
        resetPasswordRepository =
            ResetPasswordRepository()
        sharedPrefClass = SharedPrefClass()
        data = resetPasswordRepository!!.getChangePasswordResponse(JsonObject())

    }

    fun getChangePasswordResponse() : LiveData<CommonModel> {
        return data!!
    }

    override fun isLoading() : LiveData<Boolean> {
        return mIsUpdating
    }

    override fun isClick() : LiveData<String> {
        return postClick
    }

    fun changePasswordApi(oldPass : String, password : String) {
        val mJsonObject = JsonObject()
        mJsonObject.addProperty("old_password", oldPass)
        mJsonObject.addProperty("new_password", password)

        if (UtilsFunctions.isNetworkConnected()) {
            data = resetPasswordRepository!!.getChangePasswordResponse(mJsonObject)
            mIsUpdating.postValue(true)

        }
    }

    override fun clickListener(v : View) {
        postClick.postValue(v.resources.getResourceName(v.id).split("/")[1])
    }

}