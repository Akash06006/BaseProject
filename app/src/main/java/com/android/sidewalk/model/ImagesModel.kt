package com.android.sidewalk.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ImagesModel {
    @SerializedName("name")
    @Expose
    var name : String? = null
    @SerializedName("image")
    @Expose
    var image : String? = null

    @SerializedName("id")
    @Expose
    var id : String? = null

}


