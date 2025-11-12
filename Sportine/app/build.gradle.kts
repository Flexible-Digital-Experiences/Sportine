plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.sportine"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.sportine"
        minSdk = 24
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
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
    // Para Retrofit (la "radio" para hacer llamadas API)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // Dependencia para grafica
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    // Para Gson (el "traductor" de JSON a clases Java)
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
}