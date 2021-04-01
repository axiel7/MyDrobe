package com.axiel7.mydrobe.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Clothing(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var name: String,
    var photoUri: String? = null,
    var colors: MutableList<String> = mutableListOf(),
    var seasons: MutableList<Season> = mutableListOf(Season.NONE),
    var lastUsed: Long = 0
)
