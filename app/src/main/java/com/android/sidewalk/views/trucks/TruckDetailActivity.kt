package com.android.sidewalk.views.trucks

import android.annotation.SuppressLint
import android.content.Intent
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.android.sidewalk.R
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.android.sidewalk.adapters.trucks.TruckImageAdapter
import com.android.sidewalk.common.UtilsFunctions
import com.android.sidewalk.databinding.ActivityTruckDetailBinding
import com.android.sidewalk.model.ImagesModel
import com.android.sidewalk.model.truck.TruckDetailResponse
import com.android.sidewalk.utils.BaseActivity
import com.android.sidewalk.viewmodels.trucks.TrucksViewModel
import com.uniongoods.adapters.ImagesListAdapter
import java.util.*

class TruckDetailActivity : BaseActivity() {
    private var truckImages : ArrayList<String>? = null
    private lateinit var addTruckBinding : ActivityTruckDetailBinding
    private lateinit var trucksViewModel : TrucksViewModel
    private var imagesList = ArrayList<ImagesModel>()
    var truckId = ""
    override fun getLayoutId() : Int {
        return R.layout.activity_truck_detail
    }

    @SuppressLint("SetTextI18n")
    override fun initViews() {
        addTruckBinding = viewDataBinding as ActivityTruckDetailBinding

        trucksViewModel = ViewModelProviders.of(this)
            .get(TrucksViewModel::class.java)
        addTruckBinding.truckViewModel = trucksViewModel
        addTruckBinding.toolbarCommon.imgToolbarText.text =
            getString(R.string.details)
        addTruckBinding.toolbarCommon.imgRight.visibility = View.VISIBLE
        addTruckBinding.toolbarCommon.imgRight.setImageResource(R.drawable.ic_edit)

        truckId = intent.extras?.get("id") as String

        trucksViewModel.truckDetail(truckId)
        // viewPager = findViewById<ViewPager>(R.id.viewPager)
        trucksViewModel.getTruckDetail().observe(this,
            Observer<TruckDetailResponse> { addGalleryRes->
                stopProgressDialog()
                if (addGalleryRes != null) {
                    val message = addGalleryRes.message

                    if (addGalleryRes.code == 200) {
                        // galleryImagesIds.add(addGalleryRes.categoryList!!.image!!)
                        addTruckBinding.detailResponse = addGalleryRes.data
                        addTruckBinding.txtTime.setText(addGalleryRes.data!!.startTime + " - " + addGalleryRes.data!!.endTime)
                        //showToastSuccess(message)
                        truckImages = addGalleryRes.data!!.truckImages
                        setBannerAdapter()
                        if (addGalleryRes.data!!.galleries == null) {
                            addTruckBinding.txtGallery.visibility = View.GONE
                            addTruckBinding.rvGallery.visibility = View.GONE
                        } else {
                            addTruckBinding.txtGallery.visibility = View.VISIBLE
                            addTruckBinding.rvGallery.visibility = View.VISIBLE
                            for (item in addGalleryRes.data!!.galleries!!) {
                                if (!TextUtils.isEmpty(item.image)) {
                                    val imageModel = ImagesModel()
                                    imageModel.image = item.image
                                    imageModel.name = item.image
                                    imagesList.add(imageModel)
                                }
                            }
                            initRecyclerView()
                        }
                    } else {
                        UtilsFunctions.showToastError(message!!)
                    }

                }
            })



        trucksViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it : String?) {
                when (it) {
                    "img_right" -> {
                        val intent = Intent(
                            this,
                            AddTruckActivity::class.java
                        )
                        intent.putExtra("id", truckId/*categoriesList[position].id*/)

                        startActivity(intent)
                    }
                }
            })
        )

    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        val imagesListAdapter = ImagesListAdapter(this, null, imagesList, this)
        addTruckBinding.rvGallery.setHasFixedSize(true)
        linearLayoutManager.orientation = RecyclerView.HORIZONTAL
        addTruckBinding.rvGallery.layoutManager = linearLayoutManager
        addTruckBinding.rvGallery.adapter = imagesListAdapter
        addTruckBinding.rvGallery.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView : RecyclerView, dx : Int, dy : Int) {

            }
        })

    }

    fun callGalleryActivity() {
        val intent = Intent(
            this,
            GalleryActivity::class.java
        )
        intent.putExtra("id", truckId)
        startActivity(intent)
    }

    private fun setBannerAdapter() {
//        if (banners.size > 0) {
        //  bannerAdapter = PromoBannerAdapter(this, bannersList, this)
        val adapterBanner = TruckImageAdapter(this, truckImages)
        addTruckBinding.vpBanner.adapter = adapterBanner
        //addTruckBinding.pagerIndicator.setViewPager(contentBinding.vpBanner);
        // addTruckBinding.pagerIndicator.visibility = View.VISIBLE
        addTruckBinding.vpBanner.offscreenPageLimit = truckImages!!.size
        addTruckBinding.vpBanner.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state : Int) {
                // enableDisableSwipeRefresh(state == ViewPager.SCROLL_STATE_IDLE)
            }

            override fun onPageScrolled(
                position : Int,
                positionOffset : Float,
                positionOffsetPixels : Int
            ) {
                // stopStartRunnable(false)
            }

            override fun onPageSelected(position : Int) {

            }
        })

    }

}
