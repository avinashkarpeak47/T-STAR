plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.t_star"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.t_star"
        minSdk = 26
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
       // sourceCompatibility = JavaVersion.VERSION_11
       // targetCompatibility = JavaVersion.VERSION_11
        sourceCompatibility = JavaVersion.VERSION_1_8
                targetCompatibility = JavaVersion.VERSION_1_8
    }


   buildFeatures{
       viewBinding = true
   }

    packaging {
        resources {
            excludes.add("META-INF/DEPENDENCIES")
            excludes.add("META-INF/LICENSE")
            excludes.add("META-INF/LICENSE.txt")
            excludes.add("META-INF/NOTICE")
            excludes.add("META-INF/NOTICE.txt")
        }
    }
}


dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.gridlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.messaging)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    annotationProcessor("androidx.room:room-compiler:2.6.1")
    implementation ("androidx.room:room-runtime:2.6.1")

    // Photo View 
    implementation ("com.github.chrisbanes:PhotoView:2.3.0")
    implementation ("com.google.firebase:firebase-messaging:23.4.0")

    implementation ("com.google.firebase:firebase-firestore:24.11.1")
    implementation ("com.google.firebase:firebase-analytics:22.0.2")
    implementation ("com.google.firebase:firebase-bom:33.5.1")
    implementation ("com.google.firebase:firebase-firestore")

    //Translator ML Kit
    implementation ("com.google.mlkit:translate:17.0.2")


    //CLoudnary Dependency
    implementation ("com.cloudinary:cloudinary-android:2.5.0")
    implementation ("com.cloudinary:cloudinary-android:2.3.1")

    implementation ("de.hdodenhof:circleimageview:3.1.0")
    // ---------- ANDROID CORE ----------
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.activity:activity:1.9.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation ("androidx.gridlayout:gridlayout:1.0.0")

    // ---------------- NAVIGATION ----------------
    implementation("androidx.navigation:navigation-fragment:2.8.3")
    implementation("androidx.navigation:navigation-ui:2.8.3")

    // ---------- FIREBASE (USING BOM) ----------
    implementation(platform("com.google.firebase:firebase-bom:34.0.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-analytics")

    // ---------- GOOGLE SIGN-IN ----------
    implementation("com.google.android.gms:play-services-auth:21.0.0")
    implementation("com.google.android.gms:play-services-auth:21.0.0")

    // ---------- GOOGLE DRIVE REST API ----------
    implementation("com.google.api-client:google-api-client-android:1.34.1")
    implementation("com.google.api-client:google-api-client-gson:1.35.0")
    implementation("com.google.apis:google-api-services-drive:v3-rev20230822-2.0.0")

    implementation("com.google.http-client:google-http-client-android:1.43.3")


    // ---------- IMAGE ----------
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")


    // ---------- OPTIONAL ----------
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
// OkHttp (recommended)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // ---------- TEST ----------
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.google.api-client:google-api-client-android:1.34.1")
    implementation("com.google.apis:google-api-services-drive:v3-rev20230822-2.0.0")
}


/*dependencies {

    // ---------------- CORE ANDROIDX ----------------
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.activity:activity:1.9.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.gridlayout:gridlayout:1.0.0")

    // ---------------- MATERIAL DESIGN ----------------
    implementation("com.google.android.material:material:1.12.0")

    // ---------------- NAVIGATION ----------------
    implementation("androidx.navigation:navigation-fragment:2.8.3")
    implementation("androidx.navigation:navigation-ui:2.8.3")

    // ---------------- FIREBASE (USING BOM – CORRECT WAY) ----------------
    implementation(platform("com.google.firebase:firebase-bom:34.0.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-analytics")

    // ---------------- GOOGLE SIGN-IN ----------------
    implementation("com.google.android.gms:play-services-auth:21.0.1")

    // ---------------- IMAGE LOADING ----------------
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // ---------------- QR CODE ----------------
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")

    // ---------------- UI COMPONENTS ----------------
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // ---------------- GOOGLE DRIVE REST API ----------------
    implementation("com.google.api-client:google-api-client-android:1.35.0")
    implementation("com.google.apis:google-api-services-drive:v3-rev20230822-2.0.0")

    // ---------------- TESTING ----------------
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
*/


