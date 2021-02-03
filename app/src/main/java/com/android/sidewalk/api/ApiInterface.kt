package com.android.sidewalk.api

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import java.util.*

interface ApiInterface {
    @get:GET("user-profile-detail/")
    val profileData : Call<JsonObject>

    @Multipart
    @POST("mobile/auth/update")
    fun callUpdateProfile(
        @PartMap mHashMap : HashMap<String,
                RequestBody>, @Part image : MultipartBody.Part?
    ) : Call<JsonObject>

    @Multipart
    @POST("vendor/menu/addCategory")
    fun addCategory(
        @PartMap mHashMap : HashMap<String,
                RequestBody>, @Part image : MultipartBody.Part?
    ) : Call<JsonObject>

    @Multipart
    @POST("vendor/menu/addItem")
    fun addItem(
        @PartMap mHashMap : HashMap<String,
                RequestBody>, @Part image : MultipartBody.Part?
    ) : Call<JsonObject>

    @GET("vendor/auth/getProfile")
    fun getProfile() : Call<JsonObject>

    @GET("mobile/auth/getRegion")
    fun getRegions() : Call<JsonObject>

    @GET("mobile/vehicle/getList")
    fun getLists() : Call<JsonObject>

    @GET("vendor/menu/listCategory")
    fun getCategoryLists() : Call<JsonObject>

    @GET("vendor/menu/listItem/{categoryId}")
    fun getItemsList(@Path("categoryId") id : String) : Call<JsonObject>

    @GET("vendor/menu/itemDetail/{itemId}")
    fun itemDetail(@Path("itemId") id : String) : Call<JsonObject>

    //itemId
    @POST("vendor/auth/login")
    fun callLogin(@Body jsonObject : JsonObject) : Call<JsonObject>

    @Multipart
    @POST("vendor/auth/signup")
    fun callSignup(
        @PartMap jsonObject : HashMap<String, RequestBody>, @Part userImage : MultipartBody.Part?,
        @Part licenseFront : MultipartBody.Part?,
        @Part licenseBack : MultipartBody.Part?
    ) : Call<JsonObject>

    @Multipart
    @POST("vendor/auth/updateProfile")
    fun callUpdateProfile(
        @PartMap jsonObject : HashMap<String, RequestBody>, @Part userImage : MultipartBody.Part?,
        @Part licenseFront : MultipartBody.Part?,
        @Part licenseBack : MultipartBody.Part?
    ) : Call<JsonObject>

    @POST("vendor/auth/verify")
    fun callVerifyUser(@Body jsonObject : JsonObject) : Call<JsonObject>

    @POST("mobile/auth/userByPhonenumber")
    fun callForgotPassword(@Body mJsonObject : JsonObject) : Call<JsonObject>//(@Query("countryCode") countryCode : String, @Query("phoneNumber") phoneNumber : String) : Call<JsonObject>

    @POST("verify-otp/")
    fun otpVerify(@Body mJsonObject : JsonObject) : Call<JsonObject>

    @POST("mobile/auth/forgotPassword")
    fun resetPassword(@Body mJsonObject : JsonObject) : Call<JsonObject>

    @PUT("change-password/")
    fun changePassword(@Body mJsonObject : JsonObject) : Call<JsonObject>

    @GET("vendor/auth/logout")
    fun callLogout() : Call<JsonObject>

    @GET("vendor/others/home")
    fun getHomeList() : Call<JsonObject>

    @GET("vendor/truck/list")
    fun truckList() : Call<JsonObject>

    @GET("vendor/event/list")
    fun eventsList(@Query("status") id : String) : Call<JsonObject>

    @GET("vendor/truck/detail/{id}")
    fun truckDetail(@Path("id") id : String) : Call<JsonObject>

    @GET("/api/vendor/event/detail/{eventId}")
    fun eventDetail(@Path("eventId") id : String) : Call<JsonObject>

    @GET("vendor/truck/viewGallery")
    fun viewGallery(@Query("truckId") id : String) : Call<JsonObject>

    @Multipart
    @POST("vendor/auth/uploadCover")
    fun addBannerImage(
        @Part userImage : MultipartBody.Part?
    ) : Call<JsonObject>

    @Multipart
    @POST("vendor/truck/addTruck")
    fun addUpdateTruck(
        @Part imagesParts : Array<MultipartBody.Part?>?,
        @PartMap mHashMap : HashMap<String, RequestBody>?
    ) : Call<JsonObject>

    @Multipart
    @POST("vendor/others/addGallery")
    fun addGalleryImage(
        @Part userImage : MultipartBody.Part?
    ) : Call<JsonObject>

    @GET("outlet-services/")
    fun getHomeList(
        @Query("page") page : String,
        @Query("limit") limit : String,
        @Query("companyId") companyId : String
    ) : Call<JsonObject>

    @POST("outlet-group-services/")
    fun getClassesList(@Body mJsonObject : JsonObject) : Call<JsonObject>

    @POST("outlet-therapy-services/")
    fun getTherpyList(@Body mJsonObject : JsonObject) : Call<JsonObject>

    @POST("schedule-services-by-date/")
    fun getSlotByDate(@Body mJsonObject : JsonObject) : Call<JsonObject>

    @POST("up-coming-bookings/")
    fun getUpcomingBookings(@Body mJsonObject : JsonObject) : Call<JsonObject>

    @POST("service-booking-history/")
    fun getBookingHistory(@Body mJsonObject : JsonObject) : Call<JsonObject>

    @POST("service-booking-cancel-list/")
    fun getCancelledHistory(@Body mJsonObject : JsonObject) : Call<JsonObject>

    @POST("update-user-setting/")
    fun updateUserSetting(@Body mJsonObject : JsonObject) : Call<JsonObject>

    @GET("booking-rule-list/")
    fun bookingRules() : Call<JsonObject>

    @GET("booking-detail/{slot_id}/")
    fun bookingDetail(@Path("slot_id") slotId : String) : Call<JsonObject>

    @PUT("cancel-schedule-service/{id}/")
    fun cancelSrviceSlot(@Path("id") id : String) : Call<JsonObject>

    @POST("book-schedule-service/")
    fun bookSrviceSlot(@Body mJsonObject : JsonObject) : Call<JsonObject>

    @GET("employee-detail/{id}/")
    fun getEmployeeDetail(@Path("id") id : String) : Call<JsonObject>

    @POST("book-for-guest/")
    fun bookForGuest(@Body mJsonObject : JsonObject) : Call<JsonObject>

    @POST("outlet-group-services-by-date/")
    fun getClassesListByDate(@Body mJsonObject : JsonObject) : Call<JsonObject>

    @Multipart
    @POST("vendor/auth/checkSocial")
    fun checkSocial(@PartMap mHashMap : HashMap<String, RequestBody>) : Call<JsonObject>
}