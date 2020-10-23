package com.example.driver.api

/*
 Created by Saira on 03/07/2019.
*/


import android.content.Intent
import androidx.annotation.NonNull
import android.text.TextUtils
import com.example.driver.application.MyApplication
import com.example.driver.common.UtilsFunctions
import com.example.driver.constants.GlobalConstants
import com.example.driver.sharedpreference.SharedPrefClass
import com.example.driver.views.authentication.LoginActivity
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

object ApiClient {

    @JvmStatic
    private val BASE_URL = GlobalConstants.BASE_URL
    private val sharedPrefClass = SharedPrefClass()
    @JvmStatic
    private var mApiInterface: ApiInterface? = null


    @JvmStatic
    fun getApiInterface(): ApiInterface {
        return setApiInterface()
    }


    @JvmStatic
    private fun setApiInterface(): ApiInterface {
        val lang = "en"

        var mAuthToken = GlobalConstants.SESSION_TOKEN


            if (mAuthToken == "session_token" && UtilsFunctions.checkObjectNull(
                    SharedPrefClass().getPrefValue(
                        MyApplication.instance.applicationContext,
                        GlobalConstants.ACCESS_TOKEN
                    )
                )
            )
            {

                mAuthToken = sharedPrefClass.getPrefValue(
                    MyApplication.instance,
                    GlobalConstants.ACCESS_TOKEN
                ).toString()
        }



        val httpClient = OkHttpClient.Builder()
            //.connectTimeout(1, TimeUnit.MINUTES)
           // .readTimeout(1, TimeUnit.MINUTES)
           // .writeTimeout(1, TimeUnit.MINUTES)

        val mBuilder = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())


        if (!TextUtils.isEmpty(mAuthToken)) {



            if(mAuthToken.length>20)
            {
                mAuthToken= "Bearer $mAuthToken"
            }


            val finalMAuthToken = mAuthToken





            val interceptor: Interceptor = object : Interceptor {
                @Throws(IOException::class)
                override fun intercept(@NonNull chain: Interceptor.Chain): Response {
                    val original = chain.request()
                    val builder = original.newBuilder()
                        .header("Authorization", finalMAuthToken)
                        .header("lang", lang)
                    val request = builder.build()
                    val response = chain.proceed(request)
                    return if (response.code() == 401) {
                        val i = Intent(MyApplication.instance.applicationContext, LoginActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        MyApplication.instance.applicationContext.startActivity(i)
                        response
                    }

                    else response
                }
            }



            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(interceptor)
                mBuilder.client(httpClient.build())
                mApiInterface = mBuilder.build().create(ApiInterface::class.java)
            }
        } else
            mApiInterface = mBuilder.build().create(ApiInterface::class.java)

        return mApiInterface!!
    }


}
