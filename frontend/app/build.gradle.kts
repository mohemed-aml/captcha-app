plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.yourpackage.captchapp"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.yourpackage.captchapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    signingConfigs {
        create("release") {
            keyAlias = "my-key-alias"
            keyPassword = "keyPassword"
            storeFile = file("/home/amal/AndroidStudioProjects/CaptchApp/my-release-key.jks")
            storePassword = "storePassword"
        }
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core);

    // Material Design Components
    implementation("com.google.android.material:material:1.9.0")
    // Fragment support
    implementation("androidx.fragment:fragment:1.6.1")

    // Glide for image loading
    implementation ("com.github.bumptech.glide:glide:4.11.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.11.0")

    // OkHttp3 for network requests
    implementation ("com.squareup.okhttp3:okhttp:4.9.1")
}