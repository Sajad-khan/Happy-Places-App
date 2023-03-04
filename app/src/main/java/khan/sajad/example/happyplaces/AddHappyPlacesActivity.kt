package khan.sajad.example.happyplaces

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import khan.sajad.example.happyplaces.database.HappyPlaceEntity
import khan.sajad.example.happyplaces.databinding.ActivityAddHappyPlacesBinding
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class AddHappyPlacesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddHappyPlacesBinding
    private var calender = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private var happyPlaceLocation: String? = null
    private var fromMainActivity: Boolean = false

    companion object{
        private const val GALLERY_REQUEST_CODE = 1
        private const val CAMERA_REQUEST_CODE = 2
        private const val IMAGE_DIRECTORY = "HappyPlacesImages"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Add Happy Place"
        binding = ActivityAddHappyPlacesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val dao = (application as HappyPlacesApp).database.happyPlaceDao()

        var happyPlaceFromRecyclerView: HappyPlaceEntity? = null
        if(intent.hasExtra(MainActivity.HAPPY_PLACE)){
            happyPlaceFromRecyclerView = intent.getSerializableExtra(MainActivity.HAPPY_PLACE) as HappyPlaceEntity
        }
        if(happyPlaceFromRecyclerView != null){
            fromMainActivity = true
            title = "Edit Happy Place"
            setUpDataFromRecyclerView(happyPlaceFromRecyclerView)
        }

        dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calender.set(Calendar.YEAR, year)
            calender.set(Calendar.MONTH, month)
            calender.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val myFormat = "dd/MM/yyyy"
            binding.inputDate.setText(SimpleDateFormat(myFormat, Locale.UK).format(calender.time))
        }

        binding.inputDate.setOnClickListener {
            DatePickerDialog(this@AddHappyPlacesActivity, dateSetListener,
                calender.get(Calendar.YEAR),
                calender.get(Calendar.MONTH),
                calender.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.btnAddImage.setOnClickListener {
            val imageSelectionDialog = AlertDialog.Builder(this)
            imageSelectionDialog.setTitle("Select Action")
            val imageDialogItems = arrayOf("Select photo from gallery", "Capture with camera")
            imageSelectionDialog.setItems(imageDialogItems){
                _, which ->
                when(which){
                    0 -> choosePhotoFromGallery()
                    1 -> captureNewPhoto()
                }
            }
            imageSelectionDialog.show()
        }

        binding.btnSave.setOnClickListener {
            if(fromMainActivity){
                if(binding.inputDate.text?.isNotEmpty() == true && binding.inputTitle.text?.isNotEmpty() ==true
                    && happyPlaceLocation != null
                    && binding.inputDescription.text?.isNotEmpty()  == true
                    && binding.inputTitle.text?.isNotEmpty() == true) {
                    try{
                        lifecycleScope.launch {
                            if (happyPlaceFromRecyclerView != null) {
                                dao.delete(happyPlaceFromRecyclerView)
                            }
                        }
                        lifecycleScope.launch{
                            dao.insert(HappyPlaceEntity(id=happyPlaceFromRecyclerView!!.id,
                                title=binding.inputTitle.text.toString(),
                                imageLocation = happyPlaceLocation!!,
                                description = binding.inputDescription.text.toString(),
                                date = binding.inputDate.text.toString(),
                                location = binding.inputLocation.text.toString(),
                                longitude = 10.0,
                                latitude = 11.0))
                        }
                        Toast.makeText(this@AddHappyPlacesActivity, "Happy place edited!", Toast.LENGTH_SHORT).show()
                        removeInputValues()
                    }
                    catch (e: Exception){
                        e.printStackTrace()
                        Log.e("MSG", "unable to add the happy place to database")
                    }
                }
                else{
                    Toast.makeText(this, "Please fill all the fields!", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                if(binding.inputDate.text?.isNotEmpty() == true && binding.inputTitle.text?.isNotEmpty() ==true
                    && happyPlaceLocation != null
                    && binding.inputDescription.text?.isNotEmpty()  == true
                    && binding.inputTitle.text?.isNotEmpty() == true) {
                    try{
                        lifecycleScope.launch{
                            dao.insert(HappyPlaceEntity(title=binding.inputTitle.text.toString(),
                                imageLocation = happyPlaceLocation!!,
                                description = binding.inputDescription.text.toString(),
                                date = binding.inputDate.text.toString(),
                                location = binding.inputLocation.text.toString(),
                                longitude = 10.0,
                                latitude = 11.0))
                        }
                        Toast.makeText(this@AddHappyPlacesActivity, "Happy place added!", Toast.LENGTH_SHORT).show()
                        removeInputValues()
                    }
                    catch (e: Exception){
                        e.printStackTrace()
                        Log.e("MSG", "unable to add the happy place to database")
                    }
                }
                else{
                    Toast.makeText(this, "Please fill all the fields!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setUpDataFromRecyclerView(happyPlaceFromRecyclerView: HappyPlaceEntity) {
        binding.inputDate.setText(happyPlaceFromRecyclerView.date)
        binding.inputTitle.setText(happyPlaceFromRecyclerView.title)
        binding.inputDescription.setText(happyPlaceFromRecyclerView.description)
        binding.inputLocation.setText(happyPlaceFromRecyclerView.location)
        Glide.with(this).load(happyPlaceFromRecyclerView.imageLocation).into(binding.ivSelectedImage)
        happyPlaceLocation = happyPlaceFromRecyclerView.imageLocation
    }

    private fun removeInputValues() {
        binding.inputDate.text?.clear()
        binding.inputTitle.text?.clear()
        binding.inputDescription.text?.clear()
        binding.inputLocation.text?.clear()
        binding.ivSelectedImage.setImageResource(R.drawable.ic_image)
        happyPlaceLocation = null

        binding.inputTitle.clearFocus()
        binding.inputDate.clearFocus()
        binding.inputDescription.clearFocus()
        binding.inputLocation.clearFocus()
    }

    private fun captureNewPhoto() {
        Dexter.withContext(this).withPermission(
            android.Manifest.permission.CAMERA
        ).withListener(object: PermissionListener {
            override fun onPermissionGranted(response: PermissionGrantedResponse) {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
            }
            override fun onPermissionDenied(response: PermissionDeniedResponse) {
                showRationaleDialogForPermissions()
            }
            override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest,
                                                            token: PermissionToken) {
                showRationaleDialogForPermissions()
            }
        }).onSameThread().check()
    }

    private fun showRationaleDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage("Permission is required. You can enable it at Settings/HappyPlaces/Permissions")
            .setPositiveButton("Settings"){
                _, _ -> try{
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException){
                    e.printStackTrace()
                }
            }.setNegativeButton("Cancel"){
                dialog, _ ->
                    dialog.dismiss()
            }.show()
    }

    private fun choosePhotoFromGallery() {
        Dexter.withContext(this).withPermission(
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ).withListener(object: PermissionListener {
            override fun onPermissionGranted(response: PermissionGrantedResponse) {
                val galleryIntent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
            }
            override fun onPermissionDenied(response: PermissionDeniedResponse) {
                showRationaleDialogForPermissions()
            }
            override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest,
                                                            token: PermissionToken) {
                showRationaleDialogForPermissions()
            }
        }).check()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == GALLERY_REQUEST_CODE){
                if(data != null){
                    val contentUri = data.data
                    try {
                        val selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentUri)
                        val result = addImageToInternalStorage(selectedImageBitmap)
                        happyPlaceLocation = result
                        Log.e("Saved Image", "MSG : $result")
                        binding.ivSelectedImage.setImageBitmap(selectedImageBitmap)
                    }catch (e: IOException){
                        e.printStackTrace()
                        Toast.makeText(this@AddHappyPlacesActivity,
                        "Failed to load the image",
                        Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else if(requestCode == CAMERA_REQUEST_CODE){
                val imageBitmap = data!!.extras!!.get("data") as Bitmap
                val ans = addImageToInternalStorage(imageBitmap)

                happyPlaceLocation = ans
                Log.e("Saved Camera Image", "Message: $ans")
                binding.ivSelectedImage.setImageBitmap(imageBitmap)
            }
        }
    }

    private fun addImageToInternalStorage(bitmap: Bitmap): String{
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")
        try{
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        }catch (e: IOException){
            e.printStackTrace()
        }
        return file.absolutePath
    }
}