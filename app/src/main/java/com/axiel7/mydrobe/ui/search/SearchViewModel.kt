package com.axiel7.mydrobe.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.axiel7.mydrobe.MyApplication
import com.axiel7.mydrobe.models.Clothing

class SearchViewModel : ViewModel() {

    private val _query = MutableLiveData("")
    val clothes: LiveData<List<Clothing>> = Transformations.switchMap(_query) {
        if (it.isEmpty()) {
            MyApplication.drobeDb.clothesDao().getClothes()
        } else {
            MyApplication.drobeDb.clothesDao().searchClothes("%$it%")
        }
    }

    fun search(query: String) {
        _query.value = query
    }
}