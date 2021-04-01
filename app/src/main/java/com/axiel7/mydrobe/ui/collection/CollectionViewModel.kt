package com.axiel7.mydrobe.ui.collection

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.axiel7.mydrobe.MyApplication
import com.axiel7.mydrobe.models.Clothing

class CollectionViewModel : ViewModel() {

    var clothes: LiveData<List<Clothing>>
    private val _order = MutableLiveData("id")

    init {
        clothes = Transformations.switchMap(_order) {
            MyApplication.drobeDb.clothesDao().getClothes(it)
        }
    }

    fun setOrder(sort: String) {
        _order.postValue(sort)
    }

}