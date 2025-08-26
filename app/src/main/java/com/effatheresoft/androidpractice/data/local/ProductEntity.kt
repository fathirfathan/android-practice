package com.effatheresoft.androidpractice.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.effatheresoft.androidpractice.data.Product

@Entity("product")
class ProductEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val description: String
)

fun List<ProductEntity>.toProductList(): List<Product> {
    val productList = mutableListOf<Product>()
    for(product in this) {
        productList.add(
            Product(
                product.id,
                product.title,
                product.description
            )
        )
    }
    return productList
}