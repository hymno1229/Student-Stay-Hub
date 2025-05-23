package com.example.messmaster

import android.R
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.messmaster.databinding.FragmentAddPlanDialogBinding
import com.example.messmaster.databinding.FragmentRemovePlanDialogBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import showAlertDialog
import showConfirmationDialog

class RemovePlanDialog : DialogFragment() {
    private  lateinit var binding: FragmentRemovePlanDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRemovePlanDialogBinding.inflate(inflater,container,false)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent) // Make the background transparent
        binding.validityValue.isEnabled = false
        binding.planPriceValue.isEnabled = false
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val removebtn = binding.removeBtn
        val cancelBtn = binding.cancelBtn
        val plansSpinner = binding.planNameValue
        val planPrice = binding.planPriceValue
        val planValidity = binding.validityValue
        val validities = arrayOf("Select", "15 Days", "30 Days")
        val spinnerAdapter = AddPlanDialog.CustomSpinnerAdapter(requireContext(),
            R.layout.simple_spinner_item,validities)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        planValidity.adapter = spinnerAdapter

        val db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userEmail = currentUser?.email

        if (userEmail != null) {
            db.collection("users")
                .document(userEmail)
                .collection("plans")
                .get()
                .addOnSuccessListener { documents ->
                    val planNames = mutableListOf<String>()
                    for (document in documents) {
                        planNames.add(document.id)
                    }
                    // Add "Select" as the first item in the spinner
                    planNames.add(0, "Select")

                    val spinnerAdapter = AddPlanDialog.CustomSpinnerAdapter(
                        requireContext(),
                        R.layout.simple_spinner_item,
                        planNames.toTypedArray()
                    )
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    plansSpinner.adapter = spinnerAdapter
                }
                .addOnFailureListener { exception ->
                    showAlertDialog(
                        context = requireContext(),
                        title = "Alert",
                        message = "Error getting document names",
                        positiveButtonText = "OK",
                        onPositiveButtonClick = {
                            dismiss()
                        }
                    )
                }
        }
        plansSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position != 0) { // Check if a plan other than "Select" is selected
                    val selectedPlanName = plansSpinner.getItemAtPosition(position).toString()
                    // Retrieve plan details from Firestore based on selected plan name
                    if (userEmail != null) {
                        db.collection("users")
                            .document(userEmail)
                            .collection("plans")
                            .document(selectedPlanName)
                            .get()
                            .addOnSuccessListener { documentSnapshot ->
                                val price = documentSnapshot.getString("price")
                                val validity = documentSnapshot.getString("validity")
                                // Update planPrice and planValidity fields
                                planPrice.setText(price)
                                val validityPosition = validities.indexOf(validity)
                                if (validityPosition != -1) {
                                    planValidity.setSelection(validityPosition)
                                }
                            }
                            .addOnFailureListener { exception ->
                                showAlertDialog(context = requireContext(), title = "Alert", message = "Error getting plan details", positiveButtonText = "OK", onPositiveButtonClick = {
                                    dismiss()
                                })
                            }
                    }
                } else {
                    // Reset planPrice and planValidity fields if "Select" is selected
                    planPrice.setText("")
                    planValidity.setSelection(0)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle case when nothing is selected (optional)
            }
        }

        removebtn.setOnClickListener {
            showConfirmationDialog(requireContext(), "Confirmation", "Are you sure you want to delete this plan?", "Yes", "No",
                {
                    // Positive action (e.g., perform delete operation)
                    val selectedPlanPosition = plansSpinner.selectedItemPosition
                    val selectedPlanName = plansSpinner.getItemAtPosition(selectedPlanPosition).toString()

                    if (selectedPlanPosition != 0 && selectedPlanName != "Select") {
                        // Retrieve the existing plan details from Firestore
                        if (userEmail != null) {
                            db.collection("users")
                                .document(userEmail)
                                .collection("plans")
                                .document(selectedPlanName)
                                .delete()
                                .addOnSuccessListener {
                                    showAlertDialog(
                                        context = requireContext(),
                                        title = "Success",
                                        message = "Plan deleted successfully",
                                        positiveButtonText = "OK",
                                        onPositiveButtonClick = {
                                            NotificationUtils.showNotification(
                                                requireContext(),
                                                "Plan removed successfully",
                                                "Plan With Name $selectedPlanName removed Successfully"
                                            )
                                            dismiss()
                                        }
                                    )
                                }
                                .addOnFailureListener { exception ->
                                    showAlertDialog(
                                        context = requireContext(),
                                        title = "Alert",
                                        message = "Error deleting plan: ${exception.message}",
                                        positiveButtonText = "OK",
                                        onPositiveButtonClick = {
                                            NotificationUtils.showNotification(
                                                requireContext(),
                                                "Error deleting plan",
                                                "Plan With Name $selectedPlanName Not Removed"
                                            )
                                            dismiss()
                                        }
                                    )
                                }
                        }
                    } else {
                        // Show alert dialog to select a valid plan
                        showAlertDialog(
                            context = requireContext(),
                            title = "Alert",
                            message = "Please select a valid plan",
                            positiveButtonText = "OK",
                            onPositiveButtonClick = {}
                        )
                    }
                },
                {
                // if no clicked?

                }
            )
        }

        cancelBtn.setOnClickListener {
            dismiss()
        }
    }
}