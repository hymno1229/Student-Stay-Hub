package com.example.messmaster.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.messmaster.databinding.FragmentPlansViewBinding

class PlansAdapter(
    private val originalNames: List<String>,
    private val originalPrices: List<String>,
    private val originalValidities: List<String>
) : RecyclerView.Adapter<PlansAdapter.PlansViewHolder>() {

    private var names: List<String> = originalNames
    private var prices: List<String> = originalPrices
    private var validities: List<String> = originalValidities

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlansViewHolder {
        val binding = FragmentPlansViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlansViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return names.size
    }

    override fun onBindViewHolder(holder: PlansViewHolder, position: Int) {
        val name = names[position]
        val price = prices[position]
        val validity = validities[position]
        holder.bind(name, price, validity)
    }

    fun filter(query: String) {
        val filteredNames = mutableListOf<String>()
        val filteredPrices = mutableListOf<String>()
        val filteredValidities = mutableListOf<String>()

        originalNames.forEachIndexed { index, name ->
            if (name.contains(query, ignoreCase = true)) {
                filteredNames.add(name)
                filteredPrices.add(originalPrices[index])
                filteredValidities.add(originalValidities[index])
            }
        }

        names = filteredNames
        prices = filteredPrices
        validities = filteredValidities
        notifyDataSetChanged()
    }

    class PlansViewHolder(private val binding: FragmentPlansViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(name: String, price: String, validity: String) {
            binding.nameValue.text = name
            binding.mobileValue.text = price
            binding.anotherTextView.text = validity
        }
    }
}

