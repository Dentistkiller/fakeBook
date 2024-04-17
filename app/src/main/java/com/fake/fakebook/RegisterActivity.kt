package com.fake.fakebook

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import java.util.Date
import java.util.Locale

class RegisterActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var dateOfBirth: EditText
    private lateinit var usernameEt: EditText
    private lateinit var registerButton: Button
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)


        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        dateOfBirth = findViewById(R.id.etDOB)
        usernameEt = findViewById(R.id.etUserame)
        registerButton = findViewById(R.id.registerButton)
        auth = FirebaseAuth.getInstance()


        dateOfBirth.setOnClickListener{
            showDatePickerDialog()
        }

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()
            val dob = dateOfBirth.text.toString().trim()
            val username = usernameEt.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (password == confirmPassword) {
                    registerUser(email, password)
                    createProfile(email, username, dob)
                    Toast.makeText(this,"User created sucessfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter email, password, and confirm password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Registration successful, navigate to the login screen
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                    // If registration fails, display a message to the user.
                    Log.d("register", "Registration failed. ${task.exception?.message}")
                }
            }
    }

    private fun createProfile(email: String, username: String, dob: String){
        if (email.isNotEmpty() && username.isNotEmpty() && dob.isNotEmpty()) {
            val profile = hashMapOf(
                "email" to email,
                "username" to username,
                "dob" to dob,
            )
            db.collection("users").document(username).set(profile)
                .addOnSuccessListener { documentReference ->
                    Log.d("Profile Creation","Successfully created profile")
                    //db.collection("users").document(username).collection("posts").add(username)
                }
                .addOnFailureListener { e ->
                    Log.d("Profile Creation","Failed when creating profile")
                }

        }
    }

    fun loginButton(view: View) {
        val loginBtn = Intent(this@RegisterActivity, LoginActivity::class.java)
        startActivity(loginBtn)
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            // Format the date chosen by the user (MM/dd/yyyy).
            val selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", selectedMonth + 1, selectedDay, selectedYear)
            dateOfBirth.setText(selectedDate)
        }, year, month, day)

        datePickerDialog.show()
    }
}