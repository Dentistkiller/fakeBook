package com.fake.fakebook

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class ProfileActivity : AppCompatActivity() {

    private lateinit var avatarIV: ImageView
    private lateinit var btnSaveProfile: Button
    private lateinit var choosePic: Button
    private val PICK_IMAGE = 100
    private val CAPTURE_IMAGE = 101
    private var imageUri: Uri? = null
    private val firestoreDB = FirebaseFirestore.getInstance()
    private val firebaseStorage = FirebaseStorage.getInstance().reference
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        val userName = intent.getStringExtra("userName")
        Log.d("UsernameCheckLoggedIn","USERNAME is==>"+userName)


        avatarIV = findViewById(R.id.avatarIV)
        btnSaveProfile = findViewById(R.id.btnSaveProfile)
        bottomNav = findViewById(R.id.bottomNav)
        choosePic = findViewById(R.id.choosePic)


        // Load existing profile data
        loadProfileData()
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
                    true
                }

                R.id.upload -> {
                    val intent = Intent(this, CreatePostActivity::class.java)
                    intent.putExtra("userName", userName)
                    startActivity(intent)
                    finish()
                    true
                }

                else -> {false}
            }}

        choosePic.setOnClickListener {
            // Show image pick options
            val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            val chooserIntent = Intent.createChooser(pickIntent, "Select Image")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(takePictureIntent))

            startActivityForResult(chooserIntent, PICK_IMAGE)


        }

        btnSaveProfile.setOnClickListener {
            saveProfileData()
        }

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
                    true
                }

                R.id.upload -> {
                    val intent = Intent(this, CreatePostActivity::class.java)
                    intent.putExtra("userName", userName)
                    startActivity(intent)
                    finish()
                    true
                }

                else -> {false}
            }
        }
    }

    private fun loadProfileData() {
        firestoreDB.collection("users").document(userId).get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                // Populate your views here with document data
                val name = document.getString("name")
                val surname = document.getString("surname")
                val qualification = document.getString("qualification")

                findViewById<EditText>(R.id.tvFirstName).setText(name)
                findViewById<EditText>(R.id.tvLastName).setText(surname)
                findViewById<EditText>(R.id.tvQualification).setText(qualification)

            }
        }
    }

    private fun saveProfileData() {
        val firstName = findViewById<EditText>(R.id.tvFirstName).text.toString().trim()
        val lastName = findViewById<EditText>(R.id.tvLastName).text.toString().trim()
        val qualification = findViewById<EditText>(R.id.tvQualification).text.toString().trim()
        if (firstName.isEmpty() || lastName.isEmpty() || qualification.isEmpty() || imageUri == null) {
            Toast.makeText(this,"Fill In All Fields", Toast.LENGTH_SHORT).show()
            return
        }
        val fileName = UUID.randomUUID().toString()
        val ref = firebaseStorage.child("uploads/$userId/$fileName")
        ref.putFile(imageUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    val userProfile = hashMapOf(
                        "firstName" to firstName,
                        "lastName" to lastName,
                        "qualification" to qualification,
                        "imageUrl" to uri.toString()
                    )
                    firestoreDB.collection("users").document(userId).set(userProfile)
                        .addOnSuccessListener {
                            Toast.makeText(this,"Success", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this,"Fails", Toast.LENGTH_SHORT).show()

                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this,"Failure", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            val selectedImageUri = data?.data ?: return  // Handle if no image is selected

            // Load the selected image into the ImageView
            avatarIV.setImageURI(selectedImageUri)
        }
    }
}