package com.android.sidewalk.model.events

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class EventListResponse {
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
        @SerializedName("id")
        @Expose
        var id : String? = null
        @SerializedName("customerId")
        @Expose
        var customerId : String? = null
        @SerializedName("vendorId")
        @Expose
        var vendorId : String? = null
        @SerializedName("eventName")
        @Expose
        var eventName : String? = null
        @SerializedName("location")
        @Expose
        var location : String? = null
        @SerializedName("customerName")
        @Expose
        var customerName : String? = null
        @SerializedName("customerNumber")
        @Expose
        var customerNumber : String? = null
        @SerializedName("startDate")
        @Expose
        var startDate : String? = null
        @SerializedName("time")
        @Expose
        var time : String? = null
        @SerializedName("partySize")
        @Expose
        var partySize : String? = null
        @SerializedName("meat")
        @Expose
        var meat : String? = null
        @SerializedName("payment")
        @Expose
        var payment : String? = null
        @SerializedName("additionalInfo")
        @Expose
        var additionalInfo : String? = null
        @SerializedName("additionalCustReq")
        @Expose
        var additionalCustReq : String? = null
        @SerializedName("status")
        @Expose
        var status : String? = null
        @SerializedName("createdAt")
        @Expose
        var createdAt : String? = null
        @SerializedName("updatedAt")
        @Expose
        var updatedAt : String? = null

    }
}


