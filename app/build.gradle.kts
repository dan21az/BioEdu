plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.dan21az.bioedu"
    compileSdk = 36

    flavorDimensions.add("version")
    productFlavors {
        create("modern") {
            dimension = "version"
            minSdk = 24
            versionNameSuffix = "-modern"
            isDefault = true
        }
        create("legacy") {
            dimension = "version"
            minSdk = 21
            versionNameSuffix = "-legacy"
        }
    }

    defaultConfig {
        vectorDrawables.useSupportLibrary = true
        applicationId = "com.dan21az.bioedu"
        minSdk = 21
        targetSdk = 36
        versionCode = 1
        versionName = "v0.1-alpha"
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
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_24
        targetCompatibility = JavaVersion.VERSION_24
    }

}

configurations.all {
    exclude(group = "com.intellij", module = "annotations")
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.room.compiler)
    implementation(libs.androidx.core.splashscreen)
    "modernImplementation"(libs.activity)
    "legacyImplementation"("androidx.activity:activity:1.11.0")
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation(libs.constraintlayout)
    implementation(libs.gridlayout)
    implementation(libs.cardview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
