package com.android.sidewalk.viewmodels.menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.view.View
import com.android.sidewalk.common.UtilsFunctions
import com.android.sidewalk.model.CommonModel
import com.android.sidewalk.model.LoginResponse
import com.android.sidewalk.model.menu.CategoryListsResponse
import com.android.sidewalk.model.menu.ItemListResponse
import com.android.sidewalk.repositories.menu.MenuRepository
import com.android.sidewalk.viewmodels.BaseViewModel
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody

class MenuViewModel : BaseViewModel() {
    private var categoryList = MutableLiveData<CategoryListsResponse>()
    private var itemsList = MutableLiveData<ItemListResponse>()
    private var addCateogry = MutableLiveData<CommonModel>()
    private var profileDetail = MutableLiveData<LoginResponse>()
    private var menuRepository =
        MenuRepository()
    private val mIsUpdating = MutableLiveData<Boolean>()
    private val btnClick = MutableLiveData<String>()

    init {
        if (UtilsFunctions.isNetworkConnectedReturn()) {
            categoryList = menuRepository.getCategoryLists()
            addCateogry = menuRepository.addCategory(null, null)
            itemsList = menuRepository.getItemsLists(null)
        }

    }

    fun getCategoryListsRes() : LiveData<CategoryListsResponse> {
        return categoryList
    }

    fun getItemListsRes() : LiveData<ItemListResponse> {
        return itemsList
    }

    fun getAddCategoryRes() : LiveData<CommonModel> {
        return addCateogry
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

    fun addCategory(
        catImagePart : MultipartBody.Part,
        mHashMap : java.util.HashMap<String, RequestBody>
    ) {
        if (UtilsFunctions.isNetworkConnected()) {
            addCateogry = menuRepository.addCategory(mHashMap, catImagePart)
            mIsUpdating.postValue(true)

        }
    }

    fun addItem(
        catImagePart : MultipartBody.Part,
        mHashMap : java.util.HashMap<String, RequestBody>
    ) {
        if (UtilsFunctions.isNetworkConnected()) {
            addCateogry = menuRepository.addItem(mHashMap, catImagePart)
            mIsUpdating.postValue(true)

        }
    }

    fun categoryList() {
        if (UtilsFunctions.isNetworkConnected()) {
            categoryList = menuRepository.getCategoryLists()
            mIsUpdating.postValue(true)

        }
    }

    fun itemsList(catId : String) {
        if (UtilsFunctions.isNetworkConnected()) {
            itemsList = menuRepository.getItemsLists(catId)
            mIsUpdating.postValue(true)

        }
    }

}