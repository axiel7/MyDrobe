package com.axiel7.mydrobe

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.axiel7.mydrobe.databinding.ActivityMainBinding
import com.axiel7.mydrobe.models.Clothing
import com.axiel7.mydrobe.models.ClothingViewModel
import com.axiel7.mydrobe.ui.details.DetailsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val clothingViewModel: ClothingViewModel by viewModels {
        ClothingViewModel.provideFactory(MyApplication.clothesRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

    }

    fun openDetails(item: Clothing?) {
        clothingViewModel.selectItem(item)
        val newFragment = DetailsFragment()
        newFragment.show(supportFragmentManager, newFragment.tag)
    }
}