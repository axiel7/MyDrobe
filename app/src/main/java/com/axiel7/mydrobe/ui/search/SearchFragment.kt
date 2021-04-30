package com.axiel7.mydrobe.ui.search

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.axiel7.mydrobe.MainActivity
import com.axiel7.mydrobe.MyApplication
import com.axiel7.mydrobe.adapters.ClothingAdapter
import com.axiel7.mydrobe.databinding.FragmentSearchBinding
import com.google.android.material.transition.MaterialFadeThrough

class SearchFragment : Fragment() {

    private lateinit var adapter: ClothingAdapter
    private lateinit var searchViewModel: SearchViewModel
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
        //returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        searchViewModel = ViewModelProvider(this,
                SearchViewModel.provideFactory(MyApplication.clothesRepository))
                .get(SearchViewModel::class.java)
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loading.hide()
        binding.back.setOnClickListener {
            binding.search.clearFocus()
            parentFragmentManager.popBackStack()
        }

        binding.search.postDelayed({ binding.search.showKeyboard() }, 50)
        binding.search.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchViewModel.search(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchViewModel.search(newText ?: "")
                return true
            }
        })

        adapter = ClothingAdapter(requireContext(),
                onClickListener = { _, item -> (activity as MainActivity).openDetails(item) }
        )
        binding.searchRecyclerView.adapter = adapter

        searchViewModel.clothes.observe(viewLifecycleOwner, {
            adapter.setData(it)
        })

    }

    private fun SearchView.showKeyboard() {
        val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        requestFocus()
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }
}