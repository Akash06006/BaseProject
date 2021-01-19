package com.uniongoods.adapters

import android.annotation.SuppressLint
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
import com.android.sidewalk.databinding.EventItemBinding
import com.android.sidewalk.databinding.TruckItemBinding
import com.android.sidewalk.model.events.EventListResponse
import com.android.sidewalk.model.truck.TruckListResponse
import com.android.sidewalk.views.events.EventsListFragment
import com.android.sidewalk.views.trucks.TruckDetailActivity
import com.bumptech.glide.Glide

class EventListAdapter(
    context : EventsListFragment?,
    addimageContext : EventsListFragment?,
    addressList : ArrayList<EventListResponse.Data>,
    var activity : Context
) :
    RecyclerView.Adapter<EventListAdapter.ViewHolder>() {
    private val addReviewContext : EventsListFragment?
    private val addImageContext : EventsListFragment?
    private var viewHolder : ViewHolder? = null
    private var addressList : ArrayList<EventListResponse.Data>

    init {
        this.addReviewContext = context
        this.addImageContext = addimageContext
        this.addressList = addressList
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent : ViewGroup, viewType : Int) : ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.event_item,
            parent,
            false
        ) as EventItemBinding
        return ViewHolder(binding.root, viewType, binding, addressList)
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(@NonNull holder : ViewHolder, position : Int) {
        viewHolder = holder
        //  if (addImageContext != null) {
        holder.binding!!.txtName.setText(addressList[position].eventName)
        holder.binding.txtAddress.setText(addressList[position].location)
        holder.binding.txtTime.setText(addImageContext!!.resources.getString(R.string.time) + " " + addressList[position].time)

        holder.binding!!.topLay.setOnClickListener {
            //TODO--Event Detail
        }
        //}

    }

    override fun getItemCount() : Int {
        return addressList.count()

    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v : View, val viewType : Int, //These are the general elements in the RecyclerView
        val binding : EventItemBinding?,
        addressList : ArrayList<EventListResponse.Data>
    ) : RecyclerView.ViewHolder(v) {
        /*init {
            binding.linAddress.setOnClickListener {
                mContext.deleteAddress(adapterPosition)
            }
        }*/
    }
}