package com.davidson.strangers.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DetailedViewModelFactory(val strangerId: Long, private val app: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailedViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailedViewModel(strangerId, app) as T
        }
        throw IllegalArgumentException("Unable to construct DetailedViewModel")
    }
}