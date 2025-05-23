package com.example.messmaster.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.messmaster.databinding.FragmentExpiringViewBinding

class expiringAdapter(private val originalNames: List<String>, private val originalMobileNos: List<String>) : RecyclerView.Adapter<expiringAdapter.ExpiringViewHolder>() {

    private var names: List<String> = originalNames
    private var mobileNos: List<String> = originalMobileNos

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpiringViewHolder {
        val binding = FragmentExpiringViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExpiringViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return names.size
    }

    override fun onBindViewHolder(holder: ExpiringViewHolder, position: Int) {
        val name = names[position]
        val mobileNo = mobileNos[position]
        holder.bind(name, mobileNo)
    }

    fun filter(query: String) {
        val filteredNames = mutableListOf<String>()
        val filteredMobileNos = mutableListOf<String>()

        originalNames.forEachIndexed { index, name ->
            val mobileNo = originalMobileNos[index]
            if (name.contains(query, ignoreCase = true) || mobileNo.contains(query, ignoreCase = true)) {
                filteredNames.add(name)
                filteredMobileNos.add(mobileNo)
            }
        }

        names = filteredNames
        mobileNos = filteredMobileNos
        notifyDataSetChanged()
    }

    class ExpiringViewHolder(private val binding: FragmentExpiringViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(name: String, mobileNo: String) {
            binding.nameValue.text = name
            binding.mobileValue.text = mobileNo
        }
    }
}
