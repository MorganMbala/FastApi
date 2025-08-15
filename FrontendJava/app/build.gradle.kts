plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.frontendjava"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.frontendjava"
        minSdk = 29
        targetSdk = 35
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.retrofit)
    implementation(libs.retrofitGson)
    implementation(libs.okhttpLogging)
    implementation(libs.recyclerview)
    implementation(libs.cardview)
    implementation(libs.glide)
    annotationProcessor(libs.glideCompiler)
    implementation(libs.swiperefreshlayout)
    implementation(libs.shimmer)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}