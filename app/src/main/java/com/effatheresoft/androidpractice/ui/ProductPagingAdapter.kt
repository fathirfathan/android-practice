package com.effatheresoft.androidpractice.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.effatheresoft.androidpractice.data.Product
import com.effatheresoft.androidpractice.databinding.ItemProductBinding

class ProductPagingAdapter: PagingDataAdapter<Product, ProductPagingAdapter.ProductPagingViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductPagingViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductPagingViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ProductPagingViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    class ProductPagingViewHolder(private val binding: ItemProductBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product?) {
            binding.textViewProduct.text = product?.title
        }
    }

    companion object {
        val DIFF_CALLBACK = object: DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(
                oldItem: Product,
                newItem: Product
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: Product,
                newItem: Product
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}