package com.android.sidewalk.views.events

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
import com.android.sidewalk.adapters.trucks.TruckImageAdapter
import com.android.sidewalk.callbacks.ChoiceCallBack
import com.android.sidewalk.common.UtilsFunctions
import com.android.sidewalk.databinding.ActivityEventDetailBinding
import com.android.sidewalk.databinding.ActivityTruckDetailBinding
import com.android.sidewalk.model.CommonModel
import com.android.sidewalk.model.events.EventDetailResponse
import com.android.sidewalk.model.truck.TruckDetailResponse
import com.android.sidewalk.repositories.truck.AddGalleryModel
import com.android.sidewalk.utils.BaseActivity
import com.android.sidewalk.utils.DialogClass
import com.android.sidewalk.utils.Utils
import com.android.sidewalk.utils.ValidationsClass
import com.android.sidewalk.viewmodels.events.EventsViewModel
import com.android.sidewalk.viewmodels.menu.MenuViewModel
import com.android.sidewalk.viewmodels.trucks.TrucksViewModel
import com.bumptech.glide.Glide
import com.uniongoods.adapters.GalleryImagesListAdapter
import com.uniongoods.adapters.ImagesListAdapter
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class EventDetailActivity : BaseActivity() {
    private var truckImages : ArrayList<String>? = null
    private lateinit var addTruckBinding : ActivityEventDetailBinding
    private lateinit var trucksViewModel : EventsViewModel
    var viewPager : ViewPager? = null
    var imagesList = ArrayList<String>()
    var eventId = ""
    override fun getLayoutId() : Int {
        return R.layout.activity_event_detail
    }

    override fun initViews() {
        addTruckBinding = viewDataBinding as ActivityEventDetailBinding

        trucksViewModel = ViewModelProviders.of(this)
            .get(EventsViewModel::class.java)
        addTruckBinding.eventViewModel = trucksViewModel
        addTruckBinding.toolbarCommon.imgToolbarText.text =
            getString(R.string.event_detail)
        addTruckBinding.toolbarCommon.imgRight.visibility = View.GONE

        eventId = intent.extras?.get("id") as String
        val eventType = intent.extras?.get("eventType") as String
        if (eventType.equals("accept")) {
            addTruckBinding.btnAccept.visibility = View.GONE
            addTruckBinding.btnReject.visibility = View.GONE
        }

        if (UtilsFunctions.isNetworkConnected()) {
            startProgressDialog()
            trucksViewModel.eventDetail(eventId)
        }
        // viewPager = findViewById<ViewPager>(R.id.viewPager)
        trucksViewModel.getEventDetail().observe(this,
            Observer<EventDetailResponse> { eventDetailResponse->
                stopProgressDialog()
                if (eventDetailResponse != null) {
                    val message = eventDetailResponse.message

                    if (eventDetailResponse.code == 200) {
                        // galleryImagesIds.add(eventDetailResponse.categoryList!!.image!!)
                        addTruckBinding.detailResponse = eventDetailResponse.data

                    } else {
                        UtilsFunctions.showToastError(message!!)
                    }

                }
            })

        trucksViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it : String?) {
                when (it) {
                    "btnAccept" -> {
                        showToastSuccess("Coming Soon")
                    }
                    "btnReject" -> {
                        showToastSuccess("Coming Soon")
                    }
                    "imgChat" -> {
                        showToastSuccess("Coming Soon")
                    }
                }
            })
        )
    }
}
