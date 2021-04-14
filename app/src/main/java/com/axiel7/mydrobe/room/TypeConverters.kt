package com.axiel7.mydrobe.room

import androidx.room.TypeConverter
import com.axiel7.mydrobe.models.Season
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*

class TypeConverters {
    private val gson = Gson()

    @TypeConverter
    fun stringToSeason(data: String?): Season? {
        if (data == null) {
            return null
        }
        val type: Type = object : TypeToken<Season?>() {}.type
        return gson.fromJson<Season?>(data, type)
    }
    @TypeConverter
    fun seasonToString(someObject: Season?): String? {
        return gson.toJson(someObject)
    }

    @TypeConverter
    fun stringToListSeason(data: String?): List<Season?>? {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType: Type = object : TypeToken<List<Season?>?>() {}.type
        return gson.fromJson<List<Season?>>(data, listType)
    }
    @TypeConverter
    fun listSeasonToString(someObject: List<Season?>?): String? {
        return gson.toJson(someObject)
    }

    @TypeConverter
    fun stringToListString(data: String?): List<String?>? {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType: Type = object : TypeToken<List<String?>?>() {}.type
        return gson.fromJson<List<String?>>(data, listType)
    }
    @TypeConverter
    fun listStringToString(someObject: List<String?>?): String? {
        return gson.toJson(someObject)
    }
}