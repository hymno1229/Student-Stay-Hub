package com.example.messmaster

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messmaster.adapter.PlansAdapter
import com.example.messmaster.databinding.FragmentManageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FragmentManage : Fragment() {
    private lateinit var binding: FragmentManageBinding
    private var isFloating: Boolean = false
    private lateinit var adapter: PlansAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentManageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isAdded) {
            return
        }

        // Use CoroutineScope to fetch data asynchronously
        GlobalScope.launch(Dispatchers.Main) {
            val data = fetchData()
            adapter = PlansAdapter(data.names, data.prices, data.validities)

            // Check if the Fragment is still attached before updating UI
            if (!isAdded) {
                return@launch
            }

            binding.plansRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.plansRecyclerView.adapter = adapter
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

        val actionbtn = binding.floatingActionButton
        val editPlanbtn = binding.editPlanbtn
        val remPlanbtn = binding.remPlanbtn
        val addPlanbtn = binding.addPlanbtn

        actionbtn.setOnClickListener {
            if (!isFloating) {
                addPlanbtn.visibility = View.VISIBLE
                editPlanbtn.visibility = View.VISIBLE
                remPlanbtn.visibility = View.VISIBLE
                isFloating = true
                updateLayout()
            } else {
                addPlanbtn.visibility = View.INVISIBLE
                editPlanbtn.visibility = View.INVISIBLE
                remPlanbtn.visibility = View.INVISIBLE
                isFloating = false
                updateLayout()
            }
        }
        addPlanbtn.setOnClickListener {
            val dialog = AddPlanDialog()
            dialog.show(parentFragmentManager, "MyAddPlanDialogFragment")
        }

        editPlanbtn.setOnClickListener {
            val dialog = EditPlanDialog()
            dialog.show(parentFragmentManager, "MyUpdatePlanDialogFragment")
        }

        remPlanbtn.setOnClickListener {
            val dialog = RemovePlanDialog()
            dialog.show(parentFragmentManager, "MyRemovePlanDialogFragment")
        }

        binding.root.setOnClickListener {
            if (isFloating) {
                addPlanbtn.visibility = View.INVISIBLE
                editPlanbtn.visibility = View.INVISIBLE
                remPlanbtn.visibility = View.INVISIBLE
                isFloating = false
                updateLayout()
            }
        }
    }

    private suspend fun fetchData(): Data {
        val names = mutableListOf<String>()
        val prices = mutableListOf<String>()
        val validities = mutableListOf<String>()

        val db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userEmail = currentUser?.email

        userEmail?.let { email ->
            val documents = db.collection("users")
                .document(email)
                .collection("plans")
                .get()
                .await()

            for (document in documents) {
                val name = document.getString("name")
                val price = document.getString("price")
                val validity = document.getString("validity")

                name?.let { names.add(it) }
                price?.let { prices.add(it) }
                validity?.let { validities.add(it) }
            }
        }

        return Data(names, prices, validities)
    }

    private fun updateLayout() {
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.constraintLayout)
        val addPlanbtn = binding.addPlanbtn
        val margin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._12sdp)

        if (addPlanbtn.visibility == View.VISIBLE) {
            constraintSet.clear(binding.plansRecyclerView.id, ConstraintSet.BOTTOM)
            constraintSet.connect(
                binding.plansRecyclerView.id,
                ConstraintSet.BOTTOM,
                addPlanbtn.id,
                ConstraintSet.TOP,
                margin
            )
        } else {
            constraintSet.clear(binding.plansRecyclerView.id, ConstraintSet.BOTTOM)
            constraintSet.connect(
                binding.plansRecyclerView.id,
                ConstraintSet.BOTTOM,
                binding.floatingActionButton.id,
                ConstraintSet.TOP,
                margin
            )
        }

        constraintSet.applyTo(binding.constraintLayout)
    }

    data class Data(val names: List<String>, val prices: List<String>, val validities: List<String>)
}
