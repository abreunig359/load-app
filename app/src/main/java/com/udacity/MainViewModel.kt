package com.udacity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private val _downloadLinkSelected = MutableLiveData<Boolean>()
    val downloadLinkSelected: LiveData<Boolean>
        get() = _downloadLinkSelected

    private val _downloadStarted = MutableLiveData<Boolean>()
    val downloadStarted: LiveData<Boolean>
        get() = _downloadStarted

    private val _downloadFinished = MutableLiveData<Boolean>()
    val downloadFinished: LiveData<Boolean>
        get() = _downloadFinished
}