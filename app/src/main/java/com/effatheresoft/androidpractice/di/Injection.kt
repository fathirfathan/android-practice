package com.effatheresoft.androidpractice.di

import android.content.Context
import com.effatheresoft.androidpractice.data.ProductRepository
import com.effatheresoft.androidpractice.data.local.ProductDatabase
import com.effatheresoft.androidpractice.data.remote.ApiConfig
import com.effatheresoft.androidpractice.data.remote.ApiService
import com.effatheresoft.androidpractice.ui.ViewModelFactory

class Injection {
    companion object {
        fun getViewModelFactory(context: Context): ViewModelFactory = ViewModelFactory(context)
        fun getRepository(context: Context): ProductRepository = ProductRepository(
            ProductDatabase.getInstance(context), ApiConfig.getApiService())
    }
}