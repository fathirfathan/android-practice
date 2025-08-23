package com.effatheresoft.androidpractice.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.effatheresoft.androidpractice.databinding.FragmentHomeBinding
import com.effatheresoft.androidpractice.ui.TaskAdapter
import com.effatheresoft.androidpractice.util.Result
import com.effatheresoft.androidpractice.util.Utils.showErrorToast
import com.effatheresoft.androidpractice.util.getViewModelFactory

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels(factoryProducer = { getViewModelFactory() })
    private lateinit var navController: NavController
    private val adapter = TaskAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        val layoutManager = LinearLayoutManager(requireContext())
        if (adapter.onItemClicked == null) setNavigateOnItemClicked()
        binding.recyclerViewTask.layoutManager = layoutManager
        binding.recyclerViewTask.adapter = adapter
        viewModel.getTasks().observe(viewLifecycleOwner) { tasks ->
            when(tasks) {
                is Result.Loading -> { toggleProgressIndicator(true) }
                is Result.Success -> {
                    toggleProgressIndicator(false)
                    adapter.submitList(tasks.data)
                }
                is Result.Error -> {
                    toggleProgressIndicator(false)
                    showErrorToast(requireContext(), tasks.code)
                }
            }
        }

        binding.buttonToSettings.setOnClickListener {
            navController.navigate(
                HomeFragmentDirections.actionHomeFragmentToSettingsFragment())
        }

        binding.buttonClearDatabase.setOnClickListener {
            viewModel.deleteAllTasks()
        }
    }

    fun toggleProgressIndicator(isLoading: Boolean) {
        if (isLoading) binding.progressCircularTask.visibility = View.VISIBLE
        else binding.progressCircularTask.visibility = View.GONE
    }

    fun setNavigateOnItemClicked() {
        adapter.onItemClicked = { task ->
            val toDetailsDirection = HomeFragmentDirections
                .actionHomeFragmentToDetailsFragment(task.id)
            navController.navigate(toDetailsDirection)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}