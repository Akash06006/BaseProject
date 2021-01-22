package com.uniongoods.adapters

import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.sidewalk.R
import com.android.sidewalk.databinding.HomeTruckItemBinding
import com.android.sidewalk.databinding.ImageItemBinding
import com.android.sidewalk.model.ImagesModel
import com.android.sidewalk.model.home.HomeListResponse
import com.android.sidewalk.views.home.fragments.HomeFragment
import com.android.sidewalk.views.trucks.AddTruckActivity
import com.android.sidewalk.views.trucks.GalleryActivity
import com.android.sidewalk.views.trucks.TruckDetailActivity
import com.bumptech.glide.Glide

class HomeTrucksListAdapter(
    context : HomeFragment?,
    addressList : ArrayList<HomeListResponse.PopularData>,
    var activity : Context
) :
    RecyclerView.Adapter<HomeTrucksListAdapter.ViewHolder>() {
    private val truckDetailActivity : HomeFragment?
    private var viewHolder : ViewHolder? = null
    private var addressList : ArrayList<HomeListResponse.PopularData>

    init {
        this.truckDetailActivity = context
        this.addressList = addressList
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent : ViewGroup, viewType : Int) : ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.home_truck_item,
            parent,
            false
        ) as HomeTruckItemBinding
        return ViewHolder(binding.root, viewType, binding, addressList)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(@NonNull holder : ViewHolder, position : Int) {
        viewHolder = holder
        Glide.with(activity)
            .load(addressList[position].truckImages)
            //.apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
            .placeholder(
                R.drawable.ic_add_img
            )
            .into(holder.binding!!.imgReview)

        holder.binding.txtName.setText(addressList[position].name)
        holder.binding.rlTop.setOnClickListener {
            val intent = Intent(
                activity,
                TruckDetailActivity::class.java
            )
            intent.putExtra("id", addressList[position].id/*categoriesList[position].id*/)

            activity.startActivity(intent)
        }

    }

    override fun getItemCount() : Int {
        return addressList.count()
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v : View, val viewType : Int, //These are the general elements in the RecyclerView
        val binding : HomeTruckItemBinding?,
        addressList : ArrayList<HomeListResponse.PopularData>
    ) : RecyclerView.ViewHolder(v) {
        /*init {
            binding.linAddress.setOnClickListener {
                mContext.deleteAddress(adapterPosition)
            }
        }*/
    }
}