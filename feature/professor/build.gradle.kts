plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.example.bandaalana.professor"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)

    // Bibliotecas de Rede e Imagens (Retrofit, Gson e Glide)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")

    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}