package com.davidson.strangers.util

import android.content.Context
import android.net.ConnectivityManager


fun isNetworkAvailable(context: Context) : Boolean {
    var flag = false
    val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    connectivity.activeNetwork?.let {
        flag = true
    }
    return flag
}