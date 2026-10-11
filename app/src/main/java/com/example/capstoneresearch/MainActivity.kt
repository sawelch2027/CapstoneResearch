package com.example.capstoneresearch

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.Style
import org.maplibre.android.annotations.MarkerOptions

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapLibre.getInstance(this)
        setContent { MaplibreMapScreen() }
    }
}

@Composable
fun MaplibreMapScreen() {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    LaunchedEffect(Unit) { launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION) }

    // Keep a reference to the map so we can zoom in/out with buttons
    var mapReference by remember { mutableStateOf<MapLibreMap?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                MapView(context).apply {
                    getMapAsync { map ->
                        mapReference = map
                        
                        map.setStyle(Style.Builder().fromUri("https://demotiles.maplibre.org/style.json"))
                        map.cameraPosition = CameraPosition.Builder()
                            .target(LatLng(39.8, -98.6))
                            .zoom(3.0)
                            .build()
                            
                        // Add Pin Drop Method on Map Click
                        map.addOnMapClickListener { point ->
                            map.addMarker(
                                MarkerOptions()
                                    .position(point)
                                    .title("Dropped Pin")
                            )
                            true
                        }
                        
                        // Add Pin Delete Method on Marker Click
                        map.setOnMarkerClickListener { marker ->
                            map.removeMarker(marker)
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

        // Zoom In and Out Buttons overlay
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(16.dp)
        ) {
            Button(onClick = {
                mapReference?.animateCamera(CameraUpdateFactory.zoomIn())
            }) {
                Text("+")
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(onClick = {
                mapReference?.animateCamera(CameraUpdateFactory.zoomOut())
            }) {
                Text("-")
            }
        }
    }
}
