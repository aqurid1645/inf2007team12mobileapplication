plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.inf2007team12mobileapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.inf2007team12mobileapplication"
        minSdk = 31
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    //weiwen
//    packaging {
//        resources {
//            excludes += "/META-INF/{AL2.0,LGPL2.1}"
//        }
//    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation("androidx.compose.ui:ui:1.6.4")
    implementation("androidx.compose.material:material:1.6.4")
    implementation("androidx.compose.material:material-icons-core:1.6.4")
    implementation("androidx.compose.material:material-icons-extended:1.6.4")
    implementation("com.google.firebase:firebase-firestore-ktx:24.11.0")
    implementation("androidx.room:room-common:2.6.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    // Dagger - Hilt
    implementation("com.google.dagger:hilt-android:2.50")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    kapt("com.google.dagger:hilt-compiler:2.50")
    kapt("com.google.dagger:dagger-compiler:2.50")
    //Google auth dependency
    implementation("com.google.android.gms:play-services-auth:21.0.0")
    //bar code scanner
    implementation ("com.google.android.gms:play-services-code-scanner:16.0.0-beta3")
    implementation("androidx.compose.runtime:runtime-livedata:1.6.0")

    //Google auth dependency
    implementation("com.google.android.gms:play-services-auth:21.0.0")

    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:32.7.4"))
    implementation ("com.google.firebase:firebase-auth")
    implementation ("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-firestore-ktx")


    // Declare the dependency for the Cloud Firestore library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-firestore")


    // Room components

    implementation ("androidx.room:room-runtime:2.6.1")
    annotationProcessor ("androidx.room:room-compiler:2.6.1")
    implementation ("androidx.room:room-ktx:2.6.1")
    implementation ("com.mcxiaoke.volley:library-aar:1.0.0")

    // To use Kotlin annotation processing tool (kapt)
    kapt ("androidx.room:room-compiler:2.6.1")


}