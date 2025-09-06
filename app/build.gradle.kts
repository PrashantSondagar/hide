plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.hide"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.hide"
        minSdk = 24
        targetSdk = 34 // Update as necessary
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

    kotlinOptions {
        jvmTarget = "11"
    }

    packagingOptions {
        pickFirsts += "**/lib/x86_64/libopencv_java3.so" // Example for packaging options
    }
}

dependencies {
    // Core Android dependencies
    implementation("androidx.core:core-ktx:1.6.0")
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.activity:activity-ktx:1.3.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.0")

    // Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation(libs.androidx.activity)
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")

    // Add other dependencies as needed
    // testImplementation("junit:junit:4.13.2")
    // androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}
