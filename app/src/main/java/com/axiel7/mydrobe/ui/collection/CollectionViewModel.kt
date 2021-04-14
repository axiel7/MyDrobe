package com.axiel7.mydrobe.ui.collection

import androidx.lifecycle.*
import com.axiel7.mydrobe.models.Clothing
import com.axiel7.mydrobe.repository.ClothesRepository

class CollectionViewModel(private val clothesRepository: ClothesRepository) : ViewModel() {

    private val _order = MutableLiveData("id")
    var clothes: LiveData<List<Clothing>> = Transformations.switchMap(_order) {
        clothesRepository.getClothing(it).asLiveData()
    }

    fun setOrder(sort: String) {
        _order.postValue(sort)
    }

    companion object {
        fun provideFactory(
                clothesRepository: ClothesRepository
        ) : ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return CollectionViewModel(clothesRepository) as T
            }
        }
    }

}