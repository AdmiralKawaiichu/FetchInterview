package com.example.interview.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.interview.Item
import com.example.interview.databinding.FragmentHomeBinding
import kotlin.collections.sortedBy
import kotlin.collections.toSortedMap
import kotlin.jvm.java
import kotlin.text.isNullOrBlank

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var itemAdapter: ItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        itemAdapter = ItemAdapter(emptyList())
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = itemAdapter
        }

        homeViewModel.items.observe(viewLifecycleOwner) { items ->
            val filteredAndSortedItems = items
                .filter { item -> !item.name.isNullOrBlank() } // Filter out blank or null names
                .groupBy { it.listId } // Group items by listId
                .toSortedMap(compareBy { it.toString().toIntOrNull() ?: Int.MAX_VALUE }) // Ensure numeric sorting of listId
                .flatMap { (_, items) ->
                    items.sortedBy { it.name } // Sort items within each group by name
                }

            itemAdapter.updateData(filteredAndSortedItems)
        }


        homeViewModel.loading.observe(viewLifecycleOwner) {
            // Show loading indicator if needed
        }

        homeViewModel.error.observe(viewLifecycleOwner) { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }

        homeViewModel.fetchItems()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class ItemAdapter(private var items: List<Item>) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.itemName.text = "List ID: ${item.listId}, Item ID: ${item.id}, Name: ${item.name}"
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<Item>) {
        items = newItems
        notifyDataSetChanged()
    }
}