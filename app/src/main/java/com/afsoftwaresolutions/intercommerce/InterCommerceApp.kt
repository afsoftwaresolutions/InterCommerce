package com.afsoftwaresolutions.intercommerce

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import dagger.Lazy
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class InterCommerceApp : Application(), ImageLoaderFactory {

    @Inject
    lateinit var imageLoader: Lazy<ImageLoader>

    override fun newImageLoader(): ImageLoader = imageLoader.get()

}