package com.udacity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DetailViewModel(val fileName: String, val status: String) : ViewModel() {

    private val _navigateToMainActivityEvent = MutableLiveData<Boolean>()
    val navigateToMainActivityEvent: LiveData<Boolean>
        get() = _navigateToMainActivityEvent

    fun onNavigateToMainActivity() {
        _navigateToMainActivityEvent.value = true
    }

    class Factory(private val fileName: String, private val status: String) :
        ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DetailViewModel(fileName, status) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}