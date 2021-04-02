package com.axiel7.mydrobe

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.util.CoilUtils
import com.axiel7.mydrobe.repository.ClothesRepository
import com.axiel7.mydrobe.room.DrobeDatabase
import com.axiel7.mydrobe.utils.SharedPrefsHelpers
import okhttp3.OkHttpClient

class MyApplication : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()
        SharedPrefsHelpers.init(applicationContext)
        drobeDb = DrobeDatabase.getInstance(applicationContext)
        clothesRepository = ClothesRepository(drobeDb.clothesDao())
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(applicationContext)
                .crossfade(true)
                .crossfade(300)
                .okHttpClient {
                    OkHttpClient.Builder()
                    .cache(CoilUtils.createDefaultCache(applicationContext))
                    .build()
                }
                .build()
    }

    companion object {
        lateinit var drobeDb: DrobeDatabase
        lateinit var clothesRepository: ClothesRepository
    }
}