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
import com.android.sidewalk.constants.GlobalConstants
import com.android.sidewalk.databinding.ProductItemBinding
import com.android.sidewalk.model.menu.CategoryListsResponse
import com.android.sidewalk.model.menu.ItemListResponse
import com.android.sidewalk.views.menu.CategoryListActivity
import com.android.sidewalk.views.menu.ProductListActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.product_item.view.*

class ItemsListAdapter(
    context : ProductListActivity?,
    addressList : ArrayList<ItemListResponse.Data>?,
    var activity : Context
) :
    RecyclerView.Adapter<ItemsListAdapter.ViewHolder>() {
    private val categoryListActivity : ProductListActivity?
    private var viewHolder : ViewHolder? = null
    private var addressList : ArrayList<ItemListResponse.Data>?

    init {
        this.categoryListActivity = context
        this.addressList = addressList
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent : ViewGroup, viewType : Int) : ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.product_item,
            parent,
            false
        ) as ProductItemBinding
        return ViewHolder(binding.root, viewType, binding, addressList)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(@NonNull holder : ViewHolder, position : Int) {
        viewHolder = holder
        Glide.with(categoryListActivity!!)
            .load(addressList!![position].image)
            //.apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
            .placeholder(
                R.drawable.ic_add_img
            )
            .into(holder.binding!!.imgCategory)
        holder.binding.txtCatName.setText(addressList!![position].name)
        holder.binding.txtPrice.setText(GlobalConstants.CURRENCY +addressList!![position].price)
        holder.binding.txtDescription.setText(addressList!![position].description)
        /* if (addressList!![position].itemType.equals("nonveg")) {
             val resId = categoryListActivity.resources.getDrawable(
                 R.drawable.ic_nonveg
             )
             holder.binding.imgVegNonVeg.setImageResource(resId)
             *//* holder.binding.imgVegNonVeg.imageResource =
                 categoryListActivity.resources.getDrawable(
                     R.drawable.ic_nonveg
                 )*//*

        } else {
            *//*holder.binding.imgVegNonVeg.imageResource =
                categoryListActivity.resources.getDrawable(
                    R.drawable.ic_veg
                )*//*
            val resId = categoryListActivity.resources.getDrawable(
                R.drawable.ic_veg
            )
            holder.binding.imgVegNonVeg.setImageResource(resId)

        }*/
        /*holder.binding!!.imgCategory.setOnClickListener {
            categoryListActivity.callGalleryActivity()
        }*/

    }

    override fun getItemCount() : Int {
        return addressList!!.count()
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v : View, val viewType : Int, //These are the general elements in the RecyclerView
        val binding : ProductItemBinding?,
        addressList : ArrayList<ItemListResponse.Data>?
    ) : RecyclerView.ViewHolder(v) {
        /*init {
            binding.linAddress.setOnClickListener {
                mContext.deleteAddress(adapterPosition)
            }
        }*/
    }
}