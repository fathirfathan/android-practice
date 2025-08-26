package com.effatheresoft.androidpractice.data

import com.effatheresoft.androidpractice.data.local.ProductEntity

data class Product (
    val id: Int,
    val title: String,
    val description: String
)

fun List<Product>.toProductEntityList(): List<ProductEntity> {
    val productEntityList = mutableListOf<ProductEntity>()
    for(product in this) {
        productEntityList.add(
            ProductEntity(
                product.id,
                product.title,
                product.description
            )
        )
    }
    return productEntityList
}