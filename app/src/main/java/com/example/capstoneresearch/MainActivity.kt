package com.example.capstoneresearch

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

            Box(modifier = Modifier.fillMaxSize()) {
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

                // Zoom buttons
                Column(
                    modifier = Modifier.align(Alignment.CenterEnd).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(onClick = { myMap?.animateCamera(CameraUpdateFactory.zoomIn()) }) {
                        Text("Zoom In")
                    }
                    Button(onClick = { myMap?.animateCamera(CameraUpdateFactory.zoomOut()) }) {
                        Text("Zoom Out")
                    }
                }
            }
        }
    }
}
