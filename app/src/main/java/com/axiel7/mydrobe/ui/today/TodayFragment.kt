package com.axiel7.mydrobe.ui.today

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.axiel7.mydrobe.databinding.FragmentTodayBinding

class TodayFragment : Fragment() {

    private lateinit var todayViewModel: TodayViewModel
    private var _binding: FragmentTodayBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        todayViewModel = ViewModelProvider(this).get(TodayViewModel::class.java)
        _binding = FragmentTodayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        todayViewModel.text.observe(viewLifecycleOwner, {
            binding.todayText.text = it
        })
    }
}