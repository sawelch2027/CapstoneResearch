package com.example.capstoneresearch

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.maplibre.android.MapLibre
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.location.LocationComponentActivationOptions
import org.maplibre.android.location.modes.CameraMode
import org.maplibre.android.location.modes.RenderMode
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style

class MainActivity : ComponentActivity() {
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        MapLibre.getInstance(this)

        setContent {
            val ctx = LocalContext.current
            var myMap by remember { mutableStateOf<MapLibreMap?>(null) }
            var hasLocation by remember { mutableStateOf(false) }

            // Ask for location permission on startup
            val permReq = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                hasLocation = granted
            }
            LaunchedEffect(Unit) {
                permReq.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }

            // Screen layout with Header, Map, and Footer
            Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
                
                // HEADER (Added a "Locate Me" tracking button for the Waze feel!)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DropaMarker",
                        color = Color.Black,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Button(onClick = {
                        if (hasLocation) {
                            myMap?.style?.let { style ->
                                val locComponent = myMap!!.locationComponent
                                val options = LocationComponentActivationOptions.builder(ctx, style).build()
                                locComponent.activateLocationComponent(options)
                                locComponent.isLocationComponentEnabled = true
                                locComponent.cameraMode = CameraMode.TRACKING
                                locComponent.renderMode = RenderMode.COMPASS
                            }
                        } else {
                            permReq.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                    }) {
                        Text("Locate Me")
                    }
                }

                // MAP AREA (Takes up remaining space)
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    AndroidView(
                        factory = { context ->
                            MapView(context).apply {
                                getMapAsync { map ->
                                    myMap = map
                                    
                                    map.setStyle(Style.Builder().fromUri("https://demotiles.maplibre.org/style.json"))
                                    
                                    // Start over the US
                                    map.cameraPosition = CameraPosition.Builder()
                                        .target(LatLng(39.8, -98.6))
                                        .zoom(3.0)
                                        .build()

                                    // Drop pin
                                    map.addOnMapClickListener { pt ->
                                        // A true Waze feature: Categorizing the drop (Pothole!)
                                        val marker = MarkerOptions().position(pt).title("Reported Pothole")
                                        map.addMarker(marker)
                                        true
                                    }

                                    // Delete pin
                                    map.setOnMarkerClickListener { m ->
                                        map.removeMarker(m)
                                        true
                                    }
                                }
                            }
                        },
                        update = { view ->
                            view.onStart()
                            view.onResume()
                        }
                    )

                    // Crosshair in the center of the map
                    Text(
                        text = "+",
                        color = Color.Red,
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // FOOTER
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = { myMap?.animateCamera(CameraUpdateFactory.zoomOut()) }) {
                        Text("Zoom Out")
                    }
                    
                    Button(onClick = { 
                        // Drop a Pothole pin at the center crosshair
                        myMap?.let { map ->
                            val centerPoint = map.cameraPosition.target
                            map.addMarker(MarkerOptions().position(centerPoint).title("Reported Pothole"))
                        }
                    }) {
                        Text("Marker")
                    }
                    
                    Button(onClick = { myMap?.animateCamera(CameraUpdateFactory.zoomIn()) }) {
                        Text("Zoom In")
                    }
                }
            }
        }
    }
}