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
import com.android.sidewalk.utils.Utils
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import com.android.sidewalk.databinding.ActivityProfileBinding
import com.android.sidewalk.model.LoginResponse
import com.android.sidewalk.model.profile.ProfileResponse
import com.android.sidewalk.model.profile.RegionResponse
import com.android.sidewalk.sharedpreference.SharedPrefClass
import com.android.sidewalk.utils.BaseFragment
import com.android.sidewalk.utils.DialogClass
import com.android.sidewalk.utils.ValidationsClass
import com.android.sidewalk.viewmodels.profile.ProfileViewModel
import com.android.sidewalk.views.trucks.AddTruckActivity
import kotlin.collections.HashMap

class ProfileFragment : BaseFragment() {
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


        profieViewModel.getDetail().observe(this,
            Observer<ProfileResponse> { response->
                baseActivity.stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            /*profileBinding.model = response

                            SharedPrefClass()
                                .putObject(
                                    activity!!,
                                    GlobalConstants.USER_IMAGE,
                                    response.data!!.image
                                )
                            SharedPrefClass()
                                .putObject(
                                    activity!!,
                                    getString(R.string.fname),
                                    response.data!!.firstName + " " + response.data!!.lastName
                                )

                            profileBinding.txtEmail.setText(response.data?.email)
                            profileBinding.txtUsername.setText(response.data?.firstName + " " + response.data?.lastName)
*/
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
                    "img_right" -> {
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

}
