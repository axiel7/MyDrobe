package com.axiel7.mydrobe.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.axiel7.mydrobe.MyApplication
import com.axiel7.mydrobe.R
import com.axiel7.mydrobe.databinding.FragmentHomeBinding
import com.axiel7.mydrobe.models.Clothing
import com.axiel7.mydrobe.models.ClothingViewModel
import com.axiel7.mydrobe.ui.collection.CollectionFragment
import com.axiel7.mydrobe.ui.details.DetailsFragment
import com.axiel7.mydrobe.ui.search.SearchFragment
import com.axiel7.mydrobe.ui.today.TodayFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.transition.MaterialSharedAxis

class HomeFragment : Fragment() {

    private lateinit var safeContext: Context
    private val clothingViewModel: ClothingViewModel by activityViewModels {
        ClothingViewModel.provideFactory(MyApplication.clothesRepository)
    }
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.homeViewPager.adapter = TabViewPagerAdapter(childFragmentManager, lifecycle)

        TabLayoutMediator(binding.homeTabLayout, binding.homeViewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.title_today)
                1 -> tab.text = getString(R.string.title_collection)
            }
        }.attach()

        binding.homeBottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.search -> {
                    openSearch()
                    true
                }
                R.id.sort -> {
                    openSortOptions()
                    true
                }
                else -> false
            }
        }

        binding.homeFab.setOnClickListener {
            openDetails(null)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        safeContext = context
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openSortOptions() {
        when (binding.homeViewPager.currentItem) {
            0 -> (childFragmentManager.findFragmentByTag("f0") as TodayFragment)
            1 -> (childFragmentManager.findFragmentByTag("f1") as CollectionFragment).sortItems()
        }
    }

    fun hideFab() {
        binding.homeFab.hide()
    }

    fun showFab() {
        binding.homeFab.show()
    }

    fun openDetails(item: Clothing?) {
        clothingViewModel.selectItem(item)
        val newFragment = DetailsFragment()
        newFragment.show(parentFragmentManager, newFragment.tag)
    }

    private fun openSearch() {
        val newFragment = SearchFragment()
        parentFragmentManager.beginTransaction()
                .add(R.id.container, newFragment, newFragment.tag)
                .addToBackStack(newFragment.tag)
                .commit()
    }

    class TabViewPagerAdapter(fm: FragmentManager, lf: Lifecycle) : FragmentStateAdapter(fm, lf) {
        override fun getItemCount(): Int = PAGE_COUNT

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> TodayFragment()
                1 -> CollectionFragment()
                else -> TodayFragment()
            }
        }
    }

    companion object {
        const val PAGE_COUNT = 2
    }
}