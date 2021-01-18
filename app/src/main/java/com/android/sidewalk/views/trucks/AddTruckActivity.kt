package com.android.sidewalk.views.trucks

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProviders
import com.android.sidewalk.R
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.sidewalk.callbacks.ChoiceCallBack
import com.android.sidewalk.common.UtilsFunctions
import com.android.sidewalk.databinding.ActivityAddTruckBinding
import com.android.sidewalk.model.CommonModel
import com.android.sidewalk.repositories.truck.AddGalleryModel
import com.android.sidewalk.utils.BaseActivity
import com.android.sidewalk.utils.DialogClass
import com.android.sidewalk.utils.Utils
import com.android.sidewalk.utils.ValidationsClass
import com.android.sidewalk.viewmodels.trucks.TrucksViewModel
import com.bumptech.glide.Glide
import com.uniongoods.adapters.GalleryImagesListAdapter
import com.uniongoods.adapters.ImagesListAdapter
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AddTruckActivity : BaseActivity(), ChoiceCallBack {
    private lateinit var addTruckBinding : ActivityAddTruckBinding
    private lateinit var trucksViewModel : TrucksViewModel
    private val RESULT_LOAD_IMAGE = 100
    private val CAMERA_REQUEST = 1888
    private val PERMISSION_REQUEST_CODE : Int = 101
    private var confirmationDialog : Dialog? = null
    private var mDialogClass = DialogClass()
    private var profileImage = ""
    var imagesListAdapter : ImagesListAdapter? = null
    var imagesList = ArrayList<String>()
    var galleryImagesListAdapter : GalleryImagesListAdapter? = null
    var galleryImagesList = ArrayList<String>()
    var galleryImagesIds = ArrayList<String>()
    var isProfile = false
    var startTime = ""
    var endTime = ""
    var mTimePicker : TimePickerDialog? = null
    override fun getLayoutId() : Int {
        return R.layout.activity_add_truck
    }

    override fun initViews() {
        addTruckBinding = viewDataBinding as ActivityAddTruckBinding

        trucksViewModel = ViewModelProviders.of(this)
            .get(TrucksViewModel::class.java)
        addTruckBinding.truckViewModel = trucksViewModel
        addTruckBinding.toolbarCommon.imgToolbarText.text =
            getString(R.string.add_mobile_cart)
        val linearLayoutManager1 = LinearLayoutManager(this)
        imagesListAdapter = ImagesListAdapter(null, this, imagesList, this)
        addTruckBinding.rvImages.setHasFixedSize(true)
        linearLayoutManager1.orientation = RecyclerView.HORIZONTAL
        addTruckBinding.rvImages.layoutManager = linearLayoutManager1
        addTruckBinding.rvImages.adapter = imagesListAdapter
        addTruckBinding.rvImages.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView : RecyclerView, dx : Int, dy : Int) {

            }
        })
        val linearLayoutManager = LinearLayoutManager(this)
        galleryImagesListAdapter = GalleryImagesListAdapter(this, this, galleryImagesList, this)
        addTruckBinding.rvImages.setHasFixedSize(true)
        linearLayoutManager.orientation = RecyclerView.HORIZONTAL
        addTruckBinding.rvGallery.layoutManager = linearLayoutManager
        addTruckBinding.rvGallery.adapter = galleryImagesListAdapter
        addTruckBinding.rvGallery.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView : RecyclerView, dx : Int, dy : Int) {

            }
        })

        trucksViewModel.getAddGalleryRes().observe(this,
            Observer<AddGalleryModel> { addGalleryRes->
                stopProgressDialog()
                if (addGalleryRes != null) {
                    val message = addGalleryRes.message

                    if (addGalleryRes.code == 200) {
                        galleryImagesIds.add(addGalleryRes.data!!.image!!)
                    } else {
                        UtilsFunctions.showToastError(message!!)
                    }

                }
            })

        trucksViewModel.getAddTruckRes().observe(this,
            Observer<CommonModel> { addGalleryRes->
                stopProgressDialog()
                if (addGalleryRes != null) {
                    val message = addGalleryRes.message

                    if (addGalleryRes.code == 200) {
                        // galleryImagesIds.add(addGalleryRes.data!!.image!!)
                        showToastSuccess(message)
                    } else {
                        UtilsFunctions.showToastError(message!!)
                    }

                }
            })



        trucksViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it : String?) {
                when (it) {
                    "edtStartTime" -> {
                        val mcurrentTime = Calendar.getInstance()
                        val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
                        val minute = mcurrentTime.get(Calendar.MINUTE)

                        mTimePicker =
                            TimePickerDialog(this, object : TimePickerDialog.OnTimeSetListener {
                                override fun onTimeSet(
                                    view : TimePicker?,
                                    hourOfDay : Int,
                                    minute : Int
                                ) {
                                    addTruckBinding.edtStartTime.setText(
                                        String.format(
                                            "%d : %d",
                                            hourOfDay,
                                            minute
                                        )
                                    )
                                }
                            }, hour, minute, true)
                        mTimePicker!!.show()
                    }
                    "edtEndTime" -> {
                        val mcurrentTime = Calendar.getInstance()
                        val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
                        val minute = mcurrentTime.get(Calendar.MINUTE)

                        mTimePicker =
                            TimePickerDialog(this, object : TimePickerDialog.OnTimeSetListener {
                                override fun onTimeSet(
                                    view : TimePicker?,
                                    hourOfDay : Int,
                                    minute : Int
                                ) {
                                    addTruckBinding.edtEndTime.setText(
                                        String.format(
                                            "%d : %d",
                                            hourOfDay,
                                            minute
                                        )
                                    )
                                }
                            }, hour, minute, true)
                        mTimePicker!!.show()
                    }
                    "txtUploadImage" -> {
                        isProfile = true
                        if (checkPersmission()) {
                            confirmationDialog =
                                mDialogClass.setUploadConfirmationDialog(
                                    this,
                                    this,
                                    "gallery"
                                )
                        } else requestPermission()
                    }
                    "imgAddImage" -> {
                        isProfile = true
                        if (checkPersmission()) {
                            confirmationDialog =
                                mDialogClass.setUploadConfirmationDialog(
                                    this,
                                    this,
                                    "gallery"
                                )
                        } else requestPermission()
                    }
                    "txtAddGallery" -> {
                        isProfile = false
                        if (checkPersmission()) {
                            confirmationDialog =
                                mDialogClass.setUploadConfirmationDialog(
                                    this,
                                    this,
                                    "gallery"
                                )
                        } else requestPermission()
                    }
                    "btnSave" -> {
                        val truckName = addTruckBinding.edtTruckName.text.toString()
                        val location = addTruckBinding.edtLocation.text.toString()
                        val regName = addTruckBinding.edtRegNo.text.toString()
                        startTime = addTruckBinding.edtStartTime.text.toString()
                        endTime = addTruckBinding.edtEndTime.text.toString()
                        //Gallery
                        val partnerName = addTruckBinding.edtName.text.toString()
                        val partnerPhone = addTruckBinding.edtPhone.text.toString()
                        when {
                            imagesList.size == 0 -> showToastError(
                                getString(
                                    R.string.upload_img_error
                                )
                            )
                            truckName.isEmpty() -> showError(
                                addTruckBinding.edtTruckName,
                                getString(R.string.empty) + " " + getString(
                                    R.string.mobile_cart_name
                                )
                            )
                            location.isEmpty() -> showError(
                                addTruckBinding.edtLocation,
                                getString(R.string.empty) + " " + getString(
                                    R.string.lname
                                )
                            )
                            regName.isEmpty() -> showError(
                                addTruckBinding.edtRegNo,
                                getString(R.string.empty) + " " + getString(
                                    R.string.reg_no
                                )
                            )
                            startTime.isEmpty() -> showToastError(
                                getString(
                                    R.string.select_start_time
                                )
                            )
                            endTime.isEmpty() -> showToastError(
                                getString(
                                    R.string.select_end_time
                                )
                            )
                            partnerName.isEmpty() -> showError(
                                addTruckBinding.edtName,
                                getString(R.string.empty) + " " + getString(
                                    R.string.name
                                )
                            )
                            partnerPhone.isEmpty() -> showError(
                                addTruckBinding.edtPhone,
                                getString(R.string.empty) + " " + getString(
                                    R.string.phone_number
                                )
                            )
                            else -> {
                                val mHashMap = HashMap<String, RequestBody>()
                                mHashMap["name"] =
                                    Utils(this).createPartFromString(truckName)
                                mHashMap["location"] =
                                    Utils(this).createPartFromString(location)
                                mHashMap["registrationNo"] =
                                    Utils(this).createPartFromString(regName)
                                mHashMap["startTime"] =
                                    Utils(this).createPartFromString(startTime)
                                mHashMap["endTime"] =
                                    Utils(this).createPartFromString(endTime)
                                mHashMap["partnerName"] =
                                    Utils(this).createPartFromString(partnerName)
                                mHashMap["partnerNumber"] =
                                    Utils(this).createPartFromString(partnerPhone)
                                var ids = ""
                                for (item in galleryImagesIds) {
                                    if (TextUtils.isEmpty(ids)) {
                                        ids = item
                                    } else {
                                        ids = ids + "," + item
                                    }
                                }
                                mHashMap["galleryImages"] =
                                    Utils(this).createPartFromString(ids/*galleryImagesIds.toString()*/)
                                var imagesParts : Array<MultipartBody.Part?>? = null
                                if (imagesList.size > 0) {
                                    imagesParts =
                                        arrayOfNulls<MultipartBody.Part>(imagesList.count())
                                    for (i in 0 until imagesList.count()) {
                                        val f1 = File(imagesList[i])
                                        imagesParts!![i] = Utils(this).prepareFilePart("image", f1)

                                    }
                                }


                                if (UtilsFunctions.isNetworkConnected()) {
                                    startProgressDialog()
                                    trucksViewModel.addUpdateTruck(
                                        imagesParts,
                                        mHashMap
                                    )
                                }
                            }
                        }
                    }
                }
            })
        )

    }

    private fun checkPersmission() : Boolean {
        return (ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA
        ) ==
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

    private fun showError(textView : EditText, error : String) {
        textView.requestFocus()
        textView.error = error
    }

    override fun onActivityResult(
        requestCode : Int,
        resultCode : Int,
        data : Intent?
    ) {
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
            profileImage = picturePath


            setImage(picturePath)
            cursor.close()
        } else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK /*&& null != data*/) {
            setImage(profileImage)            // val extras = data!!.extras
            // val imageBitmap = extras!!.get("data") as Bitmap
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
            profileImage = absolutePath
        }
    }

    private fun setImage(path : String) {
        if (isProfile) {
            imagesList.add(path)
            if (imagesList.size > 0) {
                addTruckBinding.txtUploadImage.visibility = View.GONE
                addTruckBinding.imgAddImage.visibility = View.VISIBLE
            }
            if (imagesList.size < 3) {
                addTruckBinding.imgAddImage.visibility = View.VISIBLE
            } else {
                addTruckBinding.imgAddImage.visibility = View.GONE
            }
            imagesListAdapter?.notifyDataSetChanged()
        } else {
            startProgressDialog()
            galleryImagesList.add(path)
            galleryImagesListAdapter?.notifyDataSetChanged()
            var bannerImage : MultipartBody.Part? = null
            if (!profileImage.isEmpty()) {
                val f1 = File(profileImage)
                bannerImage =
                    Utils(this)
                        .prepareFilePart(
                            "image",
                            f1
                        )
            }

            if (UtilsFunctions.isNetworkConnected()) {
                trucksViewModel.addGalleryImage(
                    bannerImage
                )
            }

        }

    }

    override fun photoFromGallery(mKey : String) {
        val i = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(i, RESULT_LOAD_IMAGE)
    }

    override fun videoFromCamera(mKey : String) {
        //("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun videoFromGallery(mKey : String) {
        //("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun removeImage(pos : Int) {
        imagesList.removeAt(pos)
        if (imagesList.size > 0) {
            addTruckBinding.txtUploadImage.visibility = View.GONE
            addTruckBinding.imgAddImage.visibility = View.VISIBLE
        }
        if (imagesList.size < 3) {
            addTruckBinding.imgAddImage.visibility = View.VISIBLE
        } else {
            addTruckBinding.imgAddImage.visibility = View.GONE
        }
        imagesListAdapter?.notifyDataSetChanged()
        /*  Glide.with(this)
              .load(path)
              .placeholder(R.drawable.user)
              .into(profileBinding.imgProfile)*/
    }

    fun removeGalleryImage(pos : Int) {
        galleryImagesList.removeAt(pos)
        galleryImagesIds.removeAt(pos)
        galleryImagesListAdapter?.notifyDataSetChanged()
        /*  Glide.with(this)
              .load(path)
              .placeholder(R.drawable.user)
              .into(profileBinding.imgProfile)*/
    }

}
