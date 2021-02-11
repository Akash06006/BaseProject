package com.android.sidewalk.views.menu

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProviders
import com.android.sidewalk.R
import androidx.lifecycle.Observer
import com.android.sidewalk.callbacks.ChoiceCallBack
import com.android.sidewalk.common.UtilsFunctions
import com.android.sidewalk.databinding.ActivityAddItemBinding
import com.android.sidewalk.model.CommonModel
import com.android.sidewalk.model.menu.ItemDetailResponse
import com.android.sidewalk.utils.BaseActivity
import com.android.sidewalk.utils.DialogClass
import com.android.sidewalk.utils.Utils
import com.android.sidewalk.viewmodels.menu.MenuViewModel
import com.bumptech.glide.Glide
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddItemActivity : BaseActivity(), ChoiceCallBack {
    private lateinit var addItemBinding : ActivityAddItemBinding
    private lateinit var menuViewModel : MenuViewModel
    var imagesList = ArrayList<String>()
    var itemId = ""
    var catId = ""
    var itemType = "veg"
    private val RESULT_LOAD_IMAGE = 100
    private val CAMERA_REQUEST = 1888
    private val PERMISSION_REQUEST_CODE : Int = 101
    private var confirmationDialog : Dialog? = null
    private var mDialogClass = DialogClass()
    private var itemImage = ""
    var isUpdate = false
    override fun getLayoutId() : Int {
        return R.layout.activity_add_item
    }

    override fun initViews() {
        addItemBinding = viewDataBinding as ActivityAddItemBinding

        menuViewModel = ViewModelProviders.of(this)
            .get(MenuViewModel::class.java)
        addItemBinding.menuViewModel = menuViewModel
        addItemBinding.toolbarCommon.imgToolbarText.text =
            getString(R.string.add_item_title)
        catId = intent.extras?.get("id") as String
        itemId = intent.extras?.get("itemId") as String
        if (!TextUtils.isEmpty(itemId)) {
            isUpdate = true
            menuViewModel.itemsDetail(itemId)
            addItemBinding.toolbarCommon.imgToolbarText.text =
                getString(R.string.update_item)
        } else {
            isUpdate = false
        }

        menuViewModel.getAddCategoryRes().observe(this,
            Observer<CommonModel> { addGalleryRes->
                stopProgressDialog()
                if (addGalleryRes != null) {
                    if (isUpdate) {
                        showToastSuccess(getString(R.string.item_updated_successfully))
                    } else {
                        showToastSuccess(getString(R.string.item_added_successfully))
                    }
                    finish()
                }
            })

        menuViewModel.getItemDetailRes().observe(this,
            Observer<ItemDetailResponse> { addGalleryRes->
                stopProgressDialog()
                if (addGalleryRes != null) {
                    if (addGalleryRes.data != null) {
                        addItemBinding.itemDetailResponse = addGalleryRes.data
                        //val message = addGalleryRes.message
                        Glide.with(this).load(addGalleryRes.data!!.image)
                            .into(addItemBinding.imgView)
                        itemImage = addGalleryRes.data!!.image!!
                        if (addGalleryRes.data!!.itemType.equals("veg")) {
                            vegNonVegSelected(true)
                        } else {
                            vegNonVegSelected(false)
                        }
                    }
                }
            })


        menuViewModel.isClick().observe(
            this, Observer<String>(
                function =
                fun(it : String?) {
                    when (it) {
                        "txtVeg" -> {
                            vegNonVegSelected(true)

                        }
                        "txtNonVeg" -> {
                            vegNonVegSelected(false)
                        }
                        "btnAddItem" -> {
                            val name = addItemBinding.edtItemName.text.toString()
                            val price = addItemBinding.edtPrice.text.toString()
                            val description = addItemBinding.edtDescription.text.toString()
                            when {
                                itemImage.isEmpty() -> showToastError(
                                    getString(
                                        R.string.upload_img_error
                                    )
                                )
                                name.trim().isEmpty() -> showError(
                                    addItemBinding.edtItemName,
                                    getString(R.string.empty) + " " + getString(
                                        R.string.item_name
                                    )
                                )
                                price.trim().isEmpty() -> showError(
                                    addItemBinding.edtPrice,
                                    getString(R.string.empty) + " " + getString(
                                        R.string.price
                                    )
                                )
                                price.equals("0") -> {
                                    showError(
                                        addItemBinding.edtPrice,
                                        getString(R.string.invalid) + " " + getString(
                                            R.string.price
                                        )
                                    )
                                }
                                description.trim().isEmpty() -> showError(
                                    addItemBinding.edtDescription,
                                    getString(R.string.empty) + " " + getString(
                                        R.string.description
                                    )
                                )
                                else -> {
                                    val mHashMap = HashMap<String, RequestBody>()
                                    mHashMap["name"] =
                                        Utils(this).createPartFromString(name)
                                    mHashMap["itemId"] =
                                        Utils(this).createPartFromString(itemId)
                                    mHashMap["price"] =
                                        Utils(this).createPartFromString(price)
                                    mHashMap["description"] =
                                        Utils(this).createPartFromString(description)
                                    mHashMap["itemType"] =
                                        Utils(this).createPartFromString(itemType)
                                    mHashMap["categoryId"] =
                                        Utils(this).createPartFromString(catId)
                                    var itemImagePart : MultipartBody.Part? = null
                                    if (itemImage.contains("http")) {
                                        itemImagePart = null
                                    } else {
                                        val f1 = File(itemImage)
                                        itemImagePart =
                                            Utils(this)
                                                .prepareFilePart(
                                                    "image",
                                                    f1
                                                )

                                    }
                                    if (UtilsFunctions.isNetworkConnected()) {
                                        startProgressDialog()
                                        menuViewModel.addItem(
                                            itemImagePart,
                                            mHashMap
                                        )
                                    }

                                }
                                // addCategoryDialog()
                            }
                        }
                        "rlUpload" -> {
                            if (checkPersmission()) {
                                confirmationDialog =
                                    mDialogClass.setUploadConfirmationDialog(
                                        this,
                                        this,
                                        "gallery"
                                    )
                            } else requestPermission()
                        }
                    }
                })
        )
    }

    private fun vegNonVegSelected(isVeg : Boolean) {
        if (isVeg) {
            itemType = "veg"
            addItemBinding.txtVeg.setTextColor(resources.getColor(R.color.colorWhite))
            addItemBinding.txtNonVeg.setTextColor(resources.getColor(R.color.colorGrey))
            addItemBinding.txtVeg.background =
                resources.getDrawable(R.drawable.ic_veg_selected)
            addItemBinding.txtNonVeg.background =
                resources.getDrawable(R.drawable.ic_veg_unselected)
        } else {
            itemType = "nonveg"
            addItemBinding.txtNonVeg.setTextColor(resources.getColor(R.color.colorWhite))
            addItemBinding.txtVeg.setTextColor(resources.getColor(R.color.colorGrey))
            addItemBinding.txtNonVeg.background =
                resources.getDrawable(R.drawable.ic_veg_selected)
            addItemBinding.txtVeg.background =
                resources.getDrawable(R.drawable.ic_veg_unselected)
        }

    }

    private fun showError(textView : EditText, error : String) {
        textView.requestFocus()
        textView.error = error
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
            itemImage = picturePath


            setImage(picturePath)
            cursor.close()
        } else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK /*&& null != categoryList*/) {
            setImage(itemImage)            // val extras = categoryList!!.extras
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
            itemImage = absolutePath
        }
    }

    private fun setImage(path : String) {
        imagesList.add(path)
        Glide.with(this).load(path).into(addItemBinding.imgView!!)
        /* if (UtilsFunctions.isNetworkConnected()) {
             trucksViewModel.addGalleryImage(
                 bannerImage
             )
         }
    */
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
}
