package com.fake.fakebook

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.type.DateTime
import java.time.LocalDate
import java.util.Date
import java.util.UUID
import kotlin.math.log

class CreatePostActivity : AppCompatActivity() {

    private lateinit var storage: FirebaseStorage
    private lateinit var firestore: FirebaseFirestore
    private lateinit var chosenImageUri: Uri
    private lateinit var btnChooseImage : Button
    private lateinit var btnUploadImage : Button
    private lateinit var imgPreview : ImageView
    private lateinit var etCaption : EditText
    lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_post)

        val userName = intent.getStringExtra("userName")
        Log.d("UsernameCheckLoggedIn", "USERNAME is==> $userName")

        storage = FirebaseStorage.getInstance()
        firestore = FirebaseFirestore.getInstance()

        btnChooseImage = findViewById(R.id.btn_choose_image)
        btnUploadImage = findViewById(R.id.btn_upload_image)
        imgPreview = findViewById(R.id.img_preview)
        etCaption = findViewById(R.id.etCaption)
        bottomNav = findViewById(R.id.bottomNav)

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("userName", userName)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.putExtra("userName", userName)
                    startActivity(intent)
                    finish()
                    true }
                R.id.upload -> {
                    val intent = Intent(this, CreatePostActivity::class.java)
                    intent.putExtra("userName", userName)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> {false}
            }}
        btnChooseImage.setOnClickListener {
            // Code to open gallery and pick an image
            pickImageFromGallery()
        }

        btnUploadImage.setOnClickListener {
            if (::chosenImageUri.isInitialized) {
                val storageRef =
                    storage.reference.child("images/${UUID.randomUUID()}") // Ensure the path is unique for each image
                storageRef.putFile(chosenImageUri).addOnSuccessListener { snapshot ->
                    // After a successful upload, get the download URL
                    snapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString() // This is the URL you want to save
                        val likes = "0"
                        val hates = "0"
                        val date = LocalDate.now().toString()
                        val caption = etCaption.text.toString()
                        val username = userName ?: "Unknown" // Handle null case
                        saveImageUrlInFirestore(downloadUrl, username, caption, date, hates, likes)
                        Log.d(
                            "saveImageUrlInFirestore",
                            "Image upload successful, handle success case"
                        )
                    }
                }.addOnFailureListener { exception ->
                    Log.d("saveImageUrlInFirestore", "Image upload failed, exception: $exception")
                }
            } else {
                Log.d("image upload", "No image detected")
            }
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_IMAGE_PICKER)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMAGE_PICKER && resultCode == RESULT_OK) {
            chosenImageUri = data?.data!!
            imgPreview.setImageURI(chosenImageUri)
        }
    }

    private fun saveImageUrlInFirestore(imageUrl: String, username: String, caption: String, date: String, hates: String, likes: String) {
        val imageData = hashMapOf(
            "imageUrl" to imageUrl,
            "username" to username,
            "caption" to caption,
            "date" to date,
            "hates" to hates,
            "likes" to likes)
        firestore.collection("users") // Replace with your collection name
            .add(imageData)
            .addOnSuccessListener { documentReference ->
                Log.d("saving to firestore","Image URL and other data saved in Firestore")
            }
            .addOnFailureListener { exception ->
                Log.d("saving to firestore","Image URL and other data saved in Firestore")
            }
    }
    companion object {
        private const val REQUEST_CODE_IMAGE_PICKER = 100
    }
}