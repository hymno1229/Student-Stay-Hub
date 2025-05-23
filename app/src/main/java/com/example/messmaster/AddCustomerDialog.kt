package com.example.messmaster

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.messmaster.databinding.FragmentAddCustomerDialogBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import showAlertDialog
import java.text.SimpleDateFormat
import java.util.*

class AddCustomerDialog : DialogFragment() {
    private lateinit var binding: FragmentAddCustomerDialogBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val userEmail = currentUser?.email

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddCustomerDialogBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawableResource(R.color.transparent) // Make the background transparent
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val addbtn = binding.addBtn
        val cancelBtn = binding.cancelBtn
        val customerName = binding.cusNameValue
        val mobilenumber = binding.mobileValue
        val plans = binding.planSpinner

        // Initialize array to store plan values
        val planValues = mutableListOf<String>()
        planValues.add(0, "Select")

        // Check if user email is not null
        if (userEmail != null) {
            // Fetch plans from Firestore
            firestore.collection("users")
                .document(userEmail)
                .collection("plans")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        // Add document names to plan values
                        planValues.add(document.id)
                    }

                    // After fetching documents, update spinner adapter
                    val spinnerAdapter = AddPlanDialog.CustomSpinnerAdapter(
                        requireContext(),
                        R.layout.simple_spinner_item,
                        planValues.toTypedArray()
                    )
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    plans.adapter = spinnerAdapter
                }
                .addOnFailureListener { exception ->
                    // Handle any errors
                    showAlertDialog(
                        context = requireContext(),
                        title = "Alert",
                        message = "Error getting documents",
                        positiveButtonText = "OK",
                        onPositiveButtonClick = {
                            dismiss()
                        }
                    )
                }
        }

        addbtn.setOnClickListener {
            val customerNameText = customerName.text.toString().trim()
            val mobileNumberText = mobilenumber.text.toString().trim()
            val selectedPlan = plans.selectedItem.toString()

            // Check if fields are empty or plan is not selected
            if (customerNameText.isEmpty() || mobileNumberText.isEmpty() || selectedPlan == "Select") {
                showAlertDialog(
                    context = requireContext(),
                    title = "Alert",
                    message = "Please fill all fields and select a plan",
                    positiveButtonText = "OK",
                    onPositiveButtonClick = {

                    }
                )
            } else {
                // Fetch validity from Firestore based on the selected plan
                fetchValidity(
                    selectedPlan,
                    onSuccess = { validity ->
                        // Calculate expiry date based on the validity
                        val expiryDate = calculateExpiryDate(validity)

                        // Get current date
                        val currentDate = getCurrentDate()

                        // Create data map
                        val data = hashMapOf(
                            "customerName" to customerNameText,
                            "mobileNumber" to mobileNumberText,
                            "plan" to selectedPlan,
                            "date" to currentDate,
                            "expiryDate" to expiryDate
                        )

                        // Add data to Firestore
                        userEmail?.let { email ->
                            firestore.collection("users")
                                .document(email)
                                .collection("customers")
                                .document(mobileNumberText)
                                .set(data)
                                .addOnSuccessListener {
                                    showAlertDialog(
                                        context = requireContext(),
                                        title = "Alert",
                                        message = "Data added successfully",
                                        positiveButtonText = "OK",
                                        onPositiveButtonClick = {
                                            NotificationUtils.showNotification(
                                                requireContext(),
                                                "Data added successfully",
                                                "Customer Data With Mobile Number $mobileNumberText Added Successfully"
                                            )
                                            dismiss()
                                        }
                                    )
                                }
                                .addOnFailureListener { exception ->
                                    showAlertDialog(
                                        context = requireContext(),
                                        title = "Alert",
                                        message = "Error adding data: ${exception.message}",
                                        positiveButtonText = "OK",
                                        onPositiveButtonClick = {
                                            NotificationUtils.showNotification(
                                                requireContext(),
                                                "Error adding data",
                                                "Customer Data With Mobile Number $mobileNumberText Not Added"
                                            )
                                        }
                                    )
                                }
                        }
                    },
                    onFailure = {
                        showAlertDialog(
                            context = requireContext(),
                            title = "Alert",
                            message = "Failed to fetch plan details",
                            positiveButtonText = "OK",
                            onPositiveButtonClick = {

                            }
                        )
                    }
                )
            }
        }

        cancelBtn.setOnClickListener {
            dismiss()
        }
    }

    private fun fetchValidity(planName: String, onSuccess: (Int) -> Unit, onFailure: () -> Unit) {
        userEmail?.let { email ->
            firestore.collection("users")
                .document(email)
                .collection("plans")
                .document(planName)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val validity = documentSnapshot.getString("validity")
                    if (validity != null) {
                        val days = parseValidityString(validity)
                        if (days != null) {
                            onSuccess(days)
                        } else {
                            onFailure()
                        }
                    } else {
                        onFailure()
                    }
                }
                .addOnFailureListener {
                    onFailure()
                }
        }
    }

    private fun parseValidityString(validity: String): Int? {
        val trimmedValidity = validity.trim()
        val daysString = trimmedValidity.split(" ")[0]
        return daysString.toIntOrNull()
    }


    private fun calculateExpiryDate(days: Int): String {
        // Calculate expiry date based on the number of days
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, days)
        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
    }

    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
    }
}
