package com.gmcoreui.controllers.fragments

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.core.file.FileWrapper
import com.core.utils.Logger
import com.gm.R
import com.gm.db.SingleTon
import com.gmcoreui.controllers.BaseActivity
import com.gmcoreui.utils.Keys.PERMISSION_REQUEST_CAMERA
import com.gmcoreui.utils.Keys.PERMISSION_REQUEST_CAMERA_STORAGE
import com.gmcoreui.utils.PermissionUtils
import com.google.android.material.snackbar.Snackbar
import java.io.File

abstract class PhotoFragment : BaseFragment(), BottomSheetFragment.BottomSheetDataSource, BottomSheetFragment.Listener {

    private val CAMERA_PERMISSIONS = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)

    companion object {
        const val PICK_IMAGE_REQUEST = 200
        const val TAKE_IMAGE_REQUEST = 201
    }

    private var parentView: View? = null

    private fun isPermissionGranted(permission: String) =
            ActivityCompat.checkSelfPermission(context!!, permission) == PackageManager.PERMISSION_GRANTED

    private fun shouldShowPermissionRationale(permission: String) =
            ActivityCompat.shouldShowRequestPermissionRationale(activity!!, permission)

    private fun requestPermission(permission: String, requestId: Int) =
            ActivityCompat.requestPermissions(activity!!, arrayOf(permission), requestId)

    fun batchRequestPermissions(permissions: Array<String>, requestId: Int) =
            ActivityCompat.requestPermissions(activity!!, permissions, requestId)

    /**
     * Called when the 'show camera' button is clicked.
     *
     */
    fun showCamera(view: View) {
        if (view == null) {
            throw IllegalArgumentException("View is required to show error.")
        }
        parentView = view
        // Check if the Camera permission is already available.
        if (!isPermissionGranted(Manifest.permission.CAMERA)) {
            // Camera permission has not been granted.
            requestCameraPermission()
        } else {
            // Camera permissions is already available, show the camera preview.
            showCameraPreview()
        }
    }

    /**
     * Requests the Camera permission.
     * If the permission has been denied previously, a SnackBar will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    private fun requestCameraPermission() {
        if (shouldShowPermissionRationale(Manifest.permission.CAMERA)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Snackbar.make(parentView?.rootView!!,SingleTon.getResourceStringValue("message_camera_permission"),
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(SingleTon.getResourceStringValue("ok"), {
                        requestPermission(Manifest.permission.CAMERA, PERMISSION_REQUEST_CAMERA)
                    })
                    .show()
        } else {
            // Camera permission has not been granted yet. Request it directly.
            requestPermission(Manifest.permission.CAMERA, PERMISSION_REQUEST_CAMERA)
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            // Received permission result for camera permission.
            // Check if the permission has been granted
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                // Camera permission has been granted, preview can be displayed
                showCameraPreview()
            } else {
                showSnackBar(SingleTon.getResourceStringValue("error_in_permission"))
            }
        } else if (requestCode == PERMISSION_REQUEST_CAMERA_STORAGE) {
            if (grantResults.isNotEmpty() && !PermissionUtils.isGranted(grantResults)) {
                showSnackBar(SingleTon.getResourceStringValue("error_in_permission"))
            } else {
                dispatchTakePictureIntent()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    /**
     * Invoke the camera by default intent.
     */
    abstract fun showCameraPreview()

    private var outputFileUri: Uri? = null
    fun showImagePickerOptions() {
        /*val bottomSheet = BottomSheetFragment.newInstance(2)
        bottomSheet.show(activity?.supportFragmentManager, bottomSheet.tag)*/

        val builderSingle = AlertDialog.Builder(activity!!)
        builderSingle.setIcon(R.mipmap.ic_launcher)
        builderSingle.setTitle(SingleTon.getResourceStringValue("label_choose"))

        val arrayAdapter = ArrayAdapter<String>(activity!!, android.R.layout.select_dialog_singlechoice, resources.getStringArray(R.array.options_image))
        builderSingle.setNegativeButton(SingleTon.getResourceStringValue("cancel"), DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
        builderSingle.setAdapter(arrayAdapter, { dialog, which ->
            when (which) {
                0 -> {
                    pickImage()
                }
                1 -> {
                    takePhoto()
                }
            }
        })
        builderSingle.show()
    }

    override fun getLabel(index: Int): String {
        return resources.getStringArray(R.array.options_image)[index]
    }

    override fun getImageResource(index: Int): Int {
        return -1
    }

    override fun onItemClicked(position: Int) {
        when (position) {
            0 -> pickImage()
            1 -> takePhoto()
        }
    }

    fun pickImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent,SingleTon.getResourceStringValue("label_select_picture")), PICK_IMAGE_REQUEST)
    }

    fun takePhoto() {
        if (!PermissionUtils.isPermissionsGranted(activity!!, CAMERA_PERMISSIONS) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            (activity as BaseActivity).requestPermissions(CAMERA_PERMISSIONS, PERMISSION_REQUEST_CAMERA_STORAGE)
        } else {
            dispatchTakePictureIntent()
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (activity?.packageManager?.let { takePictureIntent.resolveActivity(it) } != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = FileWrapper.createImageFile(activity)
                outputFileUri = Uri.fromFile(photoFile)
            } catch (e: Exception) {
                Logger.e("addimage", e.message ?: "")
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(context!!,
                        context!!.applicationContext.packageName + ".provider", photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                takePictureIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                startActivityForResult(takePictureIntent, TAKE_IMAGE_REQUEST)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
                    outputFileUri = data?.data
                    outputFileUri?.let { setResultImage(it) }
                    /*try {
                        val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, outputFileUri)
                        // setResultImage(bitmap)
                        ImageUtils.getOutputFileUri(bitmap)?.let { setResultImage(it) }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }*/
                }
                TAKE_IMAGE_REQUEST -> {
                    outputFileUri?.let { setResultImage(it) }
                    /*this.outputFileUri?.let {
                        val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, it)
                        ImageUtils.getOutputFileUri(bitmap)?.let { it1 -> setResultImage(it1) }
                    }*/
                }
            }
        }
    }

    abstract fun setResultImage(outputFileUri: Uri)
}