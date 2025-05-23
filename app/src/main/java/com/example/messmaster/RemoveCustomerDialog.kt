package com.example.messmaster

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.DialogFragment
import com.example.messmaster.NotificationUtils.showNotification
import com.example.messmaster.databinding.FragmentRemoveCustomerDialogBinding
import com.example.messmaster.databinding.FragmentUpdateCustomerDialogBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import showAlertDialog
import showConfirmationDialog

class RemoveCustomerDialog : DialogFragment() {
    private  lateinit var binding: FragmentRemoveCustomerDialogBinding
    private lateinit var firestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRemoveCustomerDialogBinding.inflate(inflater,container,false)
        binding.nameValue.isEnabled = false
        binding.planSpinner.isEnabled = false
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent) // Make the background transparent
        firestore = FirebaseFirestore.getInstance()
        binding.searchIcon.setOnClickListener {
            val mobileNo = binding.cusmobileValue.text.toString()
            if (mobileNo.isNotBlank()) {
                fetchCustomerData(mobileNo)
            } else {
                // Handle case when mobile number is not entered
                showAlertDialog(context = requireContext(), title = "Alert", message = "Mobile number is empty", positiveButtonText = "OK", onPositiveButtonClick = {

                })
            }
        }
        fetchPlanValues()

        binding.removeBtn.setOnClickListener {
            val mob = binding.cusmobileValue.text.toString()
            val name = binding.nameValue.text.toString()
            showConfirmationDialog(
                requireContext(),
                "Confirmation",
                "Are you sure you want to remove this customer?",
                "Yes",
                "No",
                {
                    //if yes
                    if (mob.isNotEmpty() && mob.length >= 10 && name.isNotEmpty()) {
                        deleteCustomerDataByMobileNumber(mob)
                    } else { showAlertDialog(context = requireContext(), title = "Alert", message = "Enter Valid Mobile Number and Fetch data first", positiveButtonText = "OK", onPositiveButtonClick = {})
                    }
                },
                {
                    // if no
                }
            )
        }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val removeBtn = binding.removeBtn
        val cancelBtn = binding.cancelBtn
        cancelBtn.setOnClickListener {
            dismiss()
        }
    }

    private fun fetchPlanValues() {
        // Get current user's email
        val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: return

        // Construct the collection reference
        val collectionRef = firestore.collection("users").document(userEmail).collection("plans")

        // Fetch documents from Firestore
        collectionRef.get().addOnSuccessListener { documents ->
            val planValues = mutableListOf<String>()
            planValues.add(0,"Select")
            // Iterate through each document
            for (document in documents) {
                // Get plan name field
                val planName = document.getString("name")
                planName?.let { planValues.add(it) }
            }

            // Populate the spinner with fetched plan values
            populateSpinner(planValues)
        }.addOnFailureListener { exception ->
            // Handle any errors
            // For example, you can log the error
            showAlertDialog(context = requireContext(), title = "Alert", message = "Error fetching plan values: $exception", positiveButtonText = "OK", onPositiveButtonClick = {

            })
        }
    }

    private fun populateSpinner(planValues: List<String>) {
        val spinnerAdapter = AddPlanDialog.CustomSpinnerAdapter(requireContext(),
            R.layout.simple_spinner_item, planValues.toTypedArray())
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.planSpinner.adapter = spinnerAdapter
    }

    private fun fetchCustomerData(mobileNo: String) {
        // Get current user's email
        val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: return

        // Construct the document reference for the customer based on mobile number
        val documentRef = firestore.collection("users").document(userEmail)
            .collection("customers").document(mobileNo)

        // Fetch document from Firestore
        documentRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                // Extract customer data
                val customerName = document.getString("customerName")
                val planName = document.getString("plan")

                // Update UI with fetched data
                binding.nameValue.setText(customerName)
                if (planName != null) {
                    binding.planSpinner.setSelection(getPlanIndex(planName))
                    showAlertDialog(context = requireContext(), title = "Alert", message = "Data Fetched Successfully.", positiveButtonText = "OK", onPositiveButtonClick = {

                    })
                } else {
                    showAlertDialog(context = requireContext(), title = "Alert", message = "Plan name is null for customer with mobile number $mobileNo", positiveButtonText = "OK", onPositiveButtonClick = {

                    })
                }
            } else {
                // Handle case when customer document does not exist
                showAlertDialog(context = requireContext(), title = "Alert", message = "Customer with mobile number $mobileNo does not exist", positiveButtonText = "OK", onPositiveButtonClick = {

                })
            }
        }.addOnFailureListener { exception ->
            // Handle any errors
            // For example, you can log the error
            showAlertDialog(context = requireContext(), title = "Alert", message = "Error fetching customer data: $exception", positiveButtonText = "OK", onPositiveButtonClick = {

            })
        }
    }

    private fun getPlanIndex(planName: String?): Int {
        // Return the index of the plan name in the spinner adapter
        val adapter = binding.planSpinner.adapter
        if (adapter is AddPlanDialog.CustomSpinnerAdapter) {
            return adapter.getPosition(planName)
        }
        return -1
    }

    private fun deleteCustomerDataByMobileNumber(mobileNo: String) {
        // Get current user's email
        val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: return

        // Construct the document reference for the customer based on mobile number
        val documentRef = firestore.collection("users").document(userEmail)
            .collection("customers").document(mobileNo)

        // Delete the document from Firestore
        documentRef.delete()
            .addOnSuccessListener {
                // Handle deletion success
                showAlertDialog(context = requireContext(), title = "Alert", message = "Data deleted successfully", positiveButtonText = "OK", onPositiveButtonClick = {
                    showNotification(requireContext(), "Data Deleted Successfully.", "Customer With Mobile No: \"$mobileNo\" Deleted Successfully")
                    dismiss()
                })
            }
            .addOnFailureListener { exception ->
                // Handle deletion failure
                showAlertDialog(context = requireContext(), title = "Alert", message = "Error deleting customer data: $exception", positiveButtonText = "OK", onPositiveButtonClick = {
                    showNotification(requireContext(), "Data Deletion Unsuccessful.", "Something went wrong while deleting data")
                })
            }
    }
}