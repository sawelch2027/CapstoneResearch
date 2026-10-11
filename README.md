# Capstone Research: Map & Location Services Prototype

## Project Overview
This repository contains a minimalist Proof of Concept Android application built as part of the initial research milestone for our future pothole detection app. 

The primary goal of this milestone was to research and implement the fundamental technologies required for location-based services, specifically focusing on map rendering, GPS location tracking, and interactive markers using modern Android development practices (Without paying for API).

## Technologies Used
* **Language:** Kotlin
* **UI Framework:** Jetpack Compose (Declarative UI)
* **Build System / Package Manager:** Gradle (Kotlin DSL)
* **Map Engine:** MapLibre SDK (`org.maplibre.gl`)

## Key Features Implemented
1. **Interactive Vector Maps:** Displays a high-performance vector map using MapLibre without the need for API keys.
2. **GPS Integration:** Requests `ACCESS_FINE_LOCATION` permissions and dynamically centers the map camera on the user's current coordinates.
3. **Interactive Pin Management:** 
   * **Drop Pins:** Users can tap anywhere on the map to drop a custom marker.
   * **Delete Pins:** Users can tap an existing marker to instantly remove it from the map.
4. **Custom Camera Controls:** Implemented Jetpack Compose Floating Action Buttons to programmatically control the camera's zoom state.

## Research Hurdles & Solutions

During the research and development phase, several significant technical hurdles were encountered and overcome:

### 1. The OpenStreetMap (OSMDroid) 403 Forbidden Block
* **The Problem:** The initial approach utilized the OSMDroid library. However, the OpenStreetMap tile servers actively blocked the application's network requests, resulting in a map filled with "Access Blocked" tiles (HTTP 403 Forbidden).
* **The Investigation:** It was discovered that OSM enforces strict security and anti-spam policies. Android's default unencrypted HTTP blocking required the creation of a custom `network_security_config.xml`. Furthermore, OSM requires a highly specific and unique `User-Agent` header to prevent rate-limiting generic apps. 
* **The Solution / Pivot:** Even after configuring the security files and User-Agents, aggressive IP caching on the testing network prevented OSM from loading reliably. The architecture was pivoted to **MapLibre**, an open-source mapping engine that utilizes modern vector tiles, offers significantly better performance, and avoids the restrictive blocking policies of OSM, proving to be a much more viable solution for the final Capstone project.

### 2. Gradle Environment Variable Conflicts
* **The Problem:** The Android project completely failed to compile initially due to a Gradle service error: `Several environment variables and/or system properties contain different paths to the Android Preferences folder.`
* **The Solution:** The local Windows machine had conflicting system environment variables (`ANDROID_PREFS_ROOT` and `ANDROID_USER_HOME`). The build environment was debugged via PowerShell, the conflicting variables were wiped from the registry, and the Gradle daemon was flushed and restarted to allow the build to proceed. 

## Conclusion
This milestone successfully proved out the core fundamentals needed for the final project. The integration of MapLibre via Gradle, combined with the declarative UI of Jetpack Compose, provides a highly scalable, free, and robust foundation for building the forthcoming pothole detection system.
