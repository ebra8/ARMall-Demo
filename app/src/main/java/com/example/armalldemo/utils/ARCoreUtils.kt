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
     * Creates an AugmentedImageDatabase locally for offline recognition.
     * In a real app, you would load these images from the 'assets' folder.
     * For this demo, we'll try to load them, but fail gracefully if missing.
     */
    fun setupAugmentedImageDatabase(session: Session, context: Context): AugmentedImageDatabase? {
        val database = AugmentedImageDatabase(session)
        var imagesAdded = 0

        // Dictionary mapping store names to their respective reference images
        val logosToTrack = mapOf(
            "Bershka" to "bershka-intrance.jpg",
            "Esla" to "esla-intrance.jpg",
            "Kiko Milano" to "kiko-intrance.jpg",
            "Lefties" to "lefties-intrance.jpg",
            "Mazaya" to "mazaya-intrance.jpg",
            "OXXO" to "oxxo-intrance.jpg",
            "Oxygene" to "oxygene-intrance.jpg",
            "Reserved" to "reserved-intrance.jpg",
            "ZARA" to "zara-intrance.jpg"
        )

        // Iterate through the store logos and add them to the ARCore AugmentedImageDatabase
        for ((storeName, filename) in logosToTrack) {
            try {
                context.assets.open(filename).use { inputStream ->
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    if (bitmap != null) {
                        // The third parameter (0.5f) is the estimated physical width of the image in meters. 
                        // This helps ARCore track the image scale accurately in the physical world.
                        database.addImage(storeName, bitmap, 0.5f)
                        imagesAdded++
                        Log.d(TAG, "Successfully added $storeName to AR database")
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Could not load image $filename. Make sure to add it to src/main/assets/")
            }
        }
        
        // Return null if no images were successfully added to prevent crash and handle gracefully in UI
        return if (imagesAdded > 0) database else null
    }
}
