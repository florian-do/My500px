package com.do_f.my500px.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class SharedViewModel: ViewModel() {
    val position : MutableLiveData<Int> = MutableLiveData()

    fun set(position: Int) {
        this.position.value = position
    }
}