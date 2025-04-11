plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.bancusoft.pdfreader"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.bancusoft.pdfreader"
        minSdk = 24
        targetSdk = 35
        versionCode = 5
        versionName = "5.0"

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}



dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("com.tom-roush:pdfbox-android:2.0.27.0")
    // Librării pentru PDF Reader
    implementation("com.github.mhiew:android-pdf-viewer:3.2.0-beta.3")
    implementation("androidx.viewpager2:viewpager2:1.1.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("com.github.chrisbanes:PhotoView:2.3.0")


}