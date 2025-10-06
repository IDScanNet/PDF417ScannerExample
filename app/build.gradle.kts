plugins {
    id("com.android.application")
}

android {
    namespace = "net.idscan.android.pdf417scannerexample"

    compileSdk = 36

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    defaultConfig {
        minSdk = 21
        targetSdk = 36

        applicationId = "net.idscan.android.pdf417scannerexample"
        versionCode = 400301
        versionName = "4.3.1"
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation("androidx.core:core:1.17.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.13.0")
    implementation("net.idscan.components.android:scanpdf417:4.3.1")
}