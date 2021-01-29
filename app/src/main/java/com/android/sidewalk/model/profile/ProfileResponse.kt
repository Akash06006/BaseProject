package com.android.sidewalk.model.profile

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class ProfileResponse {
    @SerializedName("body")
    @Expose
    var data : Data? = null

    @SerializedName("message")
    var message : String? = null
    var code : Int? = null

    class Data {
        @SerializedName("sideWalkId")
        @Expose
        var sideWalkId : String? = null

        @SerializedName("image")
        @Expose
        var image : String? = null

        @SerializedName("cover")
        @Expose
        var cover : String? = null

        @SerializedName("licenseFront")
        @Expose
        var licenseFront : String? = null

        @SerializedName("licenseBack")
        @Expose
        var licenseBack : String? = null

        @SerializedName("id")
        @Expose
        var id : String? = null

        @SerializedName("firstName")
        @Expose
        var firstName : String? = null

        @SerializedName("lastName")
        @Expose
        var lastName : String? = null

        @SerializedName("email")
        @Expose
        var email : String? = null

        @SerializedName("phoneNumber")
        @Expose
        var phoneNumber : String? = null

        @SerializedName("countryCode")
        @Expose
        var countryCode : String? = null

        @SerializedName("password")
        @Expose
        var password : String? = null

        @SerializedName("isSocial")
        @Expose
        var isSocial : String? = null

        @SerializedName("socialType")
        @Expose
        var socialType : String? = null

        @SerializedName("socialId")
        @Expose
        var socialId : String? = null

        @SerializedName("experience")
        @Expose
        var experience : String? = null

        @SerializedName("trialOver")
        @Expose
        var trialOver : String? = null

        @SerializedName("licenseFApproved")
        @Expose
        var licenseFApproved : String? = null

        @SerializedName("licenseBApproved")
        @Expose
        var licenseBApproved : String? = null

        @SerializedName("deviceToken")
        @Expose
        var deviceToken : String? = null

        @SerializedName("sessionToken")
        @Expose
        var sessionToken : String? = null

        @SerializedName("platform")
        @Expose
        var platform : String? = null

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
