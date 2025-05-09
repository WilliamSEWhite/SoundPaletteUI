plugins {
    id("com.android.application")
}


android {
    namespace = "com.soundpaletteui"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.soundpaletteui"
        minSdk = 23
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
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.1")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.navigation:navigation-ui:2.7.7")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation("androidx.test.espresso:espresso-intents:3.6.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")
    implementation("com.google.firebase:firebase-firestore:25.1.2") {
        exclude(group = "com.google.protobuf", module = "protobuf-lite")
    }
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.test.espresso:espresso-contrib:3.6.1") {
        exclude(group = "com.google.protobuf", module = "protobuf-lite")
    }
    implementation("androidx.activity:activity:1.8.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation("pl.droidsonroids.gif:android-gif-drawable:1.2.29")
    implementation("com.github.skydoves:colorpickerpreference:2.0.6")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test:rules:1.5.0")
}
