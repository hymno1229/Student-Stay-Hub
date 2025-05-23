package com.example.messmaster

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.messmaster.databinding.FragmentAddPlanDialogBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import showAlertDialog

class AddPlanDialog : DialogFragment() {
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private  lateinit var binding: FragmentAddPlanDialogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddPlanDialogBinding.inflate(inflater,container,false)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent) // Make the background transparent
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val addbtn = binding.addBtn
        val cancelBtn = binding.cancelBtn
        val validityspinner = binding.validityValue
        val validities = arrayOf("Select","15 Days","30 Days")
        val spinnerAdapter =
            CustomSpinnerAdapter(requireContext(), android.R.layout.simple_spinner_item, validities)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        validityspinner.adapter = spinnerAdapter


        val planname = binding.planNameValue
        val planprice = binding.planPriceValue

        addbtn.setOnClickListener {
            // Get the email of the current user
            val currentUser = FirebaseAuth.getInstance().currentUser
            val userEmail = currentUser?.email

            // Get the plan details
            val planName = planname.text.toString()
            val planPrice = planprice.text.toString()
            val planValidity = validityspinner.selectedItem.toString()

            // Check if plan validity is "Select"
            if (planValidity == "Select") {
                // If plan validity is not selected, show a toast message to select plan validity
                showAlertDialog(context = requireContext(), title = "Alert", message = "Please select plan validity", positiveButtonText = "OK", onPositiveButtonClick = {

                })
                return@setOnClickListener // Exit the click listener
            }

            fun isPlanNameValid(planName: String): Boolean {
                return planName.startsWith("BLD - ", ignoreCase = true) ||
                        planName.startsWith("BL - ", ignoreCase = true) ||
                        planName.startsWith("BD - ", ignoreCase = true) ||
                        planName.startsWith("LD - ", ignoreCase = true)
            }

            if (!isPlanNameValid(planName)){
                showAlertDialog(context = requireContext(), title = "Alert", message = "Please Enter A Valid Plan Name.\nPlan Name Should Start With Any Of The Following\nBLD -\nBL -\nBD -\nLD -\nB - BREAKFAST, L - LUNCH, D - DINNER", positiveButtonText = "OK", onPositiveButtonClick = {

                })
                return@setOnClickListener
            }

            // Ensure all fields are filled
            if (userEmail != null && planName.isNotEmpty() && planPrice.isNotEmpty()) {
                // Create a new collection under users/email and add the plan details
                val userCollectionRef = db.collection("users").document(userEmail).collection("plans")
                val planDocumentRef = userCollectionRef.document(planName)
                val planData = hashMapOf(
                    "name" to planName,
                    "price" to planPrice,
                    "validity" to planValidity
                )

                planDocumentRef.set(planData)
                    .addOnSuccessListener {
                        showAlertDialog(context = requireContext(), title = "Alert", message = "Plan added successfully", positiveButtonText = "OK", onPositiveButtonClick = {
                            NotificationUtils.showNotification(
                                requireContext(),
                                "Plan added successfully",
                                "New Plan With Name $planName Added Successfully"
                            )
                            dismiss()
                        })
                    }
                    .addOnFailureListener { e ->
                        showAlertDialog(context = requireContext(), title = "Alert", message = "Failed to add plan: ${e.message}", positiveButtonText = "OK", onPositiveButtonClick = {
                            NotificationUtils.showNotification(
                                requireContext(),
                                "Failed to add plan",
                                "New Plan With Name $planName Not Added"
                            )
                        })
                    }
            } else {
                showAlertDialog(context = requireContext(), title = "Alert", message = "Please fill all fields", positiveButtonText = "OK", onPositiveButtonClick = {
                })
            }
        }


        cancelBtn.setOnClickListener {
            dismiss()
        }
    }

    class CustomSpinnerAdapter(context: Context, resource: Int, objects: Array<String>) :
        ArrayAdapter<String>(context, resource, objects) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getView(position, convertView, parent)
            view.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.primaryColor
                )
            ) // Set background color to orange
            (view as TextView).setTextColor(Color.WHITE) // Set text color to black
            return view
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getDropDownView(position, convertView, parent)
            view.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.spinnerbg
                )
            ) // Set background color to orange
            (view as TextView).setTextColor(Color.BLACK) // Set text color to black
            return view
        }
    }
}