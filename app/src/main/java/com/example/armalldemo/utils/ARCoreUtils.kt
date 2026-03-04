package com.example.armalldemo.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import com.google.ar.core.AugmentedImageDatabase
import com.google.ar.core.Session
import java.io.IOException

object ARCoreUtils {

    private const val TAG = "ARCoreUtils"

    /**
     * Loads a precompiled AugmentedImageDatabase (.imgdb) from assets.
     * This is highly optimized for performance and memory compared to runtime generation.
     */
    fun setupAugmentedImageDatabase(session: Session, context: Context): AugmentedImageDatabase? {
        return try {
            context.assets.open("mall_database.imgdb").use { inputStream ->
                val database = AugmentedImageDatabase.deserialize(session, inputStream)
                Log.d(TAG, "Successfully loaded offline AugmentedImageDatabase for 175 images")
                database
            }
        } catch (e: Exception) {
            Log.e(TAG, "Could not load mall_database.imgdb from assets", e)
            null
        }
    }
}
