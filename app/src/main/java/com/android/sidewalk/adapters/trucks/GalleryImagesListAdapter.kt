package com.uniongoods.adapters

import android.content.Context
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
import com.android.sidewalk.model.ImagesModel
import com.android.sidewalk.views.trucks.AddTruckActivity
import com.bumptech.glide.Glide

class GalleryImagesListAdapter(
    context : AddTruckActivity?,
    addimageContext : AddTruckActivity?,
    addressList : ArrayList<ImagesModel>,
    var activity : Context
) :
    RecyclerView.Adapter<GalleryImagesListAdapter.ViewHolder>() {
    private val addReviewContext : AddTruckActivity?
    private val addImageContext : AddTruckActivity?
    private var viewHolder : ViewHolder? = null
    private var addressList : ArrayList<ImagesModel>

    init {
        this.addReviewContext = context
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
        //  if (addImageContext != null) {
        Glide.with(addImageContext!!)
            .load(addressList[position].image)
            //.apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
            .placeholder(
                R.drawable.ic_add_img
            )
            .into(holder.binding!!.imgReview)
        holder.binding!!.imgCross.setOnClickListener {
            addImageContext.removeGalleryImage(
                position,
                addressList[position].image!!/*, holder.binding!!.imgCart.getText().toString()*/
            )
        }
        //}

    }

    override fun getItemCount() : Int {
        return addressList.count()

    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v : View, val viewType : Int, //These are the general elements in the RecyclerView
        val binding : ImageItemBinding?,
        addressList : ArrayList<ImagesModel>
    ) : RecyclerView.ViewHolder(v) {
        /*init {
            binding.linAddress.setOnClickListener {
                mContext.deleteAddress(adapterPosition)
            }
        }*/
    }
}