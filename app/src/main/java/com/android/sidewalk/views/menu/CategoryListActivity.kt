package com.android.sidewalk.views.menu

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProviders
import com.android.sidewalk.R
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.android.sidewalk.callbacks.ChoiceCallBack
import com.android.sidewalk.common.UtilsFunctions
import com.android.sidewalk.databinding.ActivityCategoryListBinding
import com.android.sidewalk.model.CommonModel
import com.android.sidewalk.model.menu.CategoryListsResponse
import com.android.sidewalk.utils.BaseActivity
import com.android.sidewalk.utils.DialogClass
import com.android.sidewalk.utils.Utils
import com.android.sidewalk.viewmodels.menu.MenuViewModel
import com.android.sidewalk.views.trucks.GalleryActivity
import com.bumptech.glide.Glide
import com.uniongoods.adapters.CategoryListAdapter
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CategoryListActivity : BaseActivity(), ChoiceCallBack {
    private lateinit var addTruckBinding : ActivityCategoryListBinding
    private lateinit var menuViewModel : MenuViewModel
    var viewPager : ViewPager? = null
    var imagesList = ArrayList<String>()
    var truckId = ""
    var isUpdated = true

    //    var categoryList = ArrayList<CategoryListsResponse.Data>()
    private var categoryList : ArrayList<CategoryListsResponse.Data>? = null
    private val RESULT_LOAD_IMAGE = 100
    private val CAMERA_REQUEST = 1888
    private val PERMISSION_REQUEST_CODE : Int = 101
    private var confirmationDialog : Dialog? = null
    private var mDialogClass = DialogClass()
    private var catImage = ""
    var imageView : ImageView? = null
    override fun getLayoutId() : Int {
        return R.layout.activity_category_list
    }

    override fun initViews() {
        addTruckBinding = viewDataBinding as ActivityCategoryListBinding

        menuViewModel = ViewModelProviders.of(this)
            .get(MenuViewModel::class.java)
        addTruckBinding.menuViewModel = menuViewModel
        addTruckBinding.toolbarCommon.imgToolbarText.text =
            getString(R.string.add_menu)
        if (UtilsFunctions.isNetworkConnected()) {
            startProgressDialog()
        }
        //  truckId = intent.extras?.get("id") as String
        //menuViewModel.eventDetail(truckId)
        menuViewModel.getCategoryListsRes().observe(this,
            Observer<CategoryListsResponse> { addGalleryRes->
                stopProgressDialog()
                if (addGalleryRes != null) {
                    val message = addGalleryRes.message

                    if (addGalleryRes.code == 200) {
                        addTruckBinding.txtNoRecord.visibility = View.GONE
                        addTruckBinding.rvCategory.visibility = View.VISIBLE
                        if (categoryList != null) {
                            categoryList!!.clear()
                        }

                        categoryList = addGalleryRes.data
                        if (categoryList!!.size > 0) {
                            initRecyclerView()
                        }

                    } else {
                        // UtilsFunctions.showToastError(message!!)
                        addTruckBinding.txtNoRecord.visibility = View.VISIBLE
                        addTruckBinding.rvCategory.visibility = View.GONE
                    }
                }
            })


        menuViewModel.getAddCategoryRes().observe(this,
            Observer<CommonModel> { addGalleryRes->
                stopProgressDialog()
                if (addGalleryRes != null) {
                    val message = addGalleryRes.message

                    if (addGalleryRes.code == 200) {
                        if (!isUpdated) {
                            showToastSuccess(getString(R.string.category_added_success))
                        } else {
                            showToastSuccess(getString(R.string.category_updated_success))
                        }
                        menuViewModel.categoryList()
                    } else {
                        UtilsFunctions.showToastError(message!!)
                    }
                }
            })

        menuViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it : String?) {
                when (it) {
                    "rlAddCategory" -> {
                        addCategoryDialog(false, 0)
                    }
                }
            })
        )

    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        val imagesListAdapter = CategoryListAdapter(this, categoryList, this)
        addTruckBinding.rvCategory.setHasFixedSize(true)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        addTruckBinding.rvCategory.layoutManager = linearLayoutManager
        addTruckBinding.rvCategory.adapter = imagesListAdapter
        addTruckBinding.rvCategory.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView : RecyclerView, dx : Int, dy : Int) {

            }
        })

    }

    fun callGalleryActivity() {
        val intent = Intent(
            this,
            GalleryActivity::class.java
        )
        intent.putExtra("id", truckId)
        startActivity(intent)
    }

    fun addCategoryDialog(isEdit : Boolean, position : Int) {
        var categoryId = ""
        //  if (isEdit) {
        isUpdated = isEdit
        /*} else {
            isUpdated = false
        }*/
        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(this),
                R.layout.layout_add_category,
                null,
                false
            )
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.setTitle(getString(R.string.app_name))
        imageView = dialog.findViewById(R.id.imgView) as ImageView
        val uploadImage = dialog.findViewById(R.id.rlUpload) as RelativeLayout
        val edtCatname = dialog.findViewById(R.id.edtCatName) as EditText
        val dialogButton = dialog.findViewById(R.id.dialogButtonOK) as ImageView
        val btnAddCategory = dialog.findViewById(R.id.btnAddCategory) as Button
        val title = dialog.findViewById(R.id.title) as TextView
        // if button is clicked, close the custom dialog
        uploadImage.setOnClickListener {
            if (checkPersmission()) {
                confirmationDialog =
                    mDialogClass.setUploadConfirmationDialog(
                        this,
                        this,
                        "gallery"
                    )
            } else requestPermission()
        }

        dialogButton.setOnClickListener {
            dialog.dismiss()
        }
        if (isEdit) {
            categoryId = categoryList!![position].id!!
            catImage = categoryList!![position].image!!
            title.setText(
                getString(
                    R.string.update_category
                )
            )
            Glide.with(this).load(categoryList!![position].image!!).into(imageView!!)
            edtCatname.setText(categoryList!![position].name!!)
        }
        btnAddCategory.setOnClickListener {
            val catname = edtCatname.text.toString()
            //Gallery
            when {
                catImage.isEmpty() -> showToastError(
                    getString(
                        R.string.upload_img_error
                    )
                )
                catname.trim().isEmpty() -> showError(
                    edtCatname,
                    getString(R.string.empty) + " " + getString(
                        R.string.category_name
                    )
                )
                else -> {
                    val mHashMap = HashMap<String, RequestBody>()
                    mHashMap["name"] =
                        Utils(this).createPartFromString(catname)
                    mHashMap["categoryId"] =
                        Utils(this).createPartFromString(categoryId)
                    var catImagePart : MultipartBody.Part? = null

                    if (catImage.contains("http")) {
                        catImagePart = null
                    } else {
                        val f1 = File(catImage)
                        catImagePart =
                            Utils(this)
                                .prepareFilePart(
                                    "image",
                                    f1
                                )
                    }

                    if (UtilsFunctions.isNetworkConnected()) {
                        startProgressDialog()
                        menuViewModel.addCategory(
                            catImagePart,
                            mHashMap
                        )
                    }
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
/* */
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
            catImage = picturePath


            setImage(picturePath)
            cursor.close()
        } else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK /*&& null != categoryList*/) {
            setImage(catImage)            // val extras = categoryList!!.extras
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
            catImage = absolutePath
        }
    }

    private fun setImage(path : String) {
        imagesList.add(path)
        Glide.with(this).load(path).into(imageView!!)
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
