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
import com.android.sidewalk.databinding.ImageItemBinding
import com.android.sidewalk.views.trucks.AddTruckActivity
import com.android.sidewalk.views.trucks.GalleryActivity
import com.android.sidewalk.views.trucks.TruckDetailActivity
import com.bumptech.glide.Glide

class ImagesListAdapter(
    context : TruckDetailActivity?,
    addimageContext : AddTruckActivity?,
    addressList : ArrayList<String>,
    var activity : Context
) :
    RecyclerView.Adapter<ImagesListAdapter.ViewHolder>() {
    private val truckDetailActivity : TruckDetailActivity?
    private val addImageContext : AddTruckActivity?
    private var viewHolder : ViewHolder? = null
    private var addressList : ArrayList<String>

    init {
        this.truckDetailActivity = context
        this.addImageContext = addimageContext
        this.addressList = addressList
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent : ViewGroup, viewType : Int) : ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.image_item,
            parent,
            false
        ) as ImageItemBinding
        return ViewHolder(binding.root, viewType, binding, addressList)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(@NonNull holder : ViewHolder, position : Int) {
        viewHolder = holder
        if (addImageContext != null) {
            holder.binding!!.imgCross.visibility = View.VISIBLE
            Glide.with(addImageContext!!)
                .load(addressList[position])
                //.apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
                .placeholder(
                    R.drawable.ic_add_img
                )
                .into(holder.binding!!.imgReview)
            holder.binding!!.imgCross.setOnClickListener {
                addImageContext.removeImage(position/*, holder.binding!!.imgCart.getText().toString()*/)
            }
        } else {
            holder.binding!!.imgCross.visibility = View.GONE
            Glide.with(truckDetailActivity!!)
                .load(addressList[position])
                //.apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
                .placeholder(
                    R.drawable.ic_add_img
                )
                .into(holder.binding!!.imgReview)
            if (position == 3 || (position + 1) == addressList.count()) {
                holder.binding!!.txtViewAll.visibility = View.VISIBLE
                holder.binding!!.txtViewAll.bringToFront()
            } else {
                holder.binding!!.txtViewAll.visibility = View.GONE
            }

            holder.binding!!.imgReview.setOnClickListener {
                truckDetailActivity.callGalleryActivity()
            }
        }

    }

    override fun getItemCount() : Int {
        if (addImageContext != null) {
            return addressList.count()
        } else {
            if (addressList.size > 4) {
                return 4
            } else {
                return addressList.count()
            }
        }

    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v : View, val viewType : Int, //These are the general elements in the RecyclerView
        val binding : ImageItemBinding?,
        addressList : ArrayList<String>
    ) : RecyclerView.ViewHolder(v) {
        /*init {
            binding.linAddress.setOnClickListener {
                mContext.deleteAddress(adapterPosition)
            }
        }*/
    }
}