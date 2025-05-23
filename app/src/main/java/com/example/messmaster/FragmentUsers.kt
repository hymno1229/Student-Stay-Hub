package com.example.messmaster

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messmaster.adapter.PlansAdapter
import com.example.messmaster.adapter.expiringAdapter
import com.example.messmaster.databinding.FragmentUsersBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import showAlertDialog

class FragmentUsers : Fragment() {
    private var isFloating = false
    private lateinit var binding: FragmentUsersBinding
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var adapter: expiringAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isAdded) {
            return
        }
        val actionbtn = binding.floatingActionButton
        val editcusBtn = binding.editCusFbtn
        val removeBtn = binding.remCusFbtn
        val addcusBtn = binding.addCusFbtn
        val rootview = binding.root

        actionbtn.setOnClickListener {
            if (!isFloating) {
                addcusBtn.visibility = View.VISIBLE
                editcusBtn.visibility = View.VISIBLE
                removeBtn.visibility = View.VISIBLE
                isFloating = true
                updateLayout()
            } else {
                addcusBtn.visibility = View.INVISIBLE
                editcusBtn.visibility = View.INVISIBLE
                removeBtn.visibility = View.INVISIBLE
                isFloating = false
                updateLayout()
            }
        }

        rootview.setOnClickListener {
            if (isFloating) {
                addcusBtn.visibility = View.INVISIBLE
                editcusBtn.visibility = View.INVISIBLE
                removeBtn.visibility = View.INVISIBLE
                isFloating = false
                updateLayout()
            }
        }

        addcusBtn.setOnClickListener {
            val dialog = AddCustomerDialog()
            dialog.show(parentFragmentManager, "MyAddDialogFragment")
        }

        editcusBtn.setOnClickListener {
            val dialog = UpdateCustomerDialog()
            dialog.show(parentFragmentManager, "MyUpdateDialogFragment")
        }

        removeBtn.setOnClickListener {
            val dialog = RemoveCustomerDialog()
            dialog.show(parentFragmentManager, "MyRemoveDialogFragment")
        }

        // Use CoroutineScope to fetch data asynchronously
        GlobalScope.launch(Dispatchers.Main) {
            val data = fetchDataFromFirestore()
            adapter = expiringAdapter(data.names, data.mobileNos)
            if (!isAdded) {
                return@launch
            }
            binding.cusRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.cusRecyclerView.adapter = adapter
        }
        val searchVal = binding.searchInput

        searchVal.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Filter the data based on the new search query
                val query = s.toString().trim()
                adapter.filter(query)
            }

            override fun afterTextChanged(s: Editable?) {
                // Not used
            }
        })

    }

    private suspend fun fetchDataFromFirestore(): Data {
        // Get current user's email
        val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: return Data(emptyList(), emptyList())

        // Construct the collection reference
        val collectionRef = firestore.collection("users").document(userEmail).collection("customers")

        // Fetch documents from Firestore
        val documents = collectionRef.get().await()

        val names = mutableListOf<String>()
        val mobileNos = mutableListOf<String>()

        // Iterate through each document
        for (document in documents) {
            // Get customerName and mobileNumber fields
            val customerName = document.getString("customerName")
            val mobileNumber = document.getString("mobileNumber")

            // Add customerName and mobileNumber to respective lists if they are not null
            customerName?.let { names.add(it) }
            mobileNumber?.let { mobileNos.add(it) }
        }

        return Data(names, mobileNos)
    }

    private fun updateLayout() {
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.constraintLayout)
        val addCusbtn = binding.addCusFbtn
        val margin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._12sdp) // Fetch margin in pixels

        if (addCusbtn.visibility == View.VISIBLE) {
            // If addPlanbtn is visible, adjust constraints and margins accordingly
            constraintSet.clear(R.id.cusRecyclerView, ConstraintSet.BOTTOM)
            constraintSet.connect(
                R.id.cusRecyclerView,
                ConstraintSet.BOTTOM,
                R.id.add_cusFbtn,
                ConstraintSet.TOP,
                margin // Apply top margin of 12dp
            )
        } else {
            // If addPlanbtn is not visible, revert constraints and margins
            constraintSet.clear(R.id.cusRecyclerView, ConstraintSet.BOTTOM)
            constraintSet.connect(
                R.id.cusRecyclerView,
                ConstraintSet.BOTTOM,
                R.id.floatingActionButton,
                ConstraintSet.TOP,
                margin // Apply top margin of 12dp
            )
        }

        // Apply the constraint changes
        constraintSet.applyTo(binding.constraintLayout)
    }

    data class Data(val names: List<String>, val mobileNos: List<String>)
}
