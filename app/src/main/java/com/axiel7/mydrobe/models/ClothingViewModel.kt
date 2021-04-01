package com.axiel7.mydrobe.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.axiel7.mydrobe.MyApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ClothingViewModel : ViewModel() {

    private val exampleData = mutableListOf(
            Clothing(name = "Jeans"),
            Clothing(name = "T-Shirt"),
            Clothing(name = "Pants"),
            Clothing(name = "Jacket"),
            Clothing(name = "Shocks"),
            Clothing(name = "Shoes"),
            Clothing(name = "Casual pants"),
            Clothing(name = "Ring")
    )

    val clothes: LiveData<List<Clothing>> =
            MyApplication.drobeDb.clothesDao().getClothes()

    fun insertClothing(clothing: Clothing) {
        CoroutineScope(Dispatchers.IO).launch {
            MyApplication.drobeDb.clothesDao().addClothing(clothing)
        }
    }

    fun updateClothing(clothing: Clothing) {
        CoroutineScope(Dispatchers.IO).launch {
            MyApplication.drobeDb.clothesDao().updateClothing(clothing)
        }
    }

    fun deleteClothing(clothing: Clothing) {
        CoroutineScope(Dispatchers.IO).launch {
            MyApplication.drobeDb.clothesDao().removeClothing(clothing)
        }
    }

    private val _selectedItem = MutableLiveData<Clothing?>()
    val selectedItem: LiveData<Clothing?> = _selectedItem

    fun selectItem(item: Clothing?) {
        _selectedItem.value = item
    }

}