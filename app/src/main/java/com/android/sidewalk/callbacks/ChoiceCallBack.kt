package com.android.sidewalk.callbacks

interface ChoiceCallBack {
    fun photoFromCamera(mKey:String)
    fun photoFromGallery(mKey:String)
    fun videoFromCamera(mKey:String)
    fun videoFromGallery(mKey:String)
}