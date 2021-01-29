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
import com.android.sidewalk.databinding.ActivityProductListBinding
import com.android.sidewalk.model.CommonModel
import com.android.sidewalk.model.menu.ItemListResponse
import com.android.sidewalk.utils.BaseActivity
import com.android.sidewalk.utils.DialogClass
import com.android.sidewalk.utils.Utils
import com.android.sidewalk.viewmodels.menu.MenuViewModel
import com.android.sidewalk.views.trucks.GalleryActivity
import com.bumptech.glide.Glide
import com.uniongoods.adapters.ItemsListAdapter
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ProductListActivity : BaseActivity(), ChoiceCallBack {
    private lateinit var addTruckBinding : ActivityProductListBinding
    private lateinit var menuViewModel : MenuViewModel
    var viewPager : ViewPager? = null
    var imagesList = ArrayList<String>()
    var catId = ""
    private val RESULT_LOAD_IMAGE = 100
    private val CAMERA_REQUEST = 1888
    private val PERMISSION_REQUEST_CODE : Int = 101
    private var confirmationDialog : Dialog? = null
    private var mDialogClass = DialogClass()
    private var catImage = ""
    var imageView : ImageView? = null
    override fun getLayoutId() : Int {
        return R.layout.activity_product_list
    }

    override fun onResume() {
        super.onResume()
        if (UtilsFunctions.isNetworkConnected()) {
            startProgressDialog()
            menuViewModel.itemsList(catId)
        }

    }

    override fun initViews() {
        addTruckBinding = viewDataBinding as ActivityProductListBinding

        menuViewModel = ViewModelProviders.of(this)
            .get(MenuViewModel::class.java)
        addTruckBinding.menuViewModel = menuViewModel

        catId = intent.extras?.get("id") as String
        val name = intent.extras?.get("name") as String

        addTruckBinding.toolbarCommon.imgToolbarText.text = name

        menuViewModel.getItemListsRes().observe(this,
            Observer<ItemListResponse> { addGalleryRes->
                stopProgressDialog()
                if (addGalleryRes != null) {
                    val message = addGalleryRes.message

                    if (addGalleryRes.code == 200) {
                        addTruckBinding.txtNoRecord.visibility = View.GONE
                        addTruckBinding.rvItems.visibility = View.VISIBLE

                        initRecyclerView(addGalleryRes.data)

                    } else {
                        UtilsFunctions.showToastError(message!!)
                        addTruckBinding.txtNoRecord.visibility = View.VISIBLE
                        addTruckBinding.rvItems.visibility = View.GONE
                    }
                }
            })


        menuViewModel.getAddCategoryRes().observe(this,
            Observer<CommonModel> { addGalleryRes->
                stopProgressDialog()
                if (addGalleryRes != null) {
                    val message = addGalleryRes.message

                    if (addGalleryRes.code == 200) {
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
                        val intent = Intent(
                            this,
                            AddItemActivity::class.java
                        )
                        intent.putExtra("id", catId)
                        intent.putExtra("itemId", "")
                        startActivity(intent)
                        // addCategoryDialog()
                    }
                }
            })
        )

    }

    private fun initRecyclerView(data : ArrayList<ItemListResponse.Data>?) {
        val linearLayoutManager = LinearLayoutManager(this)
        val imagesListAdapter = ItemsListAdapter(this, data, this)
        addTruckBinding.rvItems.setHasFixedSize(true)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        addTruckBinding.rvItems.layoutManager = linearLayoutManager
        addTruckBinding.rvItems.adapter = imagesListAdapter
        addTruckBinding.rvItems.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView : RecyclerView, dx : Int, dy : Int) {

            }
        })

    }

    fun callEditItemActivity(id : String?) {
        val intent = Intent(
            this,
            AddItemActivity::class.java
        )
        intent.putExtra("id", catId)
        intent.putExtra("itemId", id)
        startActivity(intent)
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
