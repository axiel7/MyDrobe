package com.axiel7.mydrobe.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.axiel7.mydrobe.models.Clothing

@Dao
interface ClothesDao {

    @Query("SELECT * FROM clothing")
    fun getClothes(): LiveData<List<Clothing>>

    @Query("SELECT * FROM clothing ORDER BY CASE WHEN :order = 'id' THEN id END DESC, CASE WHEN :order = 'name' THEN name END")
    fun getClothes(order: String): LiveData<List<Clothing>>

    @Query("SELECT * FROM clothing WHERE id=:id")
    fun getItemById(id: Int): LiveData<Clothing>

    @Query("SELECT * FROM clothing WHERE LOWER(name) LIKE LOWER(:query)")
    fun searchClothes(query: String): LiveData<List<Clothing>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addClothing(clothing: Clothing?)

    @Update
    suspend fun updateClothing(clothing: Clothing?)

    @Delete
    suspend fun removeClothing(clothing: Clothing)
}