import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.crashlytics)
    alias(libs.plugins.gms)
}

// Load signing properties if available.
val signingPropertiesFile = rootProject.file("signing.properties")
val signingProperties = Properties()
if (signingPropertiesFile.exists()) {
    FileInputStream(signingPropertiesFile).use { stream ->
        signingProperties.load(stream)
    }
}

// Access the extra properties safely
val admobAppId: String by rootProject.extra
val admobBannerId: String by rootProject.extra

android {
    namespace = "io.android.pixelspec"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        applicationId = "io.android.pixelspec"

        // Retrieve version components from environment variables or use default values
        val vMajor = System.getenv("VERSION_MAJOR") ?: "1"
        var vMinor = System.getenv("VERSION_MINOR") ?: "01"
        var buildNumber = System.getenv("BUILD_NUMBER") ?: "0001"

        try {
            vMinor = String.format("%02d", vMinor.toInt())
            buildNumber = String.format("%04d", buildNumber.toInt())
        } catch (ignored: NumberFormatException) {
            throw GradleException("VERSION_MAJOR, VERSION_MINOR, and BUILD_NUMBER must be integers.")
        }

        versionName = "$vMajor.$vMinor.$buildNumber"
        versionCode = (vMajor.toInt() * 1_000_000) + (vMinor.toInt() * 1_000) + buildNumber.toInt()

        resValue("string", "app_version", "$versionName ($versionCode)")
        buildConfigField(
            "String", "FILE_PROVIDIER_AUTHORITY", "APPLICATION_ID + \".file_provider\""
        )
        resValue("string", "app_provider_authority", "$applicationId.app_provider")
        resValue("string", "file_provider_authority", "$applicationId.file_provider")

        // Add to BuildConfig
        buildConfigField("String", "ADMOB_APP_ID", "\"$admobAppId\"")
        buildConfigField("String", "ADMOB_BANNER_ID", "\"$admobBannerId\"")

        // Add to manifest placeholders
        manifestPlaceholders.apply {
            put("admobAppId", admobAppId)
            put("admobBannerId", admobBannerId)
        }
    }

    // Move signingConfigs block before buildTypes
    signingConfigs {
        create("release") {
            if (signingProperties.containsKey("storeFile")) {
                storeFile = file(signingProperties["storeFile"] as String)
                storePassword = signingProperties["storePassword"] as String
                keyAlias = signingProperties["keyAlias"] as String
                keyPassword = signingProperties["keyPassword"] as String
            }
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.android.jvm.get().toInt())
        targetCompatibility = JavaVersion.toVersion(libs.versions.android.jvm.get().toInt())
    }

    kotlinOptions {
        jvmTarget = libs.versions.android.jvm.get()
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }

    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }

    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }
}

// Delay registration until after the android block is fully configured.
afterEvaluate {
    val extractedVersionName = android.defaultConfig.versionName
    val versionNameOutputFile = file("${layout.buildDirectory.get().asFile}/versionName.txt")

    tasks.register("writeVersionNameToFile") {
        group = "verification"
        description = "Writes the version name to a file."
        doFirst {
            versionNameOutputFile.parentFile?.mkdirs()
            extractedVersionName?.let { versionNameOutputFile.writeText(it) }
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtimeCompose)

    implementation(libs.google.android.material)
    implementation(libs.androidx.core.splashscreen)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.datastore.core.android)
    implementation(libs.androidx.datastore.preferences.core.android)
    ksp(libs.hilt.compiler)
    kspAndroidTest(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.accompanist.permissions)

    implementation(libs.timber)

    testImplementation(libs.junit4)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.testManifest)

    // Firebase
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.perf)

    // Ads
    implementation(libs.play.services.ads)

    // Coil
    implementation(libs.coil.kt.compose)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // If you need the core DataStore library (without Android dependencies)
    implementation(libs.datastore.core)
}