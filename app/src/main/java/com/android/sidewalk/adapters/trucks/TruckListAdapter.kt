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
import com.android.sidewalk.databinding.TruckItemBinding
import com.android.sidewalk.model.truck.TruckListResponse
import com.android.sidewalk.views.authentication.SignupActivity
import com.android.sidewalk.views.trucks.AddTruckActivity
import com.android.sidewalk.views.trucks.TruckDetailActivity
import com.android.sidewalk.views.trucks.TruckListFragment
import com.bumptech.glide.Glide

class TruckListAdapter(
    context : TruckListFragment?,
    addimageContext : TruckListFragment?,
    addressList : ArrayList<TruckListResponse.Data>,
    var activity : Context
) :
    RecyclerView.Adapter<TruckListAdapter.ViewHolder>() {
    private val addReviewContext : TruckListFragment?
    private val addImageContext : TruckListFragment?
    private var viewHolder : ViewHolder? = null
    private var addressList : ArrayList<TruckListResponse.Data>

    init {
        this.addReviewContext = context
        this.addImageContext = addimageContext
        this.addressList = addressList
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent : ViewGroup, viewType : Int) : ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.truck_item,
            parent,
            false
        ) as TruckItemBinding
        return ViewHolder(binding.root, viewType, binding, addressList)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(@NonNull holder : ViewHolder, position : Int) {
        viewHolder = holder
        //  if (addImageContext != null) {
        Glide.with(addImageContext!!)
            .load(addressList[position].truckImages)
            .placeholder(
                R.drawable.ic_truck
            )
            .into(holder.binding!!.imgTruck)
        holder.binding!!.txtName.setText(addressList[position].name)
        holder.binding.rBar.setRating(3.5f)
        holder.binding.txtCount.setText("(" + 10 + ")")
        holder.binding.txtAddress.setText(addressList[position].location)
        holder.binding.txtTime.setText(addImageContext.resources.getString(R.string.time) + " " + addressList[position].startTime + " - " + addressList[position].endTime)

        if (addressList[position].status.equals("1")) {
            holder.binding.chkTruckOn.isChecked = true
            // holder.binding.view.visibility = View.GONE
            holder.binding!!.topLay.alpha = 1f
        } else {
            holder.binding.chkTruckOn.isChecked = false
            //holder.binding.view.visibility = View.VISIBLE
            holder.binding!!.topLay.alpha = 0.5f
        }
        holder.binding.chkTruckOn.setOnCheckedChangeListener({ buttonView, isChecked->
            /*if (isChecked) {
                // show toast , check box is checked
            } else {
                // show toast , check box is not checked
            }*/
            addReviewContext?.changeTruckStatus(position, isChecked)
        })

        holder.binding!!.topLay.setOnClickListener {
            val intent = Intent(
                activity,
                TruckDetailActivity::class.java
            )
            intent.putExtra("id", addressList[position].id/*categoriesList[position].id*/)

            activity.startActivity(intent)
        }

        holder.binding!!.imgEdit.setOnClickListener {
            val intent = Intent(
                activity,
                AddTruckActivity::class.java
            )
            intent.putExtra("id", addressList[position].id)

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
        val binding : TruckItemBinding?,
        addressList : ArrayList<TruckListResponse.Data>
    ) : RecyclerView.ViewHolder(v) {
        /*init {
            binding.linAddress.setOnClickListener {
                mContext.deleteAddress(adapterPosition)
            }
        }*/
    }
}