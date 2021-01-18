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
import com.android.sidewalk.databinding.CategoryItemBinding
import com.android.sidewalk.model.menu.CategoryListsResponse
import com.android.sidewalk.views.menu.CategoryListActivity
import com.android.sidewalk.views.menu.ProductListActivity
import com.android.sidewalk.views.trucks.GalleryActivity
import com.bumptech.glide.Glide

class CategoryListAdapter(
    context : CategoryListActivity?,
    addressList : ArrayList<CategoryListsResponse.Data>?,
    var activity : Context
) :
    RecyclerView.Adapter<CategoryListAdapter.ViewHolder>() {
    private val categoryListActivity : CategoryListActivity?
    private var viewHolder : ViewHolder? = null
    private var addressList : ArrayList<CategoryListsResponse.Data>?

    init {
        this.categoryListActivity = context
        this.addressList = addressList
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent : ViewGroup, viewType : Int) : ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.category_item,
            parent,
            false
        ) as CategoryItemBinding
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
        holder.binding!!.imgCategory.setOnClickListener {
            categoryListActivity.callGalleryActivity()
        }

        holder.binding!!.imgCategory.setOnClickListener {
            val intent = Intent(
                categoryListActivity,
                ProductListActivity::class.java
            )
            intent.putExtra("id", addressList!![position].id)
            intent.putExtra("name", addressList!![position].name)
            categoryListActivity.startActivity(intent)
        }

    }

    override fun getItemCount() : Int {
        return addressList!!.count()
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v : View, val viewType : Int, //These are the general elements in the RecyclerView
        val binding : CategoryItemBinding?,
        addressList : ArrayList<CategoryListsResponse.Data>?
    ) : RecyclerView.ViewHolder(v) {
        /*init {
            binding.linAddress.setOnClickListener {
                mContext.deleteAddress(adapterPosition)
            }
        }*/
    }
}