package com.android.sidewalk.views.authentication

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.android.sidewalk.R
import com.android.sidewalk.application.MyApplication
import com.android.sidewalk.common.UtilsFunctions
import com.android.sidewalk.constants.GlobalConstants
import com.android.sidewalk.databinding.ActivityLoginBinding
import com.android.sidewalk.model.LoginResponse
import com.android.sidewalk.sharedpreference.SharedPrefClass
import com.android.sidewalk.utils.BaseActivity
import com.android.sidewalk.viewmodels.LoginViewModel
import com.android.sidewalk.views.home.LandingActivty
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.JsonObject
import org.json.JSONObject
import java.security.MessageDigest
import java.util.*

class LoginActivity : BaseActivity() {
    private lateinit var activityLoginbinding : ActivityLoginBinding
    private lateinit var loginViewModel : LoginViewModel
    private var callbackManager : CallbackManager? = null
    private val EMAIL = "email"
    val RC_SIGN_IN : Int = 1
    private var loginWith = ""
    private var mGoogleSignInClient : GoogleSignInClient? = null
    lateinit var mGoogleSignInOptions : GoogleSignInOptions
    private var googleSiginJSONObject = JsonObject()
    private lateinit var firebaseAuth : FirebaseAuth
    val mOtpJsonObject = JsonObject()
    private var fbSiginJSONObject = JSONObject()
    private var deviceToken = ""

    override fun getLayoutId() : Int {
        return R.layout.activity_login
    }

    override fun initViews() {
        activityLoginbinding =
            viewDataBinding as ActivityLoginBinding //DataBindingUtil.setContentView(this, R.layout.activity_login)
        loginViewModel = ViewModelProviders.of(this)
            .get(LoginViewModel::class.java)

        try {
            val info : PackageInfo = packageManager
                .getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
                Log.i("Key Hash", "key:$hashKey=")
            }
        } catch (e : java.lang.Exception) {
            Log.e("Key Hash", "error:", e)
        }

        activityLoginbinding.loginViewModel = loginViewModel
        configureGoogleSignIn()

        deviceToken = SharedPrefClass().getPrefValue(
            MyApplication.instance,
            GlobalConstants.NOTIFICATION_TOKEN
        ).toString()

        firebaseAuth = FirebaseAuth.getInstance()
        checkSocialObserver()
        loginViewModel.getLoginRes().observe(
            this,
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
                    "googleLogin" -> {
                        loginWith = "google"
                        signIn()
                    }
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

                    }
                }
            })
        )
    }

    private fun configureGoogleSignIn() {
        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)
    }

    private fun signIn() {
        val signInIntent : Intent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun handleSignInResultFacebook(jsonObject : JSONObject?) {
        fbSiginJSONObject = jsonObject!!
        /* val intent = Intent(this, SignupActivity::class.java)
         intent.putExtra("social", "false")
         intent.putExtra("fbSocial", "true")
         intent.putExtra("googleSocial", "false")
         intent.putExtra("fbData", jsonObject.toString())
         startActivity(intent)*/
        val socialId = jsonObject.getString("id")
        var email = ""
        if (jsonObject.has("email")) {
            email = jsonObject.getString("email")
        }
        loginViewModel.checkForSocial(socialId, email, deviceToken)
    }

    private fun showError(textView : EditText, error : String) {
        textView.requestFocus()
        textView.error = error
    }

    /*override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager?.onActivityResult(requestCode, resultCode, data)
    }
*/
    override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task : Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e : ApiException) {
                e.printStackTrace()
                Toast.makeText(this, "Google sign in failed:(", Toast.LENGTH_LONG).show()
            }
        } else {
            callbackManager?.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun firebaseAuthWithGoogle(acct : GoogleSignInAccount) {
        /* val credential = GoogleAuthProvider.getCredential(acct.idToken, null)

        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {*/
        // startActivity(HomeActivity.getLaunchIntent(this))
        try {
            loginWith = "google"
            val jsonObject = JsonObject()
            jsonObject.addProperty("name", acct.displayName)
            jsonObject.addProperty("email", acct.email)
            jsonObject.addProperty("id", acct.id)
            googleSiginJSONObject = jsonObject

            loginViewModel.checkForSocial(acct.id, acct.email, deviceToken)
            /*  val intent = Intent(this, SignupActivity::class.java)
                intent.putExtra("social", "false")
                intent.putExtra("fbSocial", "false")
                intent.putExtra("googleSocial", "true")
                intent.putExtra("data", googleSiginJSONObject.toString())
                startActivity(intent)*/
            // loginViewModel.checkForSocial(acct.id, acct.email, deviceToken)
            mGoogleSignInClient!!.signOut()
            mGoogleSignInClient!!.revokeAccess()
            // showToastSuccess("Google Login Success")
        } catch (e : java.lang.Exception) {
            e.printStackTrace()
            /* } else {
                 Toast.makeText(this, "Google sign in failed :(", Toast.LENGTH_LONG).show()
             }*/
        }
    }

    private fun checkSocialObserver() {
        loginViewModel.checkForSocialData().observe(this,
            Observer { loginResponse->
                stopProgressDialog()
                if (loginResponse != null) {
                    when (loginResponse.code) {
                        200 -> {
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
                            val intent = Intent(this, LandingActivty::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        }
                        211 -> {
                            if (loginWith == "facebook") {
                                val intent = Intent(
                                    this,
                                    SignupActivity::class.java
                                )
                                intent.putExtra("social", "false")
                                intent.putExtra("fbSocial", "true")
                                intent.putExtra("googleSocial", "false")
                                intent.putExtra("data", fbSiginJSONObject.toString())
                                startActivity(intent)
                            } else {
                                val intent = Intent(this, SignupActivity::class.java)
                                intent.putExtra("social", "false")
                                intent.putExtra("fbSocial", "false")
                                intent.putExtra("googleSocial", "true")
                                intent.putExtra("data", googleSiginJSONObject.toString())
                                startActivity(intent)
                            }
                        }
                        else -> {
                            UtilsFunctions.showToastError(loginResponse.message!!)
                        }
                    }
                } else {
                    UtilsFunctions.showToastError(resources.getString(R.string.internal_server_error))
                }
            })
    }
}