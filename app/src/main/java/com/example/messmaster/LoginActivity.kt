package com.example.messmaster

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import showAlertDialog

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()

        // Check if the user is already signed in
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // User is already signed in, navigate to MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish() // Finish the LoginActivity to prevent the user from returning to it when pressing the back button
        }

        val loginBtn = findViewById<Button>(R.id.loginBtn)
        val signupText = findViewById<TextView>(R.id.signup)
        val email = findViewById<TextInputEditText>(R.id.emailid)
        val password = findViewById<TextInputEditText>(R.id.password)

        loginBtn.setOnClickListener {
            val userEmail = email.text.toString()
            val userPassword = password.text.toString()

            // Check if email and password fields are empty
            if (userEmail.isEmpty() || userPassword.isEmpty()) {
                // Show a toast message indicating that fields are empty
                showAlertDialog(context = this, title = "Alert", message = "Please enter both email and password", positiveButtonText = "OK", onPositiveButtonClick = {

                })
            } else {
                // Sign in with email and password
                auth.signInWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            val user = auth.currentUser
                            saveUserEmail(userEmail)
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {
                            // If sign in fails, display a message to the user.
                            showAlertDialog(context = this, title = "Alert", message = "Authentication failed.", positiveButtonText = "OK", onPositiveButtonClick = {

                            })
                        }
                    }
            }
        }

        signupText.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun saveUserEmail(email: String) {
        val sharedPreferences = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(Constants.PREF_USER_EMAIL, email)
        editor.apply()
    }

    object Constants {
        const val PREF_NAME = "MyPrefs"
        const val PREF_USER_EMAIL = "userEmail"
        // Add more keys as needed
    }
}