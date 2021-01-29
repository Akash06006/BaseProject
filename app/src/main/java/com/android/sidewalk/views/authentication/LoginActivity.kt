package com.android.sidewalk.views.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.android.sidewalk.R
import com.android.sidewalk.application.MyApplication
import com.android.sidewalk.common.FirebaseFunctions
import com.android.sidewalk.common.UtilsFunctions
import com.android.sidewalk.constants.GlobalConstants
import com.android.sidewalk.databinding.ActivityLoginBinding
import com.android.sidewalk.model.LoginResponse
import com.android.sidewalk.sharedpreference.SharedPrefClass
import com.android.sidewalk.utils.BaseActivity
import com.android.sidewalk.utils.ValidationsClass
import com.android.sidewalk.viewmodels.LoginViewModel
import com.android.sidewalk.views.home.LandingActivty
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.gson.JsonObject
import org.json.JSONObject
import java.util.*

class LoginActivity : BaseActivity() {
    private lateinit var activityLoginbinding : ActivityLoginBinding
    private lateinit var loginViewModel : LoginViewModel
    private var callbackManager : CallbackManager? = null
    private val EMAIL = "email"
    val mOtpJsonObject = JsonObject()
    override fun getLayoutId() : Int {
        return R.layout.activity_login
    }

    override fun initViews() {
        activityLoginbinding =
            viewDataBinding as ActivityLoginBinding //DataBindingUtil.setContentView(this, R.layout.activity_login)
        loginViewModel = ViewModelProviders.of(this)
            .get(LoginViewModel::class.java)
        activityLoginbinding.loginViewModel = loginViewModel
        //  Logger.logPurchase(BigDecimal.valueOf(4.32), Currency.getInstance("USD"));
        // activityLoginbinding.tvForgotPassword.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        loginViewModel.getLoginRes().observe(this,
            Observer<LoginResponse> { loginResponse->
                stopProgressDialog()

                if (loginResponse != null) {
                    val message = loginResponse.message

                    if (loginResponse.code == 200) {
                        //TODO comment the below is login
                        SharedPrefClass()
                            .putObject(
                                MyApplication.instance,
                                "isLogin",
                                true
                            )
                        SharedPrefClass()
                            .putObject(
                                MyApplication.instance,
                                GlobalConstants.ACCESS_TOKEN,
                                loginResponse.data!!.token
                            )
                        SharedPrefClass()
                            .putObject(
                                MyApplication.instance,
                                GlobalConstants.USERID,
                                loginResponse.data!!.id
                            )
                        SharedPrefClass()
                            .putObject(
                                MyApplication.instance,
                                GlobalConstants.USER_IMAGE,
                                loginResponse.data!!.image
                            )
                        SharedPrefClass()
                            .putObject(
                                MyApplication.instance,
                                GlobalConstants.IS_SOCIAL,
                                loginResponse.data!!.isSocial
                            )
                        SharedPrefClass()
                            .putObject(
                                MyApplication.instance,
                                GlobalConstants.USERNAME,
                                loginResponse.data!!.firstName + " " + loginResponse.data!!.lastName
                            )
                        SharedPrefClass()
                            .putObject(
                                MyApplication.instance,
                                GlobalConstants.CUSTOMER_IAMGE,
                                loginResponse.data!!.image
                            )
                        GlobalConstants.VERIFICATION_TYPE = "login"
                        //TODO uncomment
                       // FirebaseFunctions.sendOTP("login", mOtpJsonObject, this)
                        showToastSuccess(message)
                        val intent = Intent(
                            this,
                            LandingActivty::class.java
                        )
                        //intent.putExtra("itemId", ""/*categoriesList[position].id*/)
                        startActivity(intent)
                        finish()

                    } else {
                        showToastError(message)
                    }

                }
            })
        /*loginViewModel.getEmailError().observe(this, Observer<String> { emailError->
            activityLoginbinding.etEmail.error = emailError
            activityLoginbinding.etEmail.requestFocus()
        })


        loginViewModel.getPasswordError().observe(this, Observer<String> { passError->
            activityLoginbinding.etPassword.requestFocus()
            activityLoginbinding.etPassword.error = passError
        })*/



        loginViewModel.isLoading().observe(this, Observer<Boolean> { aBoolean->
            if (aBoolean!!) {
                startProgressDialog()
            } else {
                stopProgressDialog()
            }
        })

        loginViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it : String?) {
                when (it) {
                    "txtSignup" -> {
                        val intent = Intent(
                            this,
                            SignupActivity::class.java
                        )
                        intent.putExtra("social", "false"/*categoriesList[position].id*/)
                        intent.putExtra("fbSocial", "false"/*categoriesList[position].id*/)
                        intent.putExtra("googleSocial", "false"/*categoriesList[position].id*/)

                        startActivity(intent)

                    }
                    "txtForgot" -> {
                        val intent = Intent(
                            this,
                            ForgotPasswrodActivity::class.java
                        )
                        //intent.putExtra("itemId", ""/*categoriesList[position].id*/)
                        startActivity(intent)

                    }
                    "btnLogin" -> {
                        val phoneNumber = activityLoginbinding.edtMobile.text.toString()
                        //val password = activityLoginbinding.edtPassword.text.toString()
                        when {
                            phoneNumber.isEmpty() -> showError(
                                activityLoginbinding.edtMobile,
                                getString(R.string.empty) + " " + getString(
                                    R.string.mob_no
                                )
                            )
                            phoneNumber.length < 10 -> showError(
                                activityLoginbinding.edtMobile,
                                getString(R.string.invalid) + " " + getString(
                                    R.string.mob_no
                                )
                            )
                            else -> {
                                mOtpJsonObject.addProperty(
                                    "countryCode",
                                    "+" + activityLoginbinding.btnCcp.selectedCountryCode
                                )
                                mOtpJsonObject.addProperty("phoneNumber", phoneNumber)
                                val mJsonObject = JsonObject()
                                mJsonObject.addProperty("phoneNumber", phoneNumber)
                                mJsonObject.addProperty(
                                    "countryCode",
                                    "+" + activityLoginbinding.btnCcp.selectedCountryCode
                                )
                                //mJsonObject.addProperty("password", password)
                                mJsonObject.addProperty("isSocial", false)
                                mJsonObject.addProperty("socialId", "")
                                mJsonObject.addProperty(
                                    "deviceToken",
                                    GlobalConstants.NOTIFICATION_TOKEN
                                )
                                mJsonObject.addProperty("platform", "android")
                                if (UtilsFunctions.isNetworkConnected()) {
                                    loginViewModel.callLoginApi(mJsonObject)
                                }
//                                val intent = Intent(this, LandingActivty::class.java)
//                                startActivity(intent)
                            }
                        }
                    }
                    "fbLogin" -> {
                        callbackManager = CallbackManager.Factory.create()
                        LoginManager.getInstance().logInWithReadPermissions(
                            this,
                            Arrays.asList(
                                "public_profile",
                                "email",
                                "user_birthday",
                                "user_friends"
                            )
                        )
                        LoginManager.getInstance().registerCallback(callbackManager,
                            object : FacebookCallback<LoginResult> {
                                override fun onSuccess(loginResult : LoginResult) {
                                    /*val profile : Profile = Profile.getCurrentProfile()

                                    val fName = profile.firstName
                                    val lName = profile.lastName
                                    val fbId = profile.id
                                    var email = ""


                                    Log.e(
                                        "LoginActivity",
                                        "Facebook token: " + loginResult.accessToken.token
                                    )
                                    Log.e(
                                        "LoginActivity",
                                        "Facebook detail: " + loginResult.toString()
                                    )
                                    startActivity(
                                        Intent(
                                            applicationContext,
                                            SignupActivity::class.java
                                        )
                                    )*/
                                    val request =
                                        GraphRequest.newMeRequest(loginResult.accessToken) { `object`, response->
                                            try {
                                                //here is the categoryList that you want
                                                Log.d("FBLOGIN_JSON_RES", `object`.toString())

                                                if (`object`.has("id")) {
                                                    handleSignInResultFacebook(`object`)
                                                } else {
                                                    Log.e("FBLOGIN_FAILD", `object`.toString())
                                                }

                                            } catch (e : Exception) {
                                                e.printStackTrace()
                                                // dismissDialogLogin()
                                            }
                                        }
                                    val parameters = Bundle()
                                    parameters.putString(
                                        "fields",
                                        "name,email,id,picture.type(large)"
                                    )
                                    request.parameters = parameters
                                    request.executeAsync()
                                }

                                override fun onCancel() {
                                    Log.d("LoginActivity", "Facebook onCancel.")

                                }

                                override fun onError(error : FacebookException) {
                                    Log.d("LoginActivity", "Facebook onError.")

                                }
                            })
                        /* activityLoginbinding.fbLogin.setReadPermissions(asList(EMAIL))
                         // If you are using in a fragment, call loginButton.setFragment(this);
                         // Callback registration
                         // If you are using in a fragment, call loginButton.setFragment(this);
 // Callback registration
                         activityLoginbinding.fbLogin.registerCallback(
                             callbackManager,
                             object : FacebookCallback<LoginResult?> {
                                 override fun onSuccess(loginResult : LoginResult?) { // App code
                                 }

                                 override fun onCancel() { // App code
                                 }

                                 override fun onError(exception : FacebookException) { // App code
                                 }
                             })*/
                    }
                }
            })
        )
    }

    private fun handleSignInResultFacebook(jsonObject : JSONObject?) {
        val intent = Intent(
            this,
            SignupActivity::class.java
        )
        intent.putExtra("social", "false")
        intent.putExtra("fbSocial", "true")
        intent.putExtra("googleSocial", "false")
        intent.putExtra("fbData", jsonObject.toString())
        startActivity(intent)

    }

    private fun showError(textView : EditText, error : String) {
        textView.requestFocus()
        textView.error = error
    }

    override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager?.onActivityResult(requestCode, resultCode, data)
    }

}