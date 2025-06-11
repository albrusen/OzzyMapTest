plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.maps.presentation"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    lint {
        disable += "NullSafeMutableLiveData"
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
    kotlinOptions {
        jvmTarget = "11"
    }


    buildFeatures {
        compose = true
    }
    composeCompiler {
        reportsDestination = layout.buildDirectory.dir("compose_compiler")
//        stabilityConfigurationFile = rootProject.layout.projectDirectory.file("stability_config.conf")
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.material3.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.androidx.navigation.compose)
    implementation (libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.ui.tooling.preview.android)

    implementation(libs.play.services.maps)
    implementation(libs.maps.compose)
    implementation (libs.maps.compose)
    // Optionally, you can include the Compose utils library for Clustering,
    // Street View metadata checks, etc.
    implementation (libs.maps.compose.utils)

    // Optionally, you can include the widgets library for ScaleBar, etc.
    implementation (libs.maps.compose.widgets)

    implementation (libs.androidx.hilt.common)
    implementation (libs.hilt.android)
    ksp (libs.hilt.compiler)
    ksp (libs.androidx.hilt.compiler)

    implementation(project(":featureMaps:domain"))
}