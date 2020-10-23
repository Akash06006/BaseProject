package com.example.driver.callbacks

interface ChoiceCallBack {
    fun photoFromCamera(mKey:String)
    fun photoFromGallery(mKey:String)
    fun videoFromCamera(mKey:String)
    fun videoFromGallery(mKey:String)
}