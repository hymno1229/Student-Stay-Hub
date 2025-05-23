package com.example.messmaster

import android.R
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.messmaster.databinding.FragmentUpdateCustomerDialogBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import showAlertDialog


class UpdateCustomerDialog : DialogFragment() {
    private lateinit var binding: FragmentUpdateCustomerDialogBinding
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentUpdateCustomerDialogBinding.inflate(inflater,container,false)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent) // Make the background transparent
        val customerName = binding.nameValue
        val plans = binding.planSpinner
        val mobileno = binding.cusmobileValue
        customerName.isEnabled = false
        plans.isEnabled = false

        firestore = FirebaseFirestore.getInstance()

        // Fetch plan values from Firestore
        fetchPlanValues()
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
        binding.updateBtn.setOnClickListener {
            val mobileNo = binding.cusmobileValue.text.toString()
            val newName = binding.nameValue.text.toString()
            val newPlan = binding.planSpinner.selectedItem.toString()

            if (mobileNo.isNotBlank() && newName.isNotBlank() && newPlan.isNotBlank() && binding.planSpinner.selectedItem != "Select") {
                updateCustomerData(mobileNo, newName, newPlan)
            } else {
                showAlertDialog(context = requireContext(), title = "Alert", message = "Please enter valid data", positiveButtonText = "OK", onPositiveButtonClick = {

                })
            }
        }
        return binding.root
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val cancelBtn = binding.cancelBtn

        cancelBtn.setOnClickListener {
            dismiss()
        }
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
                binding.nameValue.isEnabled = true
                if (planName != null) {
                    binding.planSpinner.setSelection(getPlanIndex(planName))
                    binding.planSpinner.isEnabled = true
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

    private fun updateCustomerData(mobileNo: String, newName: String, newPlan: String) {
        // Get current user's email
        val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: return

        // Construct the document reference for the customer based on mobile number
        val documentRef = firestore.collection("users").document(userEmail)
            .collection("customers").document(mobileNo)

        // Fetch document from Firestore
        documentRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                // Extract existing customer data
                val oldName = document.getString("customerName")
                val oldPlan = document.getString("planName")

                // Check if data has changed
                if (oldName != newName || oldPlan != newPlan) {
                    // Data has changed, update Firestore
                    documentRef.update("customerName", newName, "planName", newPlan)
                        .addOnSuccessListener {
                            showAlertDialog(context = requireContext(), title = "Alert", message = "Data updated successfully", positiveButtonText = "OK", onPositiveButtonClick = {
                                NotificationUtils.showNotification(
                                    requireContext(),
                                    "Data updated successfully",
                                    "Customer Data With Name $mobileNo Updated Successfully"
                                )
                            })
                        }.addOnFailureListener { exception ->
                            showAlertDialog(context = requireContext(), title = "Alert", message = "Error updating customer data: $exception", positiveButtonText = "OK", onPositiveButtonClick = {
                                NotificationUtils.showNotification(
                                    requireContext(),
                                    "Error updating customer data",
                                    "Customer Data With Name $mobileNo Not Updated"
                                )
                            })
                        }
                } else {
                    // Data has not changed
                    showAlertDialog(context = requireContext(), title = "Alert", message = "Data not changed", positiveButtonText = "OK", onPositiveButtonClick = {

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

}