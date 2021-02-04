package com.android.sidewalk.views.profile

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.android.sidewalk.R
import com.android.sidewalk.application.MyApplication
import com.android.sidewalk.callbacks.ChoiceCallBack
import com.android.sidewalk.common.UtilsFunctions
import com.bumptech.glide.Glide
import com.android.sidewalk.common.UtilsFunctions.showToastError
import com.android.sidewalk.common.UtilsFunctions.showToastSuccess
import com.android.sidewalk.constants.GlobalConstants
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import com.android.sidewalk.databinding.ActivityProfileBinding
import com.android.sidewalk.model.CommonModel
import com.android.sidewalk.model.LoginResponse
import com.android.sidewalk.model.profile.ProfileResponse
import com.android.sidewalk.model.profile.RegionResponse
import com.android.sidewalk.sharedpreference.SharedPrefClass
import com.android.sidewalk.utils.*
import com.android.sidewalk.viewmodels.profile.ProfileViewModel
import com.android.sidewalk.views.authentication.LoginActivity
import com.android.sidewalk.views.contactus.ContactUsActivity
import com.android.sidewalk.views.trucks.AddTruckActivity
import kotlin.collections.HashMap

class ProfileFragment : BaseFragment(), DialogssInterface {
    private var reginRes =
        ArrayList<RegionResponse.Data>()
    private lateinit var profileBinding : ActivityProfileBinding
    private lateinit var profieViewModel : ProfileViewModel
    private var sharedPrefClass : SharedPrefClass? = null
    private var confirmationDialog : Dialog? = null
    private var mDialogClass = DialogClass()
    private val mJsonObject = JsonObject()
    private val RESULT_LOAD_IMAGE = 100
    private val CAMERA_REQUEST = 1888
    private var profileImage = ""
    private var regionPos = 0
    private var regionId = "0"
    var region = ArrayList<String>()
    override fun getLayoutResId() : Int {
        return R.layout.activity_profile
    }

    override fun onResume() {
        super.onResume()
        val name =
            SharedPrefClass().getPrefValue(
                activity!!,
                GlobalConstants.USERNAME
            )
        val image =
            SharedPrefClass().getPrefValue(
                activity!!,
                GlobalConstants.USER_IMAGE
            )
        profileBinding.txtUsername.text = name.toString()
        Glide.with(activity!!).load(image).into(profileBinding.imgProfile)

    }

    override fun initView() {
        profileBinding = viewDataBinding as ActivityProfileBinding
        profieViewModel = ViewModelProviders.of(this)
            .get(ProfileViewModel::class.java)
        profileBinding.profileViewModel = profieViewModel
        //profileBinding.toolbarCommon.imgRight.visibility = View.VISIBLE
        profileBinding.toolbarCommon.imgRight.visibility = View.INVISIBLE
        //profileBinding.toolbarCommon.imgRight.setImageResource(R.drawable.ic_nav_edit_icon)
        profileBinding.toolbarCommon.toolbar.visibility = View.INVISIBLE
        profileBinding.toolbarCommon.imgToolbarText.text = resources.getString(R.string.settings)
        val languages = resources.getStringArray(R.array.Languages)
        val userId =
            SharedPrefClass()
                .getPrefValue(
                    activity!!,
                    GlobalConstants.USERID
                ).toString()
        val name =
            SharedPrefClass().getPrefValue(
                activity!!,
                GlobalConstants.USERNAME
            )
        val image =
            SharedPrefClass().getPrefValue(
                activity!!,
                GlobalConstants.USER_IMAGE
            )
        profileBinding.txtUsername.text = name.toString()
        mJsonObject.addProperty(
            "userId", userId
        )
        setImage(image.toString())


        profieViewModel.getLogoutResponse().observe(this,
            Observer<CommonModel> { response->
                baseActivity.stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            SharedPrefClass().putObject(
                                activity!!,
                                "isLogin",
                                false
                            )

                            SharedPrefClass().putObject(
                                activity!!,
                                GlobalConstants.USER_IMAGE,
                                "null"
                            )
                            showToastSuccess(getString(R.string.logout_msg))
                            val intent1 = Intent(activity!!, LoginActivity::class.java)
                            startActivity(intent1)

                            activity!!.finish()
                        }
                        else -> message?.let { showToastError(it) }
                    }
                }
            })

        profieViewModel.getDetail().observe(this,
            Observer<ProfileResponse> { response->
                baseActivity.stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            profileBinding.profileResponse = response.data
                        }
                        else -> message?.let { showToastError(it) }
                    }
                }
            })



        profieViewModel.getUpdateDetail().observe(this,
            Observer<LoginResponse> { response->
                baseActivity.stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            // profileBinding.model = response.categoryList
                            message?.let { showToastSuccess(it) }
                            if (UtilsFunctions.isNetworkConnected()) {
                                baseActivity.startProgressDialog()
                                profieViewModel.getProfileDetail(mJsonObject)
                            }

                        }
                        else -> message?.let { showToastError(it) }
                    }

                }
            })


        profieViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it : String?) {
                when (it) {
                    "toolbar" -> {
                        showToastError("clicked")
                    }
                    "upload_profile_layer" -> {
                        val intent = Intent(context, ViewProfileActivity::class.java)
                        startActivity(intent)
                    }
                    "txt_contact_us" -> {
                        val intent = Intent(context, ContactUsActivity::class.java)
                        startActivity(intent)
                    }
                    "txt_log_out" -> {
                        confirmationDialog = mDialogClass.setDefaultDialog(
                            activity!!,
                            this,
                            "logout", "", "",
                            activity!!.resources.getString(R.string.logout_warning)
                        )
                        confirmationDialog?.show()
                    }
                    "txtAboutUs" -> {
                        showToastSuccess("Coming Soon")
                    }
                    "txt_notifications" -> {
                        showToastSuccess("Coming Soon")
                    }
                    "txt_privacy" -> {
                        showToastSuccess("Coming Soon")
                    }
                    "txt_terms" -> {
                        showToastSuccess("Coming Soon")
                    }
                }
            })
        )

    }

    private fun showError(textView : TextView, error : String) {
        textView.requestFocus()
        textView.error = error
    }

    private fun setImage(path : String) {
        Glide.with(this)
            .load(path)
            .placeholder(R.drawable.ic_user_profile)
            .into(profileBinding.imgProfile)
    }

    override fun onDialogConfirmAction(mView : View?, mKey : String?) {
        when (mKey) {
            "logout" -> {
                confirmationDialog?.dismiss()
                profieViewModel!!.callLogoutApi()
                // dashboardViewModel!!.callLogoutApi()

            }
        }
    }

    override fun onDialogCancelAction(mView : View?, mKey : String?) {
        when (mKey) {
            "logout" -> confirmationDialog?.dismiss()
        }
    }

}
