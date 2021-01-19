package com.android.sidewalk.model.menu

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class ItemDetailResponse {
    @SerializedName("body")
    @Expose
    var data : Data? = null
    @SerializedName("message")
    var message : String? = null
    var code : Int? = null

    class Data {
        @SerializedName("name")
        @Expose
        var name : String? = null
        @SerializedName("price")
        @Expose
        var price : String? = null
        @SerializedName("description")
        @Expose
        var description : String? = null
        @SerializedName("itemType")
        @Expose
        var itemType : String? = null
        @SerializedName("image")
        @Expose
        var image : String? = null
        @SerializedName("id")
        @Expose
        var id : String? = null

    }
}
