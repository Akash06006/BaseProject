package com.example.driver.views

import android.content.Context
import android.content.Intent
import androidx.databinding.DataBindingUtil
import com.example.driver.R
import com.example.driver.application.MyApplication
import com.example.driver.constants.GlobalConstants
import com.example.driver.databinding.ActivitySplashBinding
import com.example.driver.sharedpreference.SharedPrefClass
import com.example.driver.utils.BaseActivity
import com.example.driver.views.tutorials.TutorialActivity
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.HttpMethod
import com.facebook.login.LoginManager
import java.util.*

class SplashActivity : BaseActivity() {
    private var mActivitySplashBinding : ActivitySplashBinding? = null
    private var sharedPrefClass : SharedPrefClass? = null
    private var mContext : Context? = null

    override fun getLayoutId() : Int {
        return R.layout.activity_splash
    }

    override fun initViews() {
        mContext = this
        mActivitySplashBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash)


        if (AccessToken.getCurrentAccessToken() != null) {
            GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/permissions/",
                null,
                HttpMethod.DELETE,
                GraphRequest.Callback {
                    AccessToken.setCurrentAccessToken(null)
                    LoginManager.getInstance().logOut()
                    //finish()
                }).executeAsync()
        }

        sharedPrefClass = SharedPrefClass()
        val token : String? = "sd"

        if (token != null) {
            sharedPrefClass!!.putObject(
                applicationContext,
                GlobalConstants.NOTIFICATION_TOKEN,
                token
            )
        }

        Timer().schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    checkScreenType()
                }
            }
        }, 3000)
    }

    private fun checkScreenType() {
        var login = ""
        if (checkObjectNull(
                SharedPrefClass().getPrefValue(
                    MyApplication.instance,
                    "isLogin"
                )
            )
        )
            login = sharedPrefClass!!.getPrefValue(this, "isLogin").toString()
        val intent = if (login == "true") {
            // Intent(this, LandingActivty::class.java)
            Intent(this, TutorialActivity::class.java)
        } else {
            // Intent(this, LoginActivity::class.java)
            Intent(this, TutorialActivity::class.java)

        }

        startActivity(intent)
        finish()
    }

}
