package com.android.sidewalk.model.menu

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class CategoryListsResponse {
    @SerializedName("body")
    @Expose
    var data : ArrayList<Data>? = null
    @SerializedName("message")
    var message : String? = null
    var code : Int? = null

    class Data {
        @SerializedName("image")
        @Expose
        var image : String? = null
        @SerializedName("id")
        @Expose
        var id : String? = null
        @SerializedName("name")
        @Expose
        var name : String? = null
        @SerializedName("vendorId")
        @Expose
        var vendorId : String? = null
    }

    class VehicleData {
        @SerializedName("id")
        @Expose
        var id : String? = null
        @SerializedName("name")
        @Expose
        var name : String? = null
        @SerializedName("image")
        @Expose
        var image : String? = null
        @SerializedName("selected")
        @Expose
        var selected : String? = null

    }

    class WeightData {
        @SerializedName("id")
        @Expose
        var id : String? = null
        @SerializedName("name")
        @Expose
        var name : String? = null
        @SerializedName("selected")
        @Expose
        var selected : String? = null
    }

    class DeliveryOptionData {
        @SerializedName("id")
        @Expose
        var id : String? = null
        @SerializedName("name")
        @Expose
        var name : String? = null

    }

}
