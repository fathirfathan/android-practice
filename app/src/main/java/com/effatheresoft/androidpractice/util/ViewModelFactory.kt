package com.effatheresoft.androidpractice.util

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.effatheresoft.androidpractice.data.remote.ApiConfig
import com.effatheresoft.androidpractice.data.TaskRepository
import com.effatheresoft.androidpractice.data.local.TaskDatabase
import com.effatheresoft.androidpractice.ui.details.DetailsViewModel
import com.effatheresoft.androidpractice.ui.home.HomeViewModel

class ViewModelFactory private constructor(private val repository: TaskRepository):
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(DetailsViewModel::class.java)) {
            return DetailsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: ViewModelFactory(Injector.provideTaskRepository(context))
            }.also { INSTANCE = it }
    }
}

object Injector {
    fun provideTaskRepository(context: Context): TaskRepository {
        return TaskRepository.getInstance(
            ApiConfig.Companion.provideApiService(),
            TaskDatabase.getInstance(context).taskDao())
    }
}

fun Fragment.getViewModelFactory(): ViewModelFactory {
    return ViewModelFactory.getInstance(requireContext().applicationContext)
}