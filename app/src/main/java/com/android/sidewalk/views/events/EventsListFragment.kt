package com.android.sidewalk.views.events

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.widget.Toast
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.sidewalk.utils.BaseFragment
import com.android.sidewalk.R
import com.android.sidewalk.common.UtilsFunctions
import com.android.sidewalk.common.UtilsFunctions.showToastError
import com.android.sidewalk.databinding.FragmentEventsBinding
import com.android.sidewalk.maps.FusedLocationClass
import com.android.sidewalk.model.events.EventListResponse
import com.android.sidewalk.utils.DialogClass
import com.android.sidewalk.viewmodels.events.EventsViewModel
import com.google.android.gms.location.*
import com.uniongoods.adapters.EventListAdapter

class
EventsListFragment : BaseFragment() {
    private var eventList = ArrayList<EventListResponse.Data>()
    private var mFusedLocationClass : FusedLocationClass? =
        null
    private lateinit var eventsViewModel : EventsViewModel
    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient : FusedLocationProviderClient
    var currentLat = ""
    var currentLong = ""
    var eventType = "pending"
    private lateinit var fragmentTruckBinding : FragmentEventsBinding
    //var categoriesList = null
    override fun getLayoutResId() : Int {
        return R.layout.fragment_events
    }

    override fun onResume() {
        super.onResume()
    }

    //api/mobile/services/getSubcat/b21a7c8f-078f-4323-b914-8f59054c4467
    override fun initView() {
        fragmentTruckBinding = viewDataBinding as FragmentEventsBinding
        eventsViewModel = ViewModelProviders.of(this)
            .get(EventsViewModel::class.java)
        fragmentTruckBinding.truckViewModel = eventsViewModel
        // categoriesList=List<Service>()
        if (UtilsFunctions.isNetworkConnected()) {
            baseActivity.startProgressDialog()
            eventsViewModel.eventList("0")
        }
        fragmentTruckBinding.toolbarCommon.imgToolbarText.text =
            getString(R.string.events)
        mFusedLocationClass =
            FusedLocationClass(activity)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        // initRecyclerView()
        eventsViewModel.getEventListRes().observe(this,
            Observer<EventListResponse> { loginResponse->
                baseActivity.stopProgressDialog()
                if (loginResponse != null) {
                    val message = loginResponse.message

                    if (loginResponse.code == 200) {
                        eventList.clear()
                        eventList = loginResponse.data!!
                        initRecyclerView()
                        /*if (!TextUtils.isEmpty(loginResponse.categoryList!!.vendorData!!.image)) {
                            Glide.with(activity!!).load(loginResponse.categoryList!!.vendorData!!.image)
                                .into(fragmentTruckBinding.imgRight)
                        }

                        if (!TextUtils.isEmpty(loginResponse.categoryList!!.vendorData!!.cover)) {
                            Glide.with(activity!!).load(loginResponse.categoryList!!.vendorData!!.cover)
                                .placeholder(R.drawable.ic_home_banner)
                                .into(fragmentTruckBinding.imgBanner)
                        }*/
                    } else {
                        showToastError(message!!)
                    }

                }
            })

        eventsViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it : String?) {
                when (it) {
                    "txtPending" -> {
                        eventType = "pending"
                        fragmentTruckBinding.txtPending.setTextColor(resources.getColor(R.color.colorWhite))
                        fragmentTruckBinding.txtAccepted.setTextColor(resources.getColor(R.color.colorGrey))
                        fragmentTruckBinding.txtPending.background =
                            resources.getDrawable(R.drawable.ic_event_selected)
                        fragmentTruckBinding.txtAccepted.background =
                            resources.getDrawable(R.drawable.ic_event_unselected)
                        if (UtilsFunctions.isNetworkConnected()) {
                            baseActivity.startProgressDialog()
                            eventsViewModel.eventList("0")
                        }
                    }
                    "txtAccepted" -> {
                        eventType = "accept"
                        fragmentTruckBinding.txtAccepted.setTextColor(resources.getColor(R.color.colorWhite))
                        fragmentTruckBinding.txtPending.setTextColor(resources.getColor(R.color.colorGrey))
                        fragmentTruckBinding.txtAccepted.background =
                            resources.getDrawable(R.drawable.ic_event_selected)
                        fragmentTruckBinding.txtPending.background =
                            resources.getDrawable(R.drawable.ic_event_unselected)
                        if (UtilsFunctions.isNetworkConnected()) {
                            baseActivity.startProgressDialog()
                            eventsViewModel.eventList("1")
                        }
                    }
                }
            })
        )

    }

    private fun initRecyclerView() {
        val linearLayoutManager1 = LinearLayoutManager(activity)
        val truckListAdapter = EventListAdapter(this, eventType, eventList, activity!!)
        fragmentTruckBinding.rvTrucks.setHasFixedSize(true)
        linearLayoutManager1.orientation = RecyclerView.VERTICAL
        fragmentTruckBinding.rvTrucks.layoutManager = linearLayoutManager1
        fragmentTruckBinding.rvTrucks.adapter = truckListAdapter
        fragmentTruckBinding.rvTrucks.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView : RecyclerView, dx : Int, dy : Int) {

            }
        })
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(activity!!) { task->
                    var location : Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        currentLat = location.latitude.toString()
                        currentLong = location.longitude.toString()
                        /*  Handler().postDelayed({
                              callSocketMethods("updateVehicleLocation")
                          }, 2000)
  */
                    }
                }
            } else {
                Toast.makeText(activity, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    private fun checkPermissions() : Boolean {
        if (ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            activity!!,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    private fun isLocationEnabled() : Boolean {
        var locationManager : LocationManager =
            activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    @SuppressLint("MissingPermission", "RestrictedApi")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )

    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult : LocationResult) {
            var mLastLocation : Location = locationResult.lastLocation
            currentLat = mLastLocation.latitude.toString()
            currentLong = mLastLocation.longitude.toString()
            /*Handler().postDelayed({
                callSocketMethods("updateVehicleLocation")
            }, 2000)*/

        }
    }

}