package com.android.sidewalk.utils

import android.content.Context
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by saira on 2018-03-27.
 */

class Utils(internal var context: Context) {

    fun createPartFromString(string: String): RequestBody {
        return RequestBody.create(
            MultipartBody.FORM, string
        )
    }

    fun prepareFilePart(partName: String, fileUri: File): MultipartBody.Part {
        val requestFile = RequestBody.create(
            MediaType.parse("image/*"),
            fileUri
        )
        return MultipartBody.Part.createFormData(partName, fileUri.name, requestFile)
    }


    fun getDate(format: String, milisec: String?,outputFormat:String?): String {
        val inputFormat = SimpleDateFormat(format, Locale.getDefault())
        val outputFormat1 = SimpleDateFormat(outputFormat, Locale.getDefault())

        val tz = TimeZone.getTimeZone("UTC")
        inputFormat.timeZone = tz
        val date = inputFormat.parse(milisec)

        val tzLocal = TimeZone.getDefault()
        outputFormat1.timeZone = tzLocal
        return  outputFormat1.format(date)


    }

    fun getDateLocal(format: String, milisec: String?,outputFormat:String?): String {
        val inputFormat = SimpleDateFormat(format, Locale.getDefault())
        val outputFormat1 = SimpleDateFormat(outputFormat, Locale.getDefault())

        //val tz = TimeZone.getTimeZone("Local")
        // inputFormat.timeZone = tz
        val date = inputFormat.parse(milisec)

        val tzLocal = TimeZone.getDefault()
        outputFormat1.timeZone = tzLocal
        return  outputFormat1.format(date)


    }


}
