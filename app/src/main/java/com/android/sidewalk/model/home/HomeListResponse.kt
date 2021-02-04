package com.android.sidewalk.model.home

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class HomeListResponse {
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
        @SerializedName("vendorData")
        @Expose
        var vendorData : VendorData? = null

        @SerializedName("popularData")
        @Expose
        var popularData : ArrayList<PopularData>? = null

        @SerializedName("events")
        @Expose
        var events : ArrayList<Events>? = null
    }

    class VendorData {
        @SerializedName("image")
        @Expose
        var image : String? = null

        @SerializedName("cover")
        @Expose
        var cover : String? = null

    }

    class Events {
        @SerializedName("image")
        @Expose
        var image : String? = null

        @SerializedName("id")
        @Expose
        var id : String? = null

        @SerializedName("vendorId")
        @Expose
        var vendorId : String? = null

        @SerializedName("customerId")
        @Expose
        var customerId : String? = null

        @SerializedName("eventName")
        @Expose
        var eventName : String? = null

        @SerializedName("location")
        @Expose
        var location : String? = null

        @SerializedName("customerName")
        @Expose
        var customerName : String? = null
    }

    class PopularData {
        @SerializedName("truckImages")
        @Expose
        var truckImages : String? = null

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

    }
}


