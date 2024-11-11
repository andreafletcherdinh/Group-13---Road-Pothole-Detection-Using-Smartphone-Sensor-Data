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
    implementation(libs.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("androidx.fragment:fragment-ktx:1.5.2")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation  ("org.mapsforge:mapsforge-core:0.18.0")
    implementation  ("org.mapsforge:mapsforge-map:0.18.0")
    implementation  ("org.mapsforge:mapsforge-map-reader:0.18.0")
    implementation  ("org.mapsforge:mapsforge-themes:0.18.0")
    implementation  ("net.sf.kxml:kxml2:2.3.0")
    implementation  ("org.mapsforge:mapsforge-map-android:0.18.0")
    implementation  ("com.caverock:androidsvg:1.4")
    implementation  ("org.mapsforge:mapsforge-core:0.18.0")
    implementation  ("org.mapsforge:mapsforge-poi:0.18.0")
    implementation  ("org.mapsforge:mapsforge-poi-android:0.18.0")
    implementation  ("org.mapsforge:sqlite-android:0.18.0")
    implementation  ("org.mapsforge:sqlite-android:0.18.0:natives-armeabi-v7a")
    implementation  ("org.mapsforge:sqlite-android:0.18.0:natives-arm64-v8a")
    implementation  ("org.mapsforge:sqlite-android:0.18.0:natives-x86")
    implementation  ("org.mapsforge:sqlite-android:0.18.0:natives-x86_64")






}