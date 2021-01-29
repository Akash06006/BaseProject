package com.android.sidewalk.views.trucks

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.sidewalk.R
import com.android.sidewalk.common.UtilsFunctions.showToastError
import com.android.sidewalk.databinding.FragmentTruckBinding
import com.android.sidewalk.maps.FusedLocationClass
import com.android.sidewalk.model.truck.TruckListResponse
import com.android.sidewalk.utils.BaseFragment
import com.android.sidewalk.utils.DialogClass
import com.android.sidewalk.viewmodels.trucks.TrucksViewModel
import com.android.sidewalk.views.home.LandingActivty
import com.android.sidewalk.views.menu.CategoryListActivity
import com.google.android.gms.location.*
import com.uniongoods.adapters.TruckListAdapter

class
TruckListFragment : BaseFragment() {
    private var truckList = ArrayList<TruckListResponse.Data>()
    private var mFusedLocationClass : FusedLocationClass? =
        null
    private lateinit var truckViewModel : TrucksViewModel
    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient : FusedLocationProviderClient
    var currentLat = ""
    var currentLong = ""
    var isFABOpen : Boolean? = false
    private lateinit var fragmentTruckBinding : FragmentTruckBinding
    private val PERMISSION_REQUEST_CODE : Int = 101
    private var confirmationDialog : Dialog? = null
    private var mDialogClass = DialogClass()
    //var categoriesList = null
    override fun getLayoutResId() : Int {
        return R.layout.fragment_truck
    }

    override fun onResume() {
        super.onResume()
    }

    //api/mobile/services/getSubcat/b21a7c8f-078f-4323-b914-8f59054c4467
    override fun initView() {
        fragmentTruckBinding = viewDataBinding as FragmentTruckBinding
        truckViewModel = ViewModelProviders.of(this)
            .get(TrucksViewModel::class.java)
        fragmentTruckBinding.truckViewModel = truckViewModel
        // categoriesList=List<Service>()
        truckViewModel.truckList()
        fragmentTruckBinding.toolbarCommon.imgToolbarText.text =
            getString(R.string.my_mobile_carts)
        mFusedLocationClass =
            FusedLocationClass(activity)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        // initRecyclerView()
        truckViewModel.getTruckListRes().observe(this,
            Observer<TruckListResponse> { loginResponse->
            //stopProgressDialog()
                if (loginResponse != null) {
                    val message = loginResponse.message

                    if (loginResponse.code == 200) {
                        truckList = loginResponse.data!!
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

        truckViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it : String?) {
                when (it) {
                }
            })
        )

        fragmentTruckBinding.fab.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view : View?) {
                if (!isFABOpen!!) {
                    showFABMenu()
                } else {
                    closeFABMenu()
                }
            }
        })

        fragmentTruckBinding.fab2.setOnClickListener{
           val intent= Intent(
                context,
                AddTruckActivity::class.java
            )
            intent.putExtra("id","")
            startActivity(intent)
        }
        fragmentTruckBinding.fab1.setOnClickListener{
            val intent= Intent(
                context,
                CategoryListActivity::class.java
            )
            startActivity(intent)
        }

    }

    private fun showFABMenu() {
        isFABOpen = true
        fragmentTruckBinding.fab1.animate().translationY(-resources.getDimension(R.dimen.dp_70))
        fragmentTruckBinding.fab2.animate().translationY(-resources.getDimension(R.dimen.dp_140))
    }

    private fun closeFABMenu() {
        isFABOpen = false
        fragmentTruckBinding.fab1.animate().translationY(0F)
        fragmentTruckBinding.fab2.animate().translationY(0F)
    }

    private fun initRecyclerView() {
        val linearLayoutManager1 = LinearLayoutManager(activity)
        val truckListAdapter = TruckListAdapter(this, this, truckList, activity!!)
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