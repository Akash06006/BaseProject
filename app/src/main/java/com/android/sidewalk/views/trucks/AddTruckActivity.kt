package com.android.sidewalk.views.trucks

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
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
import com.android.sidewalk.model.ImagesModel
import com.android.sidewalk.model.truck.TruckDetailResponse
import com.android.sidewalk.repositories.truck.AddGalleryModel
import com.android.sidewalk.utils.BaseActivity
import com.android.sidewalk.utils.DialogClass
import com.android.sidewalk.utils.ResizeImage
import com.android.sidewalk.utils.Utils
import com.android.sidewalk.viewmodels.trucks.TrucksViewModel
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
    private var imagesListAdapter : ImagesListAdapter? = null
    private var imagesList = ArrayList<ImagesModel>()
    private var galleryImagesListAdapter : GalleryImagesListAdapter? = null
    private var galleryImagesList = ArrayList<ImagesModel>()
    private var galleryImagesDeletedIds = ArrayList<String>()
    private var truckImagesDeletedIds = ArrayList<String>()
    private var isProfile = false
    private var startTime = ""
    private var endTime = ""
    private var mTimePicker : TimePickerDialog? = null
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
        val truckId = intent.extras?.get("id") as String
        if (!TextUtils.isEmpty(truckId)) {
            addTruckBinding.toolbarCommon.imgToolbarText.text =
                getString(R.string.update_mobile_cart)
            if (UtilsFunctions.isNetworkConnected()) {
                startProgressDialog()
                trucksViewModel.truckDetail(truckId)
            }
        }
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
                        val last = galleryImagesList.get(galleryImagesList.size - 1)
                        last.id = addGalleryRes.data!!.image!!
                        galleryImagesList[galleryImagesList.size - 1] = last

                    } else {
                        UtilsFunctions.showToastError(message!!)
                        val last = galleryImagesList.get(galleryImagesList.size - 1)
                        galleryImagesList.remove(last)
                    }
                }
            })

        trucksViewModel.getAddTruckRes().observe(this,
            Observer<CommonModel> { addGalleryRes->
                stopProgressDialog()
                if (addGalleryRes != null) {
                    val message = addGalleryRes.message

                    if (addGalleryRes.code == 200) {
                        // galleryImagesIds.add(addGalleryRes.categoryList!!.image!!)
                        showToastSuccess(message)
                        finish()
                    } else {
                        UtilsFunctions.showToastError(message!!)
                    }
                }
            })
        trucksViewModel.getTruckDetail().observe(this,
            Observer<TruckDetailResponse> { addGalleryRes->
                stopProgressDialog()
                if (addGalleryRes != null) {
                    val message = addGalleryRes.message

                    if (addGalleryRes.code == 200) {
                        // galleryImagesIds.add(addGalleryRes.categoryList!!.image!!)
                        addTruckBinding.detailResponse = addGalleryRes.data

                        for (item in addGalleryRes.data!!.truckImages!!) {
                            val imagesModel = ImagesModel()
                            imagesModel.image = item
                            imagesModel.name = "http"
                            imagesList.add(imagesModel)
                        }
                        if (addGalleryRes.data!!.galleries != null) {
                            for (item1 in addGalleryRes.data!!.galleries!!) {
                                if (!TextUtils.isEmpty(item1.image)) {
                                    val imagesModel = ImagesModel()
                                    imagesModel.image = item1.image!!
                                    imagesModel.id = item1.id!!
                                    imagesModel.name = item1.image!!
                                    galleryImagesList.add(imagesModel)
                                }
                            }
                        }
                        if (imagesList.size > 0) {
                            addTruckBinding.txtUploadImage.visibility = View.GONE
                            addTruckBinding.imgAddImage.visibility = View.VISIBLE
                        }
                        if (imagesList.size < 3) {
                            addTruckBinding.imgAddImage.visibility = View.VISIBLE
                        } else {
                            addTruckBinding.imgAddImage.visibility = View.GONE
                        }
                        galleryImagesListAdapter?.notifyDataSetChanged()
                        imagesListAdapter?.notifyDataSetChanged()
                    } else {
                        UtilsFunctions.showToastError(message!!)
                    }
                }
            })

        trucksViewModel.isClick().observe(
            this, Observer<String>(
                function =
                fun(it : String?) {
                    when (it) {
                        "edtLocation" -> {
                            if (ContextCompat.checkSelfPermission(
                                    this,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ) !==
                                PackageManager.PERMISSION_GRANTED
                            ) {
                                if (ActivityCompat.shouldShowRequestPermissionRationale(
                                        this!!,
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                    )
                                ) {
                                    ActivityCompat.requestPermissions(
                                        this!!,
                                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                                    )
                                } else {
                                    ActivityCompat.requestPermissions(
                                        this!!,
                                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                                    )
                                }
                            } else {
                                //if (!TextUtils.isEmpty(addTruckBinding.edtLocation.text.toString())) {
                                if (UtilsFunctions.isNetworkConnected()) {
                                    val intent =
                                        Intent(this, AddAddressActivity::class.java)
                                    startActivityForResult(intent, 200)
                                    /*     }
                                     } else {
                                         showToastError("Please select pickup address")
                                     }*/
                                }
                            }
                        }
                        "edtStartTime" -> {
                            val mcurrentTime = Calendar.getInstance()
                            val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
                            val minute = mcurrentTime.get(Calendar.MINUTE)

                            mTimePicker =
                                TimePickerDialog(
                                    this,
                                    object : TimePickerDialog.OnTimeSetListener {
                                        @SuppressLint("SetTextI18n")
                                        override fun onTimeSet(
                                            view : TimePicker?,
                                            hourOfDay : Int,
                                            minute : Int
                                        ) {
                                            var hours = hourOfDay
                                            var amPmformat = ""

                                            if (hours == 0) {
                                                hours += 12

                                                amPmformat = "AM"
                                            } else if (hours == 12) {
                                                amPmformat = "PM"

                                            } else if (hours > 12) {
                                                hours -= 12

                                                amPmformat = "PM"

                                            } else {
                                                amPmformat = "AM"
                                            }

                                            addTruckBinding.edtStartTime.setText("$hours : $minute $amPmformat")
                                        }

                                    }
                                    ,
                                    hour,
                                    minute,
                                    false)
                            mTimePicker!!.show()
                        }
                        "edtEndTime" -> {
                            val mcurrentTime = Calendar.getInstance()
                            val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
                            val minute = mcurrentTime.get(Calendar.MINUTE)

                            mTimePicker =
                                TimePickerDialog(
                                    this,
                                    object : TimePickerDialog.OnTimeSetListener {
                                        @SuppressLint("SetTextI18n")
                                        override fun onTimeSet(
                                            view : TimePicker?,
                                            hourOfDay : Int,
                                            minute : Int
                                        ) {
                                            var hours = hourOfDay
                                            var format = ""

                                            if (hours == 0) {
                                                hours += 12

                                                format = "AM"
                                            } else if (hours == 12) {
                                                format = "PM"

                                            } else if (hours > 12) {
                                                hours -= 12

                                                format = "PM"

                                            } else {
                                                format = "AM"
                                            }

                                            addTruckBinding.edtEndTime.setText("$hours : $minute $format")

                                        }
                                    },
                                    hour,
                                    minute,
                                    false
                                )
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
                                (imagesList.size == 0 && TextUtils.isEmpty(truckId)) -> showToastError(
                                    getString(
                                        R.string.upload_img_error
                                    )
                                )
                                truckName.trim().isEmpty() -> showError(
                                    addTruckBinding.edtTruckName,
                                    getString(R.string.empty) + " " + getString(
                                        R.string.mobile_cart_name
                                    )
                                )
                                location.trim().isEmpty() -> showError(
                                    addTruckBinding.edtLocation,
                                    getString(R.string.empty) + " " + getString(
                                        R.string.lname
                                    )
                                )
                                regName.trim().isEmpty() -> showError(
                                    addTruckBinding.edtRegNo,
                                    getString(R.string.empty) + " " + getString(
                                        R.string.reg_no
                                    )
                                )
                                startTime.trim().isEmpty() -> showToastError(
                                    getString(
                                        R.string.select_start_time
                                    )
                                )
                                endTime.trim().isEmpty() -> showToastError(
                                    getString(
                                        R.string.select_end_time
                                    )
                                )
                                partnerName.trim().isEmpty() -> showError(
                                    addTruckBinding.edtName,
                                    getString(R.string.empty) + " " + getString(
                                        R.string.name
                                    )
                                )
                                partnerPhone.trim().isEmpty() -> showError(
                                    addTruckBinding.edtPhone,
                                    getString(R.string.empty) + " " + getString(
                                        R.string.phone_number
                                    )
                                )
                                partnerPhone.trim().length < 7 -> showError(
                                    addTruckBinding.edtPhone,
                                    getString(R.string.phone_number) + " " + getString(
                                        R.string.phone_min
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
                                    mHashMap["truckId"] =
                                        Utils(this).createPartFromString(truckId)
                                    var ids = ""
                                    for (item in galleryImagesList) {
                                        if (!item.image!!.contains("http")) {
                                            if (!TextUtils.isEmpty(item.id)) {
                                                if (TextUtils.isEmpty(ids)) {
                                                    ids = item.id!!
                                                } else {
                                                    ids = ids + "," + item.id
                                                }
                                            }

                                        }
                                    }

                                    mHashMap["galleryImages"] =
                                        Utils(this).createPartFromString(ids/*galleryImagesIds.toString()*/)
                                    var galleryDeletedIds = ""
                                    for (item in galleryImagesDeletedIds) {
                                        if (TextUtils.isEmpty(galleryDeletedIds)) {
                                            galleryDeletedIds = item
                                        } else {
                                            galleryDeletedIds = galleryDeletedIds + "," + item
                                        }
                                    }
                                    mHashMap["deleteGalImg"] =
                                        Utils(this).createPartFromString(galleryDeletedIds)
                                    var truckDeletedIds = ""
                                    for (item in truckImagesDeletedIds) {
                                        if (TextUtils.isEmpty(truckDeletedIds)) {
                                            truckDeletedIds = item
                                        } else {
                                            truckDeletedIds = truckDeletedIds + "," + item
                                        }
                                    }
                                    mHashMap["deleteImgs"] =
                                        Utils(this).createPartFromString(truckDeletedIds)
                                    var finalUploadedImages = ArrayList<String>()
                                    for (item in imagesList) {
                                        if (!item.name.equals("http")) {
                                            // imagesList.remove(item)
                                            finalUploadedImages.add(item.name!!)
                                        }
                                    }
                                    var imagesParts : Array<MultipartBody.Part?>? = null
                                    if (finalUploadedImages.size > 0) {
                                        imagesParts =
                                            arrayOfNulls<MultipartBody.Part>(finalUploadedImages.count())
                                        for (i in 0 until finalUploadedImages.count()) {
                                            val f1 =
                                                File(
                                                    ResizeImage.compressImage(
                                                        finalUploadedImages[i]
                                                    )
                                                )
                                            //val f1 = File(finalUploadedImages[i])
                                            imagesParts!![i] =
                                                Utils(this).prepareFilePart("image", f1)

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
        } else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK /*&& null != categoryList*/) {
            setImage(profileImage)            // val extras = categoryList!!.extras
            // val imageBitmap = extras!!.get("categoryList") as Bitmap
            //getImageUri(imageBitmap)
        } else if (requestCode == 200) {
            val lat = data?.getStringExtra("lat")
            val long = data?.getStringExtra("long")
            val address = data?.getStringExtra("address")
            if (!TextUtils.isEmpty(lat)) {
                addTruckBinding.edtLocation.setText(address)
                val latitude = lat as String
                val longitude = long.toString()
                //delAddress = address.toString()
            }

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

    @SuppressLint("SimpleDateFormat")
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
            val imagesModel = ImagesModel()
            imagesModel.image = path
            imagesModel.name = path
            imagesList.add(imagesModel)
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
            val imageModel = ImagesModel()
            imageModel.image = path
            imageModel.name = path
            galleryImagesList.add(imageModel)
            galleryImagesListAdapter?.notifyDataSetChanged()
            var bannerImage : MultipartBody.Part? = null
            if (!profileImage.isEmpty()) {
                //val f1 = File(profileImage)
                val f1 =
                    File(ResizeImage.compressImage(profileImage))
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
        if (imagesList[pos].image!!.contains("http")) {
            truckImagesDeletedIds.add(imagesList[pos].image!!)
        }
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

    fun removeGalleryImage(pos : Int, path : String) {
        for (item in galleryImagesList) {
            if (item.image!!.contains("http")) {
                if (!TextUtils.isEmpty(path)) {
                    if (item.image.equals(path)) {
                        galleryImagesDeletedIds.add(galleryImagesList[pos].id!!)
                        galleryImagesList.remove(item)
                        break
                    }
                }
            } else {
                if (item.image.equals(path)) {
                    galleryImagesList.remove(item)
                    break
                }
            }
        }
        galleryImagesListAdapter?.notifyDataSetChanged()

    }
    /*override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?) {
        // activityCreateOrderBinding.autocompleteFragment.visibility = View.GONE
        val inputManager : InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        *//* inputManager.hideSoftInputFromWindow(
             activity!!.currentFocus.windowToken,
             InputMethodManager.SHOW_FORCED
         )*//*

        if (requestCode == 200) {
            val lat = data?.getStringExtra("lat")
            val long = data?.getStringExtra("long")
            val address = data?.getStringExtra("address")
            if (!TextUtils.isEmpty(lat)) {
                addTruckBinding.edtLocation.setText(address)
                val latitude = lat as String
                val longitude = long.toString()
                //delAddress = address.toString()
            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }
*/
}
