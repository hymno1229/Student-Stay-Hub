package com.example.messmaster

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.messmaster.databinding.ActivityChangePasswordBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import showAlertDialog
import java.util.Properties
import java.util.Random
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


class ChangePassword : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var otp: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val emailval = findViewById<TextInputEditText>(R.id.emailValue)
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val email = currentUser?.email
        emailval.isEnabled = false
        emailval.setText(email)
        val otpval = findViewById<TextInputEditText>(R.id.otpValue)
        val newPass = findViewById<TextInputEditText>(R.id.newPassValue)
        val conPass = findViewById<TextInputEditText>(R.id.conPassValue)
        val changebtn = binding.changeBtn
        otpval.isEnabled = false
        newPass.isEnabled = false
        conPass.isEnabled = false
        changebtn.isEnabled=false
        otpval.setBackgroundResource(R.drawable.input_disabled)
        newPass.setBackgroundResource(R.drawable.input_disabled)
        conPass.setBackgroundResource(R.drawable.input_disabled)
        changebtn.setBackgroundResource(R.drawable.buttonbg_disabled)
        changebtn.setOnClickListener(){
            showAlertDialog(context = this, title = "Alert", message = "Password Changed Successfully", positiveButtonText = "OK", onPositiveButtonClick = {
                finish()
            })
        }

        val backbtn = binding.backbtn
        backbtn.setOnClickListener(){
            finish()
        }

        val changepassBtn = findViewById<Button>(R.id.changeBtn)

        changepassBtn.setOnClickListener(){
            val npass = findViewById<TextInputEditText>(R.id.newPassValue).text.toString()
            val cpass = findViewById<TextInputEditText>(R.id.conPassValue).text.toString()
            if (otpval.text.toString()!= otp){
                showAlertDialog(this,"Error","Invalid OTP")
                return@setOnClickListener
            }
            if (npass!=cpass){
                showAlertDialog(this,"Error","password not equal to confirm password")
                return@setOnClickListener
            }
            val user = auth.currentUser
            user?.updatePassword(npass)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Password updated successfully
                        showAlertDialog(this, "Success", "Password updated successfully"){
                            finish()
                        }
                    } else {
                        // Password update failed
                        showAlertDialog(this, "Error", "Failed to update password: ${task.exception?.message}")
                    }
                }

        }
        binding.generateBtn.setOnClickListener(){
            otp = generateOTP()
            sendOTPEmail(email.toString(), otp)
        }
    }

    private fun generateOTP(): String {
        val otpLength = 6
        val chars = "0123456789"
        val random = Random()
        return (1..otpLength)
            .map { chars[random.nextInt(chars.length)] }
            .joinToString("")
    }

    private fun sendOTPEmail(receiverEmail: String, otp: String) {
        val senderEmail = "your_email@gmail.com" // Replace with your Gmail address
        val password = "your_app_password" // Replace with your Gmail app-specific password

        // Move the email sending operation to a background thread using coroutines
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val props = Properties()
                props["mail.smtp.auth"] = "true"
                props["mail.smtp.starttls.enable"] = "true"
                props["mail.smtp.host"] = "smtp.gmail.com"
                props["mail.smtp.port"] = "587"

                val session = Session.getInstance(props, object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(senderEmail, password)
                    }
                })

                val message = MimeMessage(session)
                message.setFrom(InternetAddress(senderEmail,"Mess Master"))
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiverEmail))
                message.subject = "One Time Password (OTP) for Password Change"
                message.setText("Your OTP for changing password is: $otp")

                Transport.send(message)
                println("OTP sent successfully to $receiverEmail")

                // Show AlertDialog on the main thread
                runOnUiThread {
                    showAlertDialog(this@ChangePassword, "Success", "OTP sent successfully to $receiverEmail")
                    val otpval = findViewById<TextInputEditText>(R.id.otpValue)
                    val newPass = findViewById<TextInputEditText>(R.id.newPassValue)
                    val conPass = findViewById<TextInputEditText>(R.id.conPassValue)
                    val changebtn = binding.changeBtn
                    otpval.isEnabled = true
                    newPass.isEnabled = true
                    conPass.isEnabled = true
                    changebtn.isEnabled=true
                    otpval.setBackgroundResource(R.drawable.input)
                    newPass.setBackgroundResource(R.drawable.input)
                    conPass.setBackgroundResource(R.drawable.input)
                    changebtn.setBackgroundResource(R.drawable.buttonbg)                }
            } catch (e: MessagingException) {
                // Show AlertDialog on the main thread
                runOnUiThread {
                    showAlertDialog(this@ChangePassword, "Error", "Failed to send OTP.")
                }
                println("Failed to send OTP: ${e.message}")
            }
        }
    }
}





