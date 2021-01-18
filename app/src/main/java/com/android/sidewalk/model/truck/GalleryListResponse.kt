package com.android.sidewalk.model.truck

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GalleryListResponse {
    @SerializedName("code")
    @Expose
    var code : Int = 0
    @SerializedName("message")
    @Expose
    var message : String? = null
    @SerializedName("body")
    @Expose
    var data : ArrayList<Data>? = null

    class Data {
        @SerializedName("createdAt")
        @Expose
        var createdAt : String? = null
        @SerializedName("image")
        @Expose
        var image : String? = null

    }

}


