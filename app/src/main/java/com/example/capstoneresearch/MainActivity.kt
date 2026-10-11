package com.example.capstoneresearch

import android.Manifest
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.maplibre.android.MapLibre
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        MapLibre.getInstance(this)

        setContent {
            var myMap by remember { mutableStateOf<MapLibreMap?>(null) }

            // Ask for location permission on startup
            val permReq = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}
            LaunchedEffect(Unit) {
                permReq.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }

            // Screen layout with Header, Map, and Footer
            Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
                
                // HEADER
                Text(
                    text = "DropaMarker",
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                // MAP AREA (Takes up remaining space)
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    AndroidView(
                        factory = { ctx ->
                            MapView(ctx).apply {
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
                                        val marker = MarkerOptions().position(pt).title("Pin")
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
                        // Drop a pin at the center of the map view when clicked
                        myMap?.let { map ->
                            val centerPoint = map.cameraPosition.target
                            map.addMarker(MarkerOptions().position(centerPoint).title("Pin"))
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