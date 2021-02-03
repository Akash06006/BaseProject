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
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.android.sidewalk.utils.BaseActivity
import com.android.sidewalk.R
import com.android.sidewalk.application.MyApplication
import com.android.sidewalk.callbacks.ChoiceCallBack
import com.android.sidewalk.common.UtilsFunctions
import com.android.sidewalk.constants.GlobalConstants
import com.android.sidewalk.databinding.ActivityEditProfileBinding
import com.android.sidewalk.model.CommonModel
import com.android.sidewalk.model.profile.ProfileResponse
import com.android.sidewalk.sharedpreference.SharedPrefClass
import com.android.sidewalk.utils.DialogClass
import com.android.sidewalk.utils.Utils
import com.android.sidewalk.utils.ValidationsClass
import com.android.sidewalk.viewmodels.profile.ProfileViewModel
import com.bumptech.glide.Glide
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class EditProfileActivity : BaseActivity(), ChoiceCallBack {
    private lateinit var activityEditProfileBinding : ActivityEditProfileBinding
    private lateinit var profileViewModel : ProfileViewModel
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
        return R.layout.activity_edit_profile
    }

    override fun initViews() {
        activityEditProfileBinding =
            viewDataBinding as ActivityEditProfileBinding //DataBindingUtil.setContentView(this, R.layout.activity_login)
        profileViewModel = ViewModelProviders.of(this)
            .get(ProfileViewModel::class.java)
        activityEditProfileBinding.loginViewModel = profileViewModel

        activityEditProfileBinding.toolbarCommon.imgToolbarText.text =
            getString(R.string.my_profile)
        // activityLoginbinding.tvForgotPassword.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        profileViewModel.geUpdateProfileRes().observe(this,
            Observer<CommonModel> { profileResponse->
                stopProgressDialog()
                // FirebaseFunctions.sendOTP("login", mOtpJsonObject, this)
                if (profileResponse != null) {
                    val message = profileResponse.message

                    if (profileResponse.code == 200) {
                        profileViewModel.callProfielApi()
                        showToastSuccess(message)
                    } else if (profileResponse.code == 408) {
                        showToastError(message)
                    } else {
                        showToastError(message)
                    }

                }
            })

        profileViewModel.getDetail().observe(this,
            Observer<ProfileResponse> { profileResponse->
                stopProgressDialog()
                // FirebaseFunctions.sendOTP("login", mOtpJsonObject, this)
                if (profileResponse != null) {
                    val message = profileResponse.message

                    if (profileResponse.code == 200) {
                        /* SharedPrefClass().putObject(
                             MyApplication.instance,
                             "isLogin",
                             true
                         )*/
                        activityEditProfileBinding.profileResponse = profileResponse.data

                        SharedPrefClass()
                            .putObject(
                                MyApplication.instance,
                                GlobalConstants.USER_IMAGE,
                                profileResponse.data!!.image
                            )

                        SharedPrefClass()
                            .putObject(
                                MyApplication.instance,
                                GlobalConstants.USERNAME,
                                profileResponse.data!!.firstName + " " + profileResponse.data!!.lastName
                            )
                        SharedPrefClass()
                            .putObject(
                                MyApplication.instance,
                                GlobalConstants.CUSTOMER_IAMGE,
                                profileResponse.data!!.image
                            )

                        if (!TextUtils.isEmpty(profileResponse.data!!.licenseFront)) {
                            lincesFront = profileResponse.data!!.licenseFront!!
                            activityEditProfileBinding.imgFrontPlus.visibility = View.GONE
                            activityEditProfileBinding.txtFront.visibility = View.GONE
                            Glide.with(this).load(profileResponse.data!!.licenseFront)
                                .into(activityEditProfileBinding.imgFront)
                        }

                        if (!TextUtils.isEmpty(profileResponse.data!!.licenseBack)) {
                            lincesBack = profileResponse.data!!.licenseBack!!
                            activityEditProfileBinding.imgBackPlus.visibility = View.GONE
                            activityEditProfileBinding.txtBack.visibility = View.GONE
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

        profileViewModel.isLoading().observe(this, Observer<Boolean> { aBoolean->
            if (aBoolean!!) {
                startProgressDialog()
            } else {
                stopProgressDialog()
            }
        })

        profileViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it : String?) {
                when (it) {
                    "iv_edit" -> {
                        imageClicked = 1
                        if (checkPersmission()) {
                            confirmationDialog =
                                mDialogClass.setUploadConfirmationDialog(
                                    this,
                                    this,
                                    "gallery"
                                )
                        } else requestPermission()
                    }
                    "imgFront" -> {
                        imageClicked = 2
                        if (checkPersmission()) {
                            confirmationDialog =
                                mDialogClass.setUploadConfirmationDialog(
                                    this,
                                    this,
                                    "gallery"
                                )
                        } else requestPermission()
                    }
                    "imgBack" -> {
                        imageClicked = 3
                        if (checkPersmission()) {
                            confirmationDialog =
                                mDialogClass.setUploadConfirmationDialog(
                                    this,
                                    this,
                                    "gallery"
                                )
                        } else requestPermission()
                    }
                    "btnSignup" -> {
                        val fName = activityEditProfileBinding.edtFirstName.text.toString()
                        val lName = activityEditProfileBinding.edtLastName.text.toString()
                        val email = activityEditProfileBinding.edtEmail.text.toString()
                        val phone = activityEditProfileBinding.edtPhone.text.toString()
                        val experience = activityEditProfileBinding.edtExperience.text.toString()
                        when {
                            fName.isEmpty() -> showError(
                                activityEditProfileBinding.edtFirstName,
                                getString(R.string.empty) + " " + getString(
                                    R.string.fname
                                )
                            )
                            lName.isEmpty() -> showError(
                                activityEditProfileBinding.edtLastName,
                                getString(R.string.empty) + " " + getString(
                                    R.string.lname
                                )
                            )
                            email.isEmpty() -> showError(
                                activityEditProfileBinding.edtEmail,
                                getString(R.string.empty) + " " + getString(
                                    R.string.email
                                )
                            )
                            !email.matches((ValidationsClass.EMAIL_PATTERN).toRegex()) ->
                                showError(
                                    activityEditProfileBinding.edtEmail,
                                    getString(R.string.invalid) + " " + getString(
                                        R.string.email
                                    )
                                )
                            phone.isEmpty() -> showError(
                                activityEditProfileBinding.edtPhone,
                                getString(R.string.empty) + " " + getString(
                                    R.string.phone_number
                                )
                            )
                            phone.length < 10 -> showError(
                                activityEditProfileBinding.edtPhone,
                                getString(R.string.phone_number) + " " + getString(
                                    R.string.phone_min
                                )
                            )
                            lincesFront.isEmpty() && !lincesBack.isEmpty() -> showToastError(
                                getString(
                                    R.string.license_front_img_error
                                )
                            )
                            !lincesFront.isEmpty() && lincesBack.isEmpty() -> showToastError(
                                getString(
                                    R.string.license_back_img_error
                                )
                            )
                            else -> {
                                val mHashMap = HashMap<String, RequestBody>()
                                mHashMap["firstName"] =
                                    Utils(this).createPartFromString(fName)
                                mHashMap["lastName"] =
                                    Utils(this).createPartFromString(lName)
                                mHashMap["countryCode"] =
                                    Utils(this).createPartFromString(activityEditProfileBinding.edtCountryCode.text.toString())
                                mHashMap["phoneNumber"] =
                                    Utils(this).createPartFromString(phone)
                                mHashMap["email"] =
                                    Utils(this).createPartFromString(email)
                                /* mHashMap["password"] =
                                     Utils(this).createPartFromString("12345678")*/
                                mHashMap["experience"] =
                                    Utils(this).createPartFromString(experience)
                                var userImage : MultipartBody.Part? = null
                                var licenseFront : MultipartBody.Part? = null
                                var licenseBack : MultipartBody.Part? = null
                                if (!profileImage.isEmpty()) {
                                    val f1 = File(profileImage)
                                    userImage =
                                        Utils(this)
                                            .prepareFilePart(
                                                "profileImage",
                                                f1
                                            )
                                }
                                if (!lincesFront.isEmpty() && !lincesFront.contains("http")) {
                                    val f1 = File(lincesFront)
                                    licenseFront =
                                        Utils(this)
                                            .prepareFilePart(
                                                "licenseFront",
                                                f1
                                            )
                                }
                                if (!lincesBack.isEmpty() && !lincesBack.contains("http")) {
                                    val f1 = File(lincesBack)
                                    licenseBack =
                                        Utils(this)
                                            .prepareFilePart(
                                                "licenseBack",
                                                f1
                                            )
                                }
                                if (UtilsFunctions.isNetworkConnected()) {
                                    profileViewModel.callUpdateProfile(
                                        mHashMap,
                                        userImage,
                                        licenseFront,
                                        licenseBack
                                    )
                                }

                            }
                        }

                    }
                }
            })
        )

    }

    private fun showError(textView : EditText, error : String) {
        textView.requestFocus()
        textView.error = error
    }

    private fun checkPersmission() : Boolean {
        return (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode : Int,
        permissions : Array<String>,
        grantResults : IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {
                    confirmationDialog =
                        mDialogClass.setUploadConfirmationDialog(
                            this,
                            this,
                            "gallery"
                        )

                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
            else -> {
            }
        }
    }

    override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
            val selectedImage = data.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor =
                contentResolver.query(
                    selectedImage!!,
                    filePathColumn,
                    null,
                    null,
                    null
                )
            cursor?.moveToFirst()
            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            val picturePath = cursor.getString(columnIndex)
            if (imageClicked == 1) {
                profileImage = picturePath
            } else if (imageClicked == 2) {
                lincesFront = picturePath
            } else {
                lincesBack = picturePath
            }

            setImage(picturePath)
            cursor.close()
        } else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK /*&& null != categoryList*/) {
            setImage(profileImage)            // val extras = categoryList!!.extras
            // val imageBitmap = extras!!.get("categoryList") as Bitmap
            //getImageUri(imageBitmap)
        }

    }

    override fun photoFromCamera(mKey : String) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile : File? = try {
                    createImageFile()
                } catch (ex : IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI : Uri =
                        FileProvider.getUriForFile(
                            this,
                            packageName + ".fileprovider",
                            it
                        )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST)
                }
            }
        }
    }

    private fun createImageFile() : File {
        // Create an image file name
        val timeStamp : String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir : File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        //currentPhotoPath = File(baseActivity?.cacheDir, fileName)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            if (imageClicked == 1) {
                profileImage = absolutePath
            } else if (imageClicked == 2) {
                lincesFront = absolutePath
            } else {
                lincesBack = absolutePath
            }
        }
    }

    private fun setImage(path : String) {
        if (imageClicked == 1) {
            Glide.with(this)
                .load(path)
                .placeholder(R.drawable.ic_user_profile)
                .into(activityEditProfileBinding.imgProfile)
        } else if (imageClicked == 2) {
            activityEditProfileBinding.imgFrontPlus.visibility = View.GONE
            activityEditProfileBinding.txtFront.visibility = View.GONE
            Glide.with(this)
                .load(path)
                .into(activityEditProfileBinding.imgFront)
        } else {
            activityEditProfileBinding.imgBackPlus.visibility = View.GONE
            activityEditProfileBinding.txtBack.visibility = View.GONE
            Glide.with(this)
                .load(path)
                .into(activityEditProfileBinding.imgBack)
        }

    }

    override fun photoFromGallery(mKey : String) {
        val i = Intent(
            Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(i, RESULT_LOAD_IMAGE)
    }

    override fun videoFromCamera(mKey : String) {
        //("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun videoFromGallery(mKey : String) {
        //("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}