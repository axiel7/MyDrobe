package com.axiel7.mydrobe.room

import androidx.room.*
import com.axiel7.mydrobe.models.Clothing
import kotlinx.coroutines.flow.Flow

@Dao
interface ClothesDao {

    @Query("SELECT * FROM clothing")
    fun getClothes(): Flow<List<Clothing>>

    @Query("SELECT * FROM clothing ORDER BY CASE WHEN :order = 'id' THEN id END DESC, CASE WHEN :order = 'name' THEN name END")
    fun getClothes(order: String): Flow<List<Clothing>>

    @Query("SELECT * FROM clothing WHERE id=:id")
    fun getItemById(id: Int): Flow<Clothing>

    @Query("SELECT * FROM clothing WHERE LOWER(name) LIKE LOWER(:query)")
    fun searchClothes(query: String): Flow<List<Clothing>>

    @Query("SELECT * FROM clothing WHERE seasons LIKE :season")
    fun getClothesBySeason(season: String): Flow<List<Clothing>>

    @Query("SELECT * FROM clothing WHERE seasons LIKE :season ORDER BY CASE WHEN :order = 'id' THEN id END DESC, CASE WHEN :order = 'name' THEN name END")
    fun getClothesBySeason(season: String, order: String): Flow<List<Clothing>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addClothing(clothing: Clothing?)

    @Update
    suspend fun updateClothing(clothing: Clothing?)

    @Delete
    suspend fun removeClothing(clothing: Clothing)
}