package com.effatheresoft.androidpractice.ui.details

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.effatheresoft.androidpractice.databinding.FragmentDetailsBinding
import com.effatheresoft.androidpractice.util.Result
import com.effatheresoft.androidpractice.util.Utils.showErrorToast
import com.effatheresoft.androidpractice.util.getViewModelFactory
import kotlin.getValue

class DetailsFragment : Fragment() {
    private val detailsViewModel: DetailsViewModel
        by viewModels(factoryProducer = { getViewModelFactory() } )
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = DetailsFragmentArgs.fromBundle(arguments as Bundle).id
        detailsViewModel.getCurrentTask(id).observe(viewLifecycleOwner) { result ->
            when(result) {
                is Result.Loading -> {}
                is Result.Success -> {
                    binding.textViewTitle.text = "Title: ${result.data.title}"
                    binding.textViewIsCompleted.text = "Is Completed: ${result.data.isCompleted}"
                    binding.textViewDetails.text = "Details: ${result.data.details}"
                }
                is Result.Error -> {
                    showErrorToast(requireContext(), result.code)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}