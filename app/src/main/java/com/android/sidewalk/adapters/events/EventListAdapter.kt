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
import com.android.sidewalk.model.events.EventDetailResponse
import com.android.sidewalk.model.events.EventListResponse
import com.android.sidewalk.model.truck.TruckListResponse
import com.android.sidewalk.utils.Utils
import com.android.sidewalk.views.events.EventDetailActivity
import com.android.sidewalk.views.events.EventsListFragment
import com.android.sidewalk.views.trucks.TruckDetailActivity
import com.bumptech.glide.Glide

class EventListAdapter(
    context : EventsListFragment?,
    eventType : String?,
    addressList : ArrayList<EventListResponse.Data>,
    var activity : Context
) :
    RecyclerView.Adapter<EventListAdapter.ViewHolder>() {
    private val addReviewContext : EventsListFragment?
    private val eventType : String?
    private var viewHolder : ViewHolder? = null
    private var addressList : ArrayList<EventListResponse.Data>

    init {
        this.addReviewContext = context
        this.eventType = eventType
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
        holder.binding.txtTime.setText(activity.resources.getString(R.string.time) + " " + addressList[position].time)
        val date = Utils(activity).getDate(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            addressList[position].startDate,
            "dd MMM yy"
        )
        val newDate = date.split(" ")
        holder.binding.txtDay.setText(newDate[0])
        holder.binding.txtMonthYear.setText(newDate[1] + " " + newDate[2])

        if (position % 2 == 0) {
            holder.binding.llDate.setBackgroundColor(activity.resources.getColor(R.color.colorPrice))
        } else {
            holder.binding.llDate.setBackgroundColor(activity.resources.getColor(R.color.colorPrimary))
        }
        holder.binding!!.topLay.setOnClickListener {
            val intent = Intent(
                activity,
                EventDetailActivity::class.java
            )
            intent.putExtra("id", addressList[position].id/*categoriesList[position].id*/)
            intent.putExtra("eventType", eventType/*categoriesList[position].id*/)

            activity.startActivity(intent)
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