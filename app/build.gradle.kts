plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.googleService)
}

android {
    namespace = "com.example.slopshop"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.slopshop"
        minSdk = 23
        //noinspection OldTargetApi
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures{
        viewBinding=true
    }
}

dependencies {


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    implementation(libs.firebaseAuntentication) //Animaciones
    implementation(libs.lottie) //Autentificacion con Firebase
    implementation(libs.firebaseDatabase) //Base de datos de firebase
    implementation(libs.imagePicker)//Recortar y ajustar imagenes
    implementation(libs.glide)//Leer imagenes nube
    implementation(libs.firebaseStorage)//BD para imagenes
    implementation(libs.autenteticationGoogle)//Autentificación Google


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}