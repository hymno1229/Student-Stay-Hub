package com.example.messmaster

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import showAlertDialog

class SignupActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        val loginTxt = findViewById<TextView>(R.id.loginTxt)
        val signupBtn = findViewById<Button>(R.id.signupBtn)
        val nameValue = findViewById<TextInputEditText>(R.id.namevalue)
        val emailValue = findViewById<TextInputEditText>(R.id.emailValue)
        val password = findViewById<TextInputEditText>(R.id.password)

        loginTxt.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish() // Finish the current activity to prevent going back to it when pressing back
        }

        signupBtn.setOnClickListener {
            val name = nameValue.text.toString().trim()
            val email = emailValue.text.toString().trim()
            val pwd = password.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty() && pwd.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, pwd)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            // Update user profile with name
                            val profileUpdates = UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build()
                            user?.updateProfile(profileUpdates)

                            // Save user email to SharedPreferences
                            saveUserEmail(email)

                            // Add user email as a document in the /users collection
                            val db = FirebaseFirestore.getInstance()
                            db.collection("users").document(email)
                                .set(mapOf("name" to name, "email" to email))
                                .addOnSuccessListener {
                                    // Document added successfully
                                    // Navigate to MainActivity or any other activity
                                    showAlertDialog(context = this, title = "Alert", message = "Registration Successful", positiveButtonText = "OK", onPositiveButtonClick = {
                                        startActivity(Intent(this, LoginActivity::class.java))
                                        finish() // Finish the current activity to prevent going back to it when pressing back
                                    })
                                }
                                .addOnFailureListener { e ->
                                    // If adding document fails, delete the user from Authentication
                                    user?.delete()?.addOnCompleteListener {
                                        // User deleted successfully
                                        // Display a message to the user
                                        showAlertDialog(context = this, title = "Alert", message = "Failed to add user document: ${e.message}. User deleted.", positiveButtonText = "OK", onPositiveButtonClick = {})
                                    }
                                }
                        } else {
                            // If sign-in fails, display a message to the user.
                            showAlertDialog(context = this, title = "Alert", message = "Signup Failed", positiveButtonText = "OK", onPositiveButtonClick = {

                            })
                        }
                    }
            } else {
                showAlertDialog(context = this, title = "Alert", message = "Please Fill All The Details", positiveButtonText = "OK", onPositiveButtonClick = {

                })
            }
        }
    }

    private fun saveUserEmail(email: String) {
        val editor = sharedPreferences.edit()
        editor.putString("email", email)
        editor.apply()
    }
}
