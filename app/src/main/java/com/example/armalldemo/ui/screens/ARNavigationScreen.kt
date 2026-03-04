package com.example.armalldemo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.armalldemo.utils.ARCoreUtils
import com.google.ar.core.AugmentedImage
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.core.TrackingState
import io.github.sceneview.ar.ARScene
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
@Composable
fun ARNavigationScreen() {
    val context = LocalContext.current
    var isDatabaseLoaded by remember { mutableStateOf(false) }
    var recognizedLogo by remember { mutableStateOf<String?>("Initializing AR features...") }
    
    // Track which images we've already placed an anchor on
    val trackedImages = remember { mutableSetOf<String>() }

    // Keep session reference to configure DB asynchronously 
    var arSession by remember { mutableStateOf<Session?>(null) }

    LaunchedEffect(arSession) {
        arSession?.let { session ->
            // Move heavy image decoding off the main thread to prevent ANR!
            withContext(Dispatchers.IO) {
                val database = ARCoreUtils.setupAugmentedImageDatabase(session, context)
                if (database != null) {
                    val config = session.config
                    config.augmentedImageDatabase = database
                    session.configure(config)
                }
                // Update UI once loading is complete
                isDatabaseLoaded = true
                recognizedLogo = "Scanning environment..."
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // AR Scene from Sceneview
        ARScene(
            modifier = Modifier.fillMaxSize(),
            sessionConfiguration = { session, config ->
                // Instruct ARCore to auto-focus
                config.focusMode = Config.FocusMode.AUTO
                // Capture session so LaunchedEffect can attach the DB asynchronously
                arSession = session 
            },
            onSessionUpdated = { session, updatedFrame ->
                var currentlyLookingAt: String? = null
                
                // Retrieve all currently tracked Augmented Images from the ARCore session.
                // This allows the app to know which physical reference images are in the camera view.
                val allTrackables = session.getAllTrackables(AugmentedImage::class.java)

                for (augmentedImage in allTrackables) {
                    // Check if the image is actively being tracked. 
                    // FULL_TRACKING ensures we are actively looking at the physical image in real-time, 
                    // rather than ARCore just remembering where it was previously.
                    if (augmentedImage.trackingState == TrackingState.TRACKING && 
                        augmentedImage.trackingMethod == AugmentedImage.TrackingMethod.FULL_TRACKING) {
                        
                        // Format names like "pull_bear" -> "Pull Bear", "btc" -> "BTC", "miss_L" -> "Miss L" 
                        val rawName = augmentedImage.name
                        val storeName = rawName.split("_").joinToString(" ") { word ->
                            word.replaceFirstChar { char -> char.uppercase() }
                        }.replace("Btc", "BTC").replace("Miss L", "Miss L")

                        currentlyLookingAt = storeName
                        
                        // Keep track of detected images so we avoid redundant processing 
                        // (e.g., placing 3D navigation nodes multiple times for the same store logo)
                        if (!trackedImages.contains(storeName)) {
                            trackedImages.add(storeName)
                        }
                    }
                }
                
                // Update the UI state based on tracking results.
                // If we are actively tracking a logo, display the store name.
                // Otherwise, revert to scanning instructions (only if the image database is ready).
                if (currentlyLookingAt != null) {
                    recognizedLogo = currentlyLookingAt
                } else if (isDatabaseLoaded) {
                    recognizedLogo = "Scanning environment..."
                }
            }
        )
        
        // UX Overlay (Top Bar)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xD90D1B2A))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "MallAR",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            
            IconButton(
                onClick = { /* Settings/Profile */ },
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xD90D1B2A))
            ) {
                 Icon(Icons.Default.Info, contentDescription = "Info", tint = Color.White)
            }
        }

        // Navigation Bottom Sheet Style Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xF20D1B2A)) // Semi-transparent dark blue
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = recognizedLogo ?: "Point camera at a store logo",
                    color = if (recognizedLogo == "Scanning environment...") Color.LightGray else Color(0xFF00B4D8),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF00B4D8),
                    trackColor = Color.DarkGray
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Find a recognizable storefront to calibrate your location and start navigating.",
                    color = Color.White,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
