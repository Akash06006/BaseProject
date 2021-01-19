package com.android.sidewalk.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CommonModel {
    @SerializedName("code")
    @Expose
    var code : Int = 0
    @SerializedName("message")
    @Expose
    var message : String? = null
    @SerializedName("categoryList")
    @Expose
    var data : Any? = null

}


