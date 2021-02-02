package com.android.sidewalk.views.profile

import android.Manifest
import android.app.Activity
import android.app.Dialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.android.sidewalk.utils.BaseActivity
import com.android.sidewalk.R
import com.android.sidewalk.application.MyApplication
import com.android.sidewalk.callbacks.ChoiceCallBack
import com.android.sidewalk.common.FirebaseFunctions
import com.android.sidewalk.common.UtilsFunctions
import com.android.sidewalk.constants.GlobalConstants
import com.android.sidewalk.databinding.ActivityEditProfileBinding
import com.android.sidewalk.databinding.ActivityViewProfileBinding
import com.android.sidewalk.model.LoginResponse
import com.android.sidewalk.model.profile.ProfileResponse
import com.android.sidewalk.sharedpreference.SharedPrefClass
import com.android.sidewalk.utils.DialogClass
import com.android.sidewalk.utils.Utils
import com.android.sidewalk.utils.ValidationsClass
import com.android.sidewalk.viewmodels.profile.ProfileViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ViewProfileActivity : BaseActivity() {
    private lateinit var activityEditProfileBinding : ActivityViewProfileBinding
    private lateinit var loginViewModel : ProfileViewModel
    val mOtpJsonObject = JsonObject()
    var isSocial = false
    var socialType = ""
    private val PERMISSION_REQUEST_CODE : Int = 101
    var socialId = ""
    private var confirmationDialog : Dialog? = null
    private var mDialogClass = DialogClass()
    private val RESULT_LOAD_IMAGE = 100
    private val CAMERA_REQUEST = 1888
    var imageClicked = 0
    private var profileImage = ""
    private var lincesFront = ""
    private var lincesBack = ""
    override fun getLayoutId() : Int {
        return R.layout.activity_view_profile
    }

    override fun initViews() {
        activityEditProfileBinding =
            viewDataBinding as ActivityViewProfileBinding //DataBindingUtil.setContentView(this, R.layout.activity_login)
        loginViewModel = ViewModelProviders.of(this)
            .get(ProfileViewModel::class.java)
        activityEditProfileBinding.loginViewModel = loginViewModel

        activityEditProfileBinding.toolbarCommon.imgRight.visibility = View.VISIBLE

        Glide.with(this)
            .load(resources.getDrawable(
                R.drawable.user
            ))
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(resources.getDrawable(
                R.drawable.user
            ))
            .into(activityEditProfileBinding.toolbarCommon.imgRight)

        activityEditProfileBinding.toolbarCommon.imgRight.setOnClickListener{
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)

        }

        loginViewModel.getDetail().observe(this,
            Observer<ProfileResponse> { profileResponse->
                stopProgressDialog()
                // FirebaseFunctions.sendOTP("login", mOtpJsonObject, this)
                if (profileResponse != null) {
                    val message = profileResponse.message

                    if (profileResponse.code == 200) {

                        activityEditProfileBinding.profileResponse = profileResponse.data
                        setImage(profileResponse.data!!.image.toString())
                        if (!TextUtils.isEmpty(profileResponse.data!!.licenseFront)) {
                            Glide.with(this).load(profileResponse.data!!.licenseFront)
                                .into(activityEditProfileBinding.imgFront)
                        }

                        if (!TextUtils.isEmpty(profileResponse.data!!.licenseBack)) {
                            Glide.with(this).load(profileResponse.data!!.licenseBack)
                                .into(activityEditProfileBinding.imgBack)
                        }

                    } else if (profileResponse.code == 408) {
                        showToastError(message)
                    } else {
                        showToastError(message)
                    }

                }
            })


        loginViewModel.isLoading().observe(this, Observer<Boolean> { aBoolean->
            if (aBoolean!!) {
                startProgressDialog()
            } else {
                stopProgressDialog()
            }
        })

        loginViewModel.isClick().observe(
            this, Observer<String>(
            fun(it : String?) {
                when (it) {
                    "img_right" -> {
                        val intent = Intent(this, EditProfileActivity::class.java)
                        startActivity(intent)
                    }


                }
            })
        )

    }

    private fun showError(textView : EditText, error : String) {
        textView.requestFocus()
        textView.error = error
    }





    private fun setImage(path : String) {
            Glide.with(this)
                .load(path)
                .placeholder(R.drawable.ic_user_profile)
                .into(activityEditProfileBinding.imgProfile)
    }


}