package com.example.driver.views.orders

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.driver.R
import com.example.driver.adapters.orders.DiscountListAdapter
import com.example.driver.adapters.orders.VehiclesListAdapter
import com.example.driver.adapters.orders.WeightListAdapter
import com.example.driver.common.UtilsFunctions
import com.example.driver.databinding.ActivityCreateOrderBinding
import com.example.driver.model.order.ListsResponse
import com.example.driver.utils.BaseActivity
import com.example.driver.viewmodels.order.OrderViewModel

class CreateOrderActivty : BaseActivity() {
    private lateinit var activityCreateOrderBinding : ActivityCreateOrderBinding
    private lateinit var orderViewModel : OrderViewModel
    var vehicleList = ArrayList<ListsResponse.VehicleData>()
    var weightList = ArrayList<ListsResponse.WeightData>()
    var vehiclesAdapter : VehiclesListAdapter? = null
    var weightAdapter : WeightListAdapter? = null


    override fun getLayoutId() : Int {
        return R.layout.activity_create_order
    }

    override fun initViews() {
        activityCreateOrderBinding = viewDataBinding as ActivityCreateOrderBinding
        orderViewModel = ViewModelProviders.of(this).get(OrderViewModel::class.java)
        activityCreateOrderBinding.orderViewModel = orderViewModel

        activityCreateOrderBinding.toolbarCommon.imgToolbarText.text =
            getString(R.string.create_order)
        if (UtilsFunctions.isNetworkConnected()) {
            startProgressDialog()
        }

        orderViewModel.getListsRes().observe(this,
            Observer<ListsResponse> { response->
                stopProgressDialog()

                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            vehicleList.addAll(response.data?.vehicleData!!)
                            weightList.addAll(response.data?.weightData!!)

                            initDiscountsAdapter()
                            initWeightAdapter()
                            initVehiclesAdapter()
                        }
                        else -> message?.let { UtilsFunctions.showToastError(it) }
                    }
                }
            })

    }

    private fun initDiscountsAdapter() {
        /*val adapter = CategoriesGridListAdapter(this@HomeFragment, categoriesList, activity!!)
        fragmentHomeBinding.gridview.adapter = adapter*/
        val discountAdapter =
            DiscountListAdapter(
                this,
                null,
                this
            )
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.HORIZONTAL
        //val gridLayoutManager = GridLayoutManager(this, 4)
        activityCreateOrderBinding.rvDiscounts.layoutManager = linearLayoutManager
        activityCreateOrderBinding.rvDiscounts.setHasFixedSize(true)
        activityCreateOrderBinding.rvDiscounts.adapter = discountAdapter
        activityCreateOrderBinding.rvDiscounts.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView : RecyclerView, dx : Int, dy : Int) {

            }
        })

    }

    private fun initVehiclesAdapter() {
        /*val adapter = CategoriesGridListAdapter(this@HomeFragment, categoriesList, activity!!)
        fragmentHomeBinding.gridview.adapter = adapter*/
        vehiclesAdapter =
            VehiclesListAdapter(
                this,
                vehicleList,
                this
            )
        // val linearLayoutManager = LinearLayoutManager(this)
        //val gridLayoutManager = GridLayoutManager(this, 4)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.HORIZONTAL
        activityCreateOrderBinding.rvVehicles.layoutManager = linearLayoutManager
        activityCreateOrderBinding.rvVehicles.setHasFixedSize(true)
        activityCreateOrderBinding.rvVehicles.adapter = vehiclesAdapter
        activityCreateOrderBinding.rvVehicles.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView : RecyclerView, dx : Int, dy : Int) {

            }
        })

    }

    private fun initWeightAdapter() {
        /*val adapter = CategoriesGridListAdapter(this@HomeFragment, categoriesList, activity!!)
        fragmentHomeBinding.gridview.adapter = adapter*/
        weightAdapter =
            WeightListAdapter(
                this,
                weightList,
                this
            )
        // val linearLayoutManager = LinearLayoutManager(this)
        // val gridLayoutManager = GridLayoutManager(this, 4)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.HORIZONTAL
        activityCreateOrderBinding.rvWeight.layoutManager = linearLayoutManager
        activityCreateOrderBinding.rvWeight.setHasFixedSize(true)
        activityCreateOrderBinding.rvWeight.adapter = weightAdapter
        activityCreateOrderBinding.rvWeight.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView : RecyclerView, dx : Int, dy : Int) {

            }
        })

    }

    fun selectedVehicle(position : Int, s : String) {
        for (i in 0 until vehicleList.count()) {
            vehicleList[i].selected = "false"
        }
        vehicleList[position].selected = "true"
        vehiclesAdapter?.notifyDataSetChanged()
    }

    fun selectedWeight(position : Int, s : String) {
        for (i in 0 until weightList.count()) {
            weightList[i].selected = "false"
        }
        weightList[position].selected = "true"
        weightAdapter?.notifyDataSetChanged()
    }
}
