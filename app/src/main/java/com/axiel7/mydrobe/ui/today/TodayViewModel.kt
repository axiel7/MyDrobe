package com.axiel7.mydrobe.ui.today

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TodayViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "Coming soon"
    }
    val text: LiveData<String> = _text
}