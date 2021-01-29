package com.android.sidewalk.views.authentication

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
import com.android.sidewalk.databinding.ActivitySignupBinding
import com.android.sidewalk.model.CommonModel
import com.android.sidewalk.model.LoginResponse
import com.android.sidewalk.sharedpreference.SharedPrefClass
import com.android.sidewalk.utils.DialogClass
import com.android.sidewalk.utils.Utils
import com.android.sidewalk.utils.ValidationsClass
import com.android.sidewalk.viewmodels.LoginViewModel
import com.android.sidewalk.views.home.LandingActivty
import com.bumptech.glide.Glide
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SignupActivity : BaseActivity(), ChoiceCallBack {
    private lateinit var activitySignupbinding : ActivitySignupBinding
    private lateinit var loginViewModel : LoginViewModel
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
        return R.layout.activity_signup
    }

    override fun initViews() {
        activitySignupbinding =
            viewDataBinding as ActivitySignupBinding //DataBindingUtil.setContentView(this, R.layout.activity_login)
        loginViewModel = ViewModelProviders.of(this)
            .get(LoginViewModel::class.java)
        activitySignupbinding.loginViewModel = loginViewModel
        val fbSocial = intent.extras.get("fbSocial").toString()
        if (fbSocial.equals("true")) {
            isSocial = true
            socialType = "facebook"
            val fbDetails = JSONObject(intent.extras.get("fbData").toString())
            if (fbDetails.has("id")) {
                socialId = fbDetails.getString("id")
            }

            if (fbDetails.has("name")) {
                val fullname = fbDetails.getString("name")
                if (fullname.contains(" ")) {
                    var name = fullname.split(" ")
                    val firstName = name[0]
                    val lastname = name[1]

                    activitySignupbinding.edtFirstName.setText(firstName)
                    activitySignupbinding.edtLastName.setText(lastname)
                } else {
                    activitySignupbinding.edtFirstName.setText(fullname)
                }
            }
            if (fbDetails.has("email")) {
                val email = fbDetails.getString("email")
                activitySignupbinding.edtEmail.setText(email)

            }
        }
        // activityLoginbinding.tvForgotPassword.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        loginViewModel.getSignupRes().observe(this,
            Observer<LoginResponse> { loginResponse->
                stopProgressDialog()
                // FirebaseFunctions.sendOTP("login", mOtpJsonObject, this)
                if (loginResponse != null) {
                    val message = loginResponse.message

                    if (loginResponse.code == 200) {
                        /* SharedPrefClass().putObject(
                             MyApplication.instance,
                             "isLogin",
                             true
                         )*/
                        GlobalConstants.VERIFICATION_TYPE =
                            "signup"
                        FirebaseFunctions.sendOTP("login", mOtpJsonObject, this)
                        // mOtpJsonObject.addProperty("phoneNumber", response.categoryList?.phoneNumber)
                        //mOtpJsonObject.addProperty("countryCode", response.categoryList?.countryCode)
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
                                GlobalConstants.IS_SOCIAL,
                                isSocial
                            )

                        SharedPrefClass()
                            .putObject(
                                MyApplication.instance,
                                GlobalConstants.USERNAME,
                                activitySignupbinding.edtFirstName.text.toString() + " " + activitySignupbinding.edtLastName.text.toString()
                            )
                        val mJsonObject = JsonObject()
                        mJsonObject.addProperty("userId", loginResponse.data!!.id)
                        mJsonObject.addProperty("sessionToken", loginResponse.data!!.token)
                        //loginViewModel.callVerifyUserApi(mJsonObject)
                        /*showToastSuccess(message)
                        val intent = Intent(this, OTPVerificationActivity::class.java)
                        startActivity(intent)
                        finish()*/

                    } else if (loginResponse.code == 408) {
                        showToastError(message)
                    } else {
                        showToastError(message)
                    }

                }
            })

        loginViewModel.getVerifyUserRes().observe(this,
            Observer<CommonModel> { loginResponse->
                stopProgressDialog()
                if (loginResponse != null) {
                    val message = loginResponse.message

                    if (loginResponse.code == 200) {
                        SharedPrefClass()
                            .putObject(
                                MyApplication.instance,
                                "isLogin",
                                true
                            )
                        //showToastSuccess(message)
                        val intent = Intent(
                            this,
                            LandingActivty::class.java
                        )
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()

                    } else {
                        //showToastError(message)
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
                        val fName = activitySignupbinding.edtFirstName.text.toString()
                        val lName = activitySignupbinding.edtLastName.text.toString()
                        val email = activitySignupbinding.edtEmail.text.toString()
                        val phone = activitySignupbinding.edtPhone.text.toString()
                        val experience = activitySignupbinding.edtExperience.text.toString()

                        when {
                            profileImage.isEmpty() -> showToastError(
                                getString(
                                    R.string.profile_img_error
                                )
                            )
                            fName.isEmpty() -> showError(
                                activitySignupbinding.edtFirstName,
                                getString(R.string.empty) + " " + getString(
                                    R.string.fname
                                )
                            )
                            lName.isEmpty() -> showError(
                                activitySignupbinding.edtLastName,
                                getString(R.string.empty) + " " + getString(
                                    R.string.lname
                                )
                            )
                            email.isEmpty() -> showError(
                                activitySignupbinding.edtEmail,
                                getString(R.string.empty) + " " + getString(
                                    R.string.email
                                )
                            )
                            !email.matches((ValidationsClass.EMAIL_PATTERN).toRegex()) ->
                                showError(
                                    activitySignupbinding.edtEmail,
                                    getString(R.string.invalid) + " " + getString(
                                        R.string.email
                                    )
                                )
                            phone.isEmpty() -> showError(
                                activitySignupbinding.edtPhone,
                                getString(R.string.empty) + " " + getString(
                                    R.string.phone_number
                                )
                            )
                            phone.length < 10 -> showError(
                                activitySignupbinding.edtPhone,
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
                            !activitySignupbinding.chkTerms.isChecked() -> {
                                showToastError(
                                    getString(
                                        R.string.agree_terms_msg
                                    )
                                )
                            }
                            else -> {
                                mOtpJsonObject.addProperty(
                                    "countryCode",
                                    "+" + activitySignupbinding.btnCcp.selectedCountryCode
                                )
                                mOtpJsonObject.addProperty("phoneNumber", phone)
                                val mHashMap = HashMap<String, RequestBody>()
                                mHashMap["firstName"] =
                                    Utils(this).createPartFromString(fName)
                                mHashMap["lastName"] =
                                    Utils(this).createPartFromString(lName)
                                mHashMap["countryCode"] =
                                    Utils(this).createPartFromString("+" + activitySignupbinding.btnCcp.selectedCountryCode)
                                mHashMap["phoneNumber"] =
                                    Utils(this).createPartFromString(phone)
                                mHashMap["email"] =
                                    Utils(this).createPartFromString(email)
                                mHashMap["isSocial"] =
                                    Utils(this).createPartFromString(isSocial.toString())
                                /* mHashMap["password"] =
                                     Utils(this).createPartFromString("12345678")*/
                                mHashMap["deviceToken"] =
                                    Utils(this).createPartFromString("deivce_token")
                                mHashMap["platform"] =
                                    Utils(this).createPartFromString("android")
                                mHashMap["socialType"] =
                                    Utils(this).createPartFromString(socialType)
                                mHashMap["socialId"] =
                                    Utils(this).createPartFromString(socialId)
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
                                if (!lincesFront.isEmpty()) {
                                    val f1 = File(lincesFront)
                                    licenseFront =
                                        Utils(this)
                                            .prepareFilePart(
                                                "licenseFront",
                                                f1
                                            )
                                }
                                if (!lincesBack.isEmpty()) {
                                    val f1 = File(lincesBack)
                                    licenseBack =
                                        Utils(this)
                                            .prepareFilePart(
                                                "licenseBack",
                                                f1
                                            )
                                }
                                //val mJsonObject = JsonObject()
                                //mJsonObject.addProperty("firstName", fName)
                                // mJsonObject.addProperty("lastName", lName)
                                /* mJsonObject.addProperty(
                                     "countryCode",
                                     "+" + activitySignupbinding.btnCcp.selectedCountryCode
                                 )*/
                                // mJsonObject.addProperty("phoneNumber", phone)
                                // mJsonObject.addProperty("email", email)
                                // mJsonObject.addProperty("password", password)
                                //mJsonObject.addProperty("isSocial", isSocial)
                                // mJsonObject.addProperty("deviceToken", "deivce_token")
                                // mJsonObject.addProperty("platform", "android")
                                // mJsonObject.addProperty("socialType", socialType)
                                // mJsonObject.addProperty("socialId", socialId)
                                if (UtilsFunctions.isNetworkConnected()) {
                                    loginViewModel.callSignupApi(
                                        mHashMap,
                                        userImage,
                                        licenseFront,
                                        licenseBack
                                    )
                                }
                                //val intent = Intent(this, OTPVerificationActivity::class.java)
                                //intent.putExtra("itemId", ""/*categoriesList[position].id*/)
                                //startActivity(intent)
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
                .into(activitySignupbinding.imgProfile)
        } else if (imageClicked == 2) {
            activitySignupbinding.imgFrontPlus.visibility = View.GONE
            activitySignupbinding.txtFront.visibility = View.GONE
            Glide.with(this)
                .load(path)
                .into(activitySignupbinding.imgFront)
        } else {
            activitySignupbinding.imgBackPlus.visibility = View.GONE
            activitySignupbinding.txtBack.visibility = View.GONE
            Glide.with(this)
                .load(path)
                .into(activitySignupbinding.imgBack)
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