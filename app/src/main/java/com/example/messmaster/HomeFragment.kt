package com.example.messmaster

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.messmaster.adapter.expiringAdapter
import com.example.messmaster.databinding.FragmentHomeBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.core.FirestoreClient
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Locale

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: expiringAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var email:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val greetingTextView = binding.root.findViewById<TextView>(R.id.greetings)
        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()
        // Retrieve user's name from Firebase Authentication
        val user = FirebaseAuth.getInstance().currentUser
        val userName = user?.displayName
        email = user?.email.toString()
        greetingTextView.setText("Hello "+ userName+" 👋")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.banner1, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.banner2, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.banner3, ScaleTypes.FIT))
        val imageSlider = binding.imageSlider
        imageSlider.setImageList(imageList)
        imageSlider.setImageList(imageList, ScaleTypes.FIT)
        fun getTotalCount( onComplete: (Int) -> Unit) {
            val db = FirebaseFirestore.getInstance()
            val userEmail = email
            val collectionPath = "users/$userEmail/customers"
            val collectionReference = db.collection(collectionPath)

            collectionReference.get()
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val count = task.result?.size() ?: 0
                        onComplete(count)
                    } else {
                        onComplete(-1) // Indicate error with a negative count
                    }
                })
        }

        fun getCountValues(onComplete: (activeCount: Int, expiredCount: Int, breakfastCount: Int, lunchCount: Int, dinnerCount: Int) -> Unit) {
            val db = FirebaseFirestore.getInstance()
            val userEmail = email // Assuming email is accessible in this scope
            val collectionPath = "users/$userEmail/customers"
            val collectionReference = db.collection(collectionPath)

            val currentDate = Calendar.getInstance().time
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            var activeCount = 0
            var expiredCount = 0
            var breakfastCount = 0
            var lunchCount = 0
            var dinnerCount = 0

            collectionReference.get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents) {
                        val expiryDateStr = document.getString("expiryDate")
                        val planStr = document.getString("plan")

                        if (expiryDateStr != null && planStr != null) {
                            val expiryDate = sdf.parse(expiryDateStr)

                            if (expiryDate != null && expiryDate > currentDate) {
                                activeCount++
                                val planParts = planStr.split("-")
                                if (planParts.isNotEmpty()) {
                                    val trimmedPlanPart = planParts[0].trim()
                                    for (char in trimmedPlanPart) {
                                        when (char) {
                                            'B' -> breakfastCount++
                                            'L' -> lunchCount++
                                            'D' -> dinnerCount++
                                        }
                                    }
                                }
                            } else {
                                expiredCount++
                            }
                        }
                    }
                    onComplete(activeCount, expiredCount, breakfastCount, lunchCount, dinnerCount)
                }
                .addOnFailureListener { e ->
                    onComplete(-1, -1, -1, -1, -1) // Indicate error with negative counts
                }
        }

        getTotalCount() { count ->
            if (count >= 0) {
                binding.totalValue.text = count.toString()
            } else {
                binding.totalValue.text = "0"
            }
        }

        getCountValues() { activeCount, expiredCount, breakfastCount, lunchCount, dinnerCount ->
            if (activeCount >= 0) {
                binding.activeValue.text = activeCount.toString()
            } else {
                binding.activeValue.text = "0"
            }
            if (expiredCount >= 0) {
                binding.inactiveValue.text = expiredCount.toString()
            } else {
                binding.inactiveValue.text = "0"
            }
            if (breakfastCount >= 0) {
                binding.breakfastValue.text = breakfastCount.toString()
            } else {
                binding.breakfastValue.text = "0"
            }
            if (lunchCount >= 0) {
                binding.lunchValue.text = lunchCount.toString()
            } else {
                binding.lunchValue.text = "0"
            }
            if (dinnerCount >= 0) {
                binding.dinnerValue.text = dinnerCount.toString()
            } else {
                binding.dinnerValue.text = "0"
            }
        }

        data class Customer(
            val name: String,
            val customerName: String,
            val mobileNumber: String
        )
        fun getCustomersWithExpiringDateToday(onComplete: (List<Customer>) -> Unit) {
            val db = FirebaseFirestore.getInstance()
            val userEmail = email // Assuming email is accessible in this scope
            val collectionPath = "users/$userEmail/customers"
            val collectionReference = db.collection(collectionPath)

            val currentDate = Calendar.getInstance().time
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            val customers = mutableListOf<Customer>()

            collectionReference.whereEqualTo("expiryDate", sdf.format(currentDate)).get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents) {
                        val name = document.getString("name") ?: ""
                        val customerName = document.getString("customerName") ?: ""
                        val mobileNumber = document.getString("mobileNumber") ?: ""
                        customers.add(Customer(name, customerName, mobileNumber))
                    }
                    onComplete(customers)
                }
                .addOnFailureListener { e ->
                    onComplete(emptyList()) // Handle error, return empty list
                }
        }
        getCustomersWithExpiringDateToday { customers ->
            // Extract only customerName and mobileNumber and pass to adapter
            val customerNames = customers.map { it.customerName }
            val mobileNumbers = customers.map { it.mobileNumber }
            val adapter = expiringAdapter(customerNames, mobileNumbers)
            if (isAdded){
                binding.expiringRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                binding.expiringRecyclerView.adapter = adapter
            }
        }

    }
}