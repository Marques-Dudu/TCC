plugins {
    alias(libs.plugins.android.application)

    id("com.google.gms.google-services")



}

android {
    namespace = "tcc.etec.franco.dstarde.befghl.safewayapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "tcc.etec.franco.dstarde.befghl.safewayapp"
        minSdk = 25
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
    buildFeatures {
        viewBinding = true
    }


    // Ativando o ViewBinding: Biblioteca para referenciar os fragments XML aos fragments Java
    viewBinding{
        enable = true

    }
}


dependencies {



    implementation ("com.google.firebase:firebase-auth:23.2.0")
    implementation ("androidx.credentials:credentials:1.5.0")
    implementation ("androidx.credentials:credentials-play-services-auth:1.5.0")
    implementation ("com.google.android.libraries.identity.googleid:googleid:1.1.1")
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    implementation("com.android.volley:volley:1.2.1")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation(libs.legacy.support.v4)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.play.services.maps)
    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)


    // Dependencia para acessar a localizacao do usuario
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))

    // Importando o FireBase analises
    implementation("com.google.firebase:firebase-analytics")

    // Importando Firebase Autenticator
    implementation ("com.google.firebase:firebase-auth")

    // Importando o FireBase Firestore
    implementation ("com.google.firebase:firebase-firestore")

    // Biblioteca para a insercao de imagens no banco de dados Firestore
    implementation ("com.google.firebase:firebase-storage")

    // Bibliotecas para tratamento de imagens vindas da internet
    implementation ("com.github.bumptech.glide:glide:4.16.0")

    //importando a biblioteca do Open StreetMap
    //implementation ("org.osmdroid:osmdroid-android:6.1.16")

    //Importando a biblioteca do Google Maps
        implementation("com.google.android.gms:play-services-maps:18.2.0")

    // Biblioteca para personalizar imageView
    implementation("com.google.android.material:material:1.12.0")


    // dependencia para resolver o problema de lentidao da foto
    implementation("com.google.firebase:firebase-appcheck-debug:18.0.0")

    // biblioteca para recorte da imagem
    implementation ("com.github.yalantis:ucrop:2.2.8")

    implementation ("androidx.recyclerview:recyclerview:1.2.1")
    implementation ("androidx.cardview:cardview:1.0.0")  // Para usar CardView nos itens da RecyclerView



}
