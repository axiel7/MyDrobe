package com.axiel7.mydrobe.models

import androidx.lifecycle.*
import com.axiel7.mydrobe.repository.ClothesRepository
import kotlinx.coroutines.launch

class ClothingViewModel(private val clothesRepository: ClothesRepository) : ViewModel() {

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
            clothesRepository.getClothing().asLiveData()

    fun addClothing(clothing: Clothing) {
        viewModelScope.launch {
            clothesRepository.createClothing(clothing)
        }
    }

    fun updateClothing(clothing: Clothing) {
        viewModelScope.launch {
            clothesRepository.updateClothing(clothing)
        }
    }

    fun deleteClothing(clothing: Clothing) {
        viewModelScope.launch {
            clothesRepository.removeClothing(clothing)
        }
    }

    private val _selectedItem = MutableLiveData<Clothing?>()
    val selectedItem: LiveData<Clothing?> = _selectedItem

    fun selectItem(item: Clothing?) {
        _selectedItem.value = item
    }

    companion object {
        fun provideFactory(
                clothesRepository: ClothesRepository
        ) : ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return ClothingViewModel(clothesRepository) as T
            }
        }
    }
}