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
import com.android.sidewalk.model.menu.ItemListResponse
import com.android.sidewalk.views.menu.ProductListActivity
import com.bumptech.glide.Glide

class ItemsListAdapter(
    context : ProductListActivity?,
    addressList : ArrayList<ItemListResponse.Data>?,
    var activity : Context
) :
    RecyclerView.Adapter<ItemsListAdapter.ViewHolder>() {
    private val categoryListActivity : ProductListActivity?
    private var viewHolder : ViewHolder? = null
    private var itemsList : ArrayList<ItemListResponse.Data>?

    init {
        this.categoryListActivity = context
        this.itemsList = addressList
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent : ViewGroup, viewType : Int) : ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.product_item,
            parent,
            false
        ) as ProductItemBinding
        return ViewHolder(binding.root, viewType, binding, itemsList)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(@NonNull holder : ViewHolder, position : Int) {
        viewHolder = holder
        Glide.with(categoryListActivity!!)
            .load(itemsList!![position].image)
            .placeholder(
                R.drawable.ic_add_img
            )
            .into(holder.binding!!.imgCategory)
        holder.binding.txtCatName.setText(itemsList!![position].name)
        holder.binding.txtPrice.setText(GlobalConstants.CURRENCY + itemsList!![position].price)
        holder.binding.txtDescription.setText(itemsList!![position].description)
        if (itemsList!![position].itemType.equals("nonveg")) {
            holder.binding.imgVegNonVeg.setImageDrawable(
                categoryListActivity.resources.getDrawable(
                    R.drawable.ic_nonveg
                )
            )
            holder.binding.txtVegNonVeg.setText(categoryListActivity.resources.getString(R.string.non_veg))
        } else {
            holder.binding.imgVegNonVeg.setImageDrawable(
                categoryListActivity.resources.getDrawable(
                    R.drawable.ic_veg
                )
            )
            holder.binding.txtVegNonVeg.setText(categoryListActivity.resources.getString(R.string.veg))

        }
        holder.binding!!.imgEdit.setOnClickListener {
            categoryListActivity.callEditItemActivity(itemsList!![position].id)
        }
    }

    override fun getItemCount() : Int {
        return itemsList!!.count()
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