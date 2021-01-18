package com.uniongoods.adapters

import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.sidewalk.R
import com.android.sidewalk.model.truck.TruckListResponse
import com.android.sidewalk.views.authentication.SignupActivity
import com.android.sidewalk.views.trucks.AddTruckActivity
import com.android.sidewalk.views.trucks.GalleryActivity
import com.android.sidewalk.views.trucks.TruckDetailActivity
import com.android.sidewalk.views.trucks.TruckListFragment
import com.bumptech.glide.Glide

class ViewGalleryAdapter(
    context : GalleryActivity?,
    addimageContext : GalleryActivity?,
    addressList : ArrayList<String>,
    var activity : Context
) :
    BaseAdapter() {
    private var layoutInflater : LayoutInflater? = null
    private lateinit var imageView : ImageView
    private val truckDetailActivity : GalleryActivity?
    private val addImageContext : GalleryActivity?
    private var addressList : ArrayList<String>
    override fun getCount() : Int {
        return addressList.size
    }

    init {
        this.truckDetailActivity = context
        this.addImageContext = addimageContext
        this.addressList = addressList
    }

    override fun getItem(position : Int) : Any? {
        return null
    }

    override fun getItemId(position : Int) : Long {
        return 0
    }

    override fun getView(
        position : Int,
        convertView : View?,
        parent : ViewGroup
    ) : View? {
        var convertView = convertView
        if (layoutInflater == null) {
            layoutInflater =
                truckDetailActivity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }
        if (convertView == null) {
            convertView = layoutInflater!!.inflate(R.layout.gallery_image_item, null)
        }
        imageView = convertView!!.findViewById(R.id.imgGallery)
        Glide.with(truckDetailActivity!!).load(addressList[position]).into(imageView)


        imageView.setOnClickListener {
            truckDetailActivity.showImageAlert(truckDetailActivity, addressList[position])
        }
        return convertView
    }
}