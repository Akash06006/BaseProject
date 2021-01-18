package com.android.sidewalk.repositories.truck

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AddGalleryModel {
    @SerializedName("code")
    @Expose
    var code : Int = 0
    @SerializedName("message")
    @Expose
    var message : String? = null
    @SerializedName("body")
    @Expose
    var data : Data? = null

    class Data {
        @SerializedName("image")
        @Expose
        var image : String? = null
    }
}


