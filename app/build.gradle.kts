import org.gradle.internal.impldep.org.junit.experimental.categories.Categories.CategoryFilter.exclude

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.road_pothole_detection_13"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.road_pothole_detection_13"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.preference)
    implementation(libs.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("org.maplibre.gl:android-sdk:9.6.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")

//  implementation ("com.mapbox.mapboxsdk:mapbox-android-plugin-annotation-v9:0.9.0")
//  implementation ("com.graphhopper:graphhopper-core:10.0")
//  implementation ("com.graphhopper:graphhopper-reader-osm:10.0")
//  implementation ("com.graphhopper:graphhopper-web-api:10.0")

    // Thêm một thư viện bên ngoài vào đồ án Pothole Detection.
    // Cú pháp "com.github.User:Repo:Tag":
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
}