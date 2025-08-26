package com.effatheresoft.androidpractice.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.effatheresoft.androidpractice.data.Product
import com.effatheresoft.androidpractice.data.remote.ApiService
import com.effatheresoft.androidpractice.data.remote.ProductResponse

class ProductPagingSource(private val apiService: ApiService): PagingSource<Int, Product>() {
    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val skipPageNumber = position - 1

            val productList = mutableListOf<Product>()
            val rawResponse = apiService.getProductsWithCoroutine(skipPageNumber, params.loadSize)
            for (response in rawResponse.products) {
                productList.add(
                    Product(
                        response.id,
                        response.title,
                        response.description
                    )
                )
            }
            val responseData = productList

            LoadResult.Page(
                data = responseData,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.isNullOrEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

}