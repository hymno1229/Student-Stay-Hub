package com.example.messmaster

import android.R
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.messmaster.databinding.FragmentAddPlanDialogBinding
import com.example.messmaster.databinding.FragmentEditPlanDialogBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import showAlertDialog

class EditPlanDialog : DialogFragment() {
    private lateinit var binding: FragmentEditPlanDialogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentEditPlanDialogBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent) // Make the background transparent
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val updatebtn = binding.updateBtn
        val cancelBtn = binding.cancelBtn
        val plansSpinner = binding.planNameValue
        val planPrice = binding.planPriceValue
        val planValidity = binding.validityValue
        val validities = arrayOf("Select", "15 Days", "30 Days")
        val spinnerAdapter = AddPlanDialog.CustomSpinnerAdapter(requireContext(),R.layout.simple_spinner_item,validities)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        planValidity.adapter = spinnerAdapter

        // Retrieve document names from Firestore and populate plansSpinner
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

        // Listen for changes on plansSpinner
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

        // Button click listeners
        updatebtn.setOnClickListener {
            val selectedPlanPosition = plansSpinner.selectedItemPosition
            val selectedValidityPosition = planValidity.selectedItemPosition
            val selectedPlanName = plansSpinner.getItemAtPosition(selectedPlanPosition).toString()
            val selectedPrice = planPrice.text.toString()
            val selectedValidity = planValidity.getItemAtPosition(selectedValidityPosition).toString()

            if (selectedPlanPosition != 0 && selectedValidityPosition != 0 && selectedPrice.isNotEmpty() && selectedPlanName != "Select") {
                // Retrieve the existing plan details from Firestore
                if (userEmail != null) {
                    db.collection("users")
                        .document(userEmail)
                        .collection("plans")
                        .document(selectedPlanName)
                        .get()
                        .addOnSuccessListener { documentSnapshot ->
                            val existingPrice = documentSnapshot.getString("price")
                            val existingValidity = documentSnapshot.getString("validity")

                            // Check if the selected values are different from the existing values
                            if (selectedPrice != existingPrice || selectedValidity != existingValidity) {
                                // Data has been changed, proceed with the update
                                db.collection("users")
                                    .document(userEmail)
                                    .collection("plans")
                                    .document(selectedPlanName)
                                    .update(
                                        mapOf(
                                            "price" to selectedPrice,
                                            "validity" to selectedValidity
                                        )
                                    )
                                    .addOnSuccessListener {
                                        showAlertDialog(context = requireContext(), title = "Success", message = "Plan updated successfully", positiveButtonText = "OK", onPositiveButtonClick = {
                                            dismiss()
                                        })
                                    }
                                    .addOnFailureListener { exception ->
                                        showAlertDialog(
                                            context = requireContext(),
                                            title = "Alert",
                                            message = "Error updating plan details: ${exception.message}",
                                            positiveButtonText = "OK",
                                            onPositiveButtonClick = {
                                                dismiss()
                                            }
                                        )
                                    }
                            } else {
                                // No data has been changed, show alert
                                showAlertDialog(
                                    context = requireContext(),
                                    title = "Alert",
                                    message = "No data has been changed",
                                    positiveButtonText = "OK",
                                    onPositiveButtonClick = {

                                    }
                                )
                            }
                        }
                        .addOnFailureListener { exception ->
                            showAlertDialog(
                                context = requireContext(),
                                title = "Alert",
                                message = "Error retrieving existing plan details: ${exception.message}",
                                positiveButtonText = "OK",
                                onPositiveButtonClick = {
                                    dismiss()
                                }
                            )
                        }
                }
            } else {
                // Show alert dialog to select plan validity, fill in the price, or select a valid plan
                if (selectedPrice.isEmpty()) {
                    showAlertDialog(context = requireContext(), title = "Alert", message = "Please enter plan price", positiveButtonText = "OK", onPositiveButtonClick = {

                    })
                } else if (selectedPlanName == "Select") {
                    showAlertDialog(context = requireContext(), title = "Alert", message = "Please select a plan", positiveButtonText = "OK", onPositiveButtonClick = {

                    })
                } else {
                    showAlertDialog(context = requireContext(), title = "Alert", message = "Please select plan validity", positiveButtonText = "OK", onPositiveButtonClick = {

                    })
                }
            }
        }

        cancelBtn.setOnClickListener {
            dismiss()
        }
    }
}