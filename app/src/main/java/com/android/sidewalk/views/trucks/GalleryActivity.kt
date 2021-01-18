package com.android.sidewalk.views.trucks

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProviders
import com.android.sidewalk.R
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.android.sidewalk.callbacks.ChoiceCallBack
import com.android.sidewalk.common.UtilsFunctions
import com.android.sidewalk.databinding.ActivityGalleryBinding
import com.android.sidewalk.model.CommonModel
import com.android.sidewalk.model.truck.GalleryListResponse
import com.android.sidewalk.model.truck.TruckDetailResponse
import com.android.sidewalk.repositories.truck.AddGalleryModel
import com.android.sidewalk.utils.BaseActivity
import com.android.sidewalk.utils.DialogClass
import com.android.sidewalk.utils.Utils
import com.android.sidewalk.utils.ValidationsClass
import com.android.sidewalk.viewmodels.trucks.TrucksViewModel
import com.bumptech.glide.Glide
import com.uniongoods.adapters.GalleryImagesListAdapter
import com.uniongoods.adapters.ImagesListAdapter
import com.uniongoods.adapters.ViewGalleryAdapter
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class GalleryActivity : BaseActivity() {
    private lateinit var addTruckBinding : ActivityGalleryBinding
    private lateinit var trucksViewModel : TrucksViewModel
    var viewPager : ViewPager? = null
    var imagesList = ArrayList<String>()
    override fun getLayoutId() : Int {
        return R.layout.activity_gallery
    }

    override fun initViews() {
        addTruckBinding = viewDataBinding as ActivityGalleryBinding

        trucksViewModel = ViewModelProviders.of(this)
            .get(TrucksViewModel::class.java)
        addTruckBinding.truckViewModel = trucksViewModel
        addTruckBinding.toolbarCommon.imgToolbarText.text =
            getString(R.string.view_gallery)
        val id = intent.extras?.get("id") as String

        trucksViewModel.viewGallery(id)

        trucksViewModel.getViewGallery().observe(this,
            Observer<GalleryListResponse> { viewGalleryResponse->
                stopProgressDialog()
                if (viewGalleryResponse != null) {
                    val message = viewGalleryResponse.message

                    if (viewGalleryResponse.code == 200) {
                        for (item in viewGalleryResponse.data!!) {
                            imagesList.add(item.image!!)
                        }
                        val mainAdapter = ViewGalleryAdapter(this, this, imagesList, this)
                        addTruckBinding.gvGallery.adapter = mainAdapter
                    } else {
                        UtilsFunctions.showToastError(message!!)
                    }

                }
            })

    }

    private fun initRecyclerView() {

    }

}
