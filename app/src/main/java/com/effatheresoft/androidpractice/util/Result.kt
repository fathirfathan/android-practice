package com.effatheresoft.androidpractice.util

sealed class Result<out R> {
    data class Success<out T> (val data: T): Result<T>()
    data class Error(val code: String): Result<Nothing>()
    object Loading: Result<Nothing>()
}