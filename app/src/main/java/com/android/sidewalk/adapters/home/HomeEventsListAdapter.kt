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
import com.android.sidewalk.databinding.HomeEventItemBinding
import com.android.sidewalk.databinding.HomeTruckItemBinding
import com.android.sidewalk.databinding.ImageItemBinding
import com.android.sidewalk.model.ImagesModel
import com.android.sidewalk.model.home.HomeListResponse
import com.android.sidewalk.views.events.EventDetailActivity
import com.android.sidewalk.views.home.fragments.HomeFragment
import com.android.sidewalk.views.trucks.AddTruckActivity
import com.android.sidewalk.views.trucks.GalleryActivity
import com.android.sidewalk.views.trucks.TruckDetailActivity
import com.bumptech.glide.Glide

class HomeEventsListAdapter(
    context : HomeFragment?,
    addressList : ArrayList<HomeListResponse.Events>,
    var activity : Context
) :
    RecyclerView.Adapter<HomeEventsListAdapter.ViewHolder>() {
    private val truckDetailActivity : HomeFragment?
    private var viewHolder : ViewHolder? = null
    private var addressList : ArrayList<HomeListResponse.Events>

    init {
        this.truckDetailActivity = context
        this.addressList = addressList
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent : ViewGroup, viewType : Int) : ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.home_event_item,
            parent,
            false
        ) as HomeEventItemBinding
        return ViewHolder(binding.root, viewType, binding, addressList)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(@NonNull holder : ViewHolder, position : Int) {
        viewHolder = holder
        Glide.with(activity)
            .load(addressList[position].image)
            //.apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
            .placeholder(
                R.drawable.ic_add_img
            )
            .into(holder.binding!!.imgReview)
        holder.binding.txtName.setText(addressList[position].eventName)
        holder.binding.rlTop.setOnClickListener {
            val intent = Intent(
                activity,
                EventDetailActivity::class.java
            )
            intent.putExtra("eventType", "accept")
            intent.putExtra("id", addressList[position].id)

            activity.startActivity(intent)
        }

    }

    override fun getItemCount() : Int {
        return addressList.count()
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v : View, val viewType : Int, //These are the general elements in the RecyclerView
        val binding : HomeEventItemBinding?,
        addressList : ArrayList<HomeListResponse.Events>
    ) : RecyclerView.ViewHolder(v) {
        /*init {
            binding.linAddress.setOnClickListener {
                mContext.deleteAddress(adapterPosition)
            }
        }*/
    }
}