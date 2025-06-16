import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { input ->
        localProperties.load(input)
    }
}

android {
    namespace = "com.example.ozzymaptest"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ozzy.ozzymaptest"
        minSdk = 24
        targetSdk = 35
        versionCode = 3
        versionName = "1.03"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        manifestPlaceholders["MAPS_API_KEY_PLACEHOLDER"] = localProperties.getProperty("MAPS_API_KEY")
    }

    lint {
        disable += "NullSafeMutableLiveData"
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            isShrinkResources = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3.android)

    implementation (libs.androidx.hilt.navigation.compose)
    implementation (libs.androidx.hilt.common)
    implementation (libs.hilt.android)
    ksp (libs.hilt.compiler)
    ksp (libs.androidx.hilt.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":core:presentation"))

    implementation(project(":featureMaps:domain"))
    implementation(project(":featureMaps:data"))
    implementation(project(":featureMaps:presentation"))
    implementation(project(":featureHome:presentation"))
}