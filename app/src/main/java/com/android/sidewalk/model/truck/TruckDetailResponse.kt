package com.android.sidewalk.model.truck

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TruckDetailResponse {
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
        @SerializedName("truckImages")
        @Expose
        var truckImages : ArrayList<String>? = null
        @SerializedName("id")
        @Expose
        var id : String? = null
        @SerializedName("vendorId")
        @Expose
        var vendorId : String? = null
        @SerializedName("name")
        @Expose
        var name : String? = null
        @SerializedName("location")
        @Expose
        var location : String? = null
        @SerializedName("registrationNo")
        @Expose
        var registrationNo : String? = null
        @SerializedName("startTime")
        @Expose
        var startTime : String? = null
        @SerializedName("endTime")
        @Expose
        var endTime : String? = null
        @SerializedName("partnerName")
        @Expose
        var partnerName : String? = null
        @SerializedName("partnerNumber")
        @Expose
        var partnerNumber : String? = null
        @SerializedName("rating")
        @Expose
        var rating : String? = null
        @SerializedName("totalRatings")
        @Expose
        var totalRatings : String? = null
        @SerializedName("status")
        @Expose
        var status : String? = null
        @SerializedName("galleries")
        @Expose
        var galleries : ArrayList<Galleries>? = null

    }

    class Galleries {
        @SerializedName("image")
        @Expose
        var image : String? = null
    }

}


