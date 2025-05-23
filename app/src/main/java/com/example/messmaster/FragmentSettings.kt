package com.example.messmaster

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.messmaster.databinding.FragmentSettingsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import showConfirmationDialog


class FragmentSettings : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentSettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val currentUser = auth.currentUser
        val profile_name = binding.profileName
        val email = binding.emailLabel
        currentUser?.let {
            val displayName = it.displayName
            profile_name.text = displayName?.uppercase()
            val emailID = it.email
            email.setText(emailID)
        }
        val logout = binding.logoutBtn
        logout.setOnClickListener {
            val alertDialogBuilder = MaterialAlertDialogBuilder(
                requireContext(),
                R.style.CustomAlertDialogTheme
            ) // Use custom theme here
            alertDialogBuilder.apply {
                setTitle("Logout")
                setMessage("Are you sure you want to log out?")
                setPositiveButton("Yes") { dialog, _ ->
                    // Perform logout action here
                    FirebaseAuth.getInstance().signOut()
                    // After logout, navigate the user to the login screen or any other appropriate screen
                    startActivity(Intent(requireContext(), LoginActivity::class.java))
                    // Finish the current activity to prevent the user from returning to it via the back button
                    requireActivity().finish()
                    dialog.dismiss() // Dismiss the dialog
                }
                setNegativeButton("No") { dialog, _ ->
                    // User clicked No, do nothing
                    dialog.dismiss() // Dismiss the dialog
                }
            }

            // Access the buttons and set their text color and background tint
            val alertDialog = alertDialogBuilder.create()
            alertDialog.setOnShowListener {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.apply {
                    setTextColor(Color.WHITE)
                    backgroundTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.primaryColor)) // Set your desired color

                    // Set margin only to the left side
                    val layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    layoutParams.setMargins(40, 0, 0, 0) // Adjust left margin in pixels as needed
                    this.layoutParams = layoutParams
                }
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.apply {
                    setTextColor(Color.WHITE)
                    backgroundTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.primaryColor)) // Set your desired color
                }
            }
            alertDialog.show()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val notification = binding.notificationToggle
        val datasaver = binding.datasaverToggle
        val changepassword = binding.changepassIcon
        val resetapp = binding.resetIcon
        var isDToggleOn = false

        // Initialize SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Check the current state from SharedPreferences
        var isNToggleOn = sharedPreferences.getBoolean("notification_enabled", false)
        var isDataSaverOn = sharedPreferences.getBoolean("data_saver_enabled", false)

        // Set initial state of the notification toggle icon
        notification.setImageResource(if (isNToggleOn) R.drawable.toggle_on else R.drawable.toggle_off)
        binding.datasaverToggle.setImageResource(if (isDataSaverOn) R.drawable.toggle_on else R.drawable.toggle_off)

        // Set OnClickListener for the notification image
        notification.setOnClickListener {
            // Toggle the state
            isNToggleOn = !isNToggleOn

            // Update the image resource based on the new state
            notification.setImageResource(if (isNToggleOn) R.drawable.toggle_on else R.drawable.toggle_off)

            // Save the state in SharedPreferences
            sharedPreferences.edit().putBoolean("notification_enabled", isNToggleOn).apply()
        }
        fun enableDataSaver(context: Context) {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // For Android Q (API 29) and above
                connectivityManager.bindProcessToNetwork(null)
            } else {
                // For Android versions below Q
                connectivityManager.restrictBackgroundStatus
            }

            // You can add more logic here to further optimize data usage,
            // such as reducing image quality, prefetching data, etc.
        }

        fun disableDataSaver(context: Context) {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // For Android Q (API 29) and above
                connectivityManager.bindProcessToNetwork(null)
            } else {
                // For Android versions below Q
                connectivityManager.restrictBackgroundStatus
            }

            // You can add more logic here to restore default data usage settings,
            // such as restoring image quality, stopping prefetching, etc.
        }

        // OnClickListener implementation
        binding.datasaverToggle.setOnClickListener {
            // Toggle the state
            isDataSaverOn = !isDataSaverOn

            // Update the image resource based on the new state
            binding.datasaverToggle.setImageResource(if (isDataSaverOn) R.drawable.toggle_on else R.drawable.toggle_off)

            // Save the state in SharedPreferences
            sharedPreferences.edit().putBoolean("data_saver_enabled", isDataSaverOn).apply()

            // Enable or disable data saver based on the state
            if (isDataSaverOn) {
                enableDataSaver(requireContext())
            } else {
                disableDataSaver(requireContext())
            }
        }

        changepassword.setOnClickListener() {
            startActivity(Intent(requireContext(), ChangePassword::class.java))
        }

        resetapp.setOnClickListener {
            showConfirmationDialog(
                requireContext(),
                "Confirmation",
                "Are you sure you want to perform this action?",
                "Yes",
                "No",
                positiveAction = {
                    // Positive action (e.g., perform delete operation)

                    clearAllDataAndSignOut()
                },
                {
                    // Negative action (e.g., do nothing)
                },
            )
        }
    }
    private fun clearAllDataAndSignOut() {
        // Sign out the current user
        auth.signOut()

        // Clear all SharedPreferences data
        clearAllSharedPreferences()

        // Navigate to the login activity
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)

        // Finish the current activity
        requireActivity().finish()
    }

    private fun clearAllSharedPreferences() {
        val sharedPrefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.clear()
        editor.apply()
    }
}