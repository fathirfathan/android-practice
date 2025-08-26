package com.effatheresoft.androidpractice.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.effatheresoft.androidpractice.R
import com.effatheresoft.androidpractice.databinding.ActivityMainBinding
import com.effatheresoft.androidpractice.di.Injection
import kotlin.getValue

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels { Injection.getViewModelFactory(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.recyclerViewTask.layoutManager = LinearLayoutManager(this)
//        val productAdapter = ProductAdapter()
        val productAdapter = ProductPagingAdapter()
        binding.recyclerViewTask.adapter = productAdapter
//        viewModel.getProductsFromLocal().observe(this) { productList ->
//            productAdapter.submitList(productList)
//        }
        viewModel.products.observe(this) {
            productAdapter.submitData(lifecycle, it)
        }

        binding.buttonRemote.setOnClickListener {
            viewModel.getProductsFromRemote()
        }
        binding.buttonDeleteProducts.setOnClickListener {
            viewModel.deleteAllProducts()
        }
    }
}