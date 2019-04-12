package com.do_f.my500px.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel: ViewModel() {
    val position : MutableLiveData<Int> = MutableLiveData()

    fun set(position: Int) {
        this.position.value = position
    }
}