package com.example.interview.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.interview.Item
import com.example.interview.RetrofitClient
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _items = MutableLiveData<List<Item>>()
    val items: LiveData<List<Item>> = _items

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchItems() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val items = RetrofitClient.apiService.getItems()
                _items.value = items
                _loading.value = false
            } catch (e: Exception) {
                _error.value = "Error fetching data: ${e.message}"
                _loading.value = false
            }
        }
    }
}