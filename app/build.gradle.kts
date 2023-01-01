plugins {
    id("com.android.application")
    id("dev.rikka.tools.refine")
}

val versionMajor = 0
val versionMinor = 3
val versionPatch = 0

val releaseStoreFile: String? by rootProject
val releaseStorePassword: String? by rootProject
val releaseKeyAlias: String? by rootProject
val releaseKeyPassword: String? by rootProject

android {
    namespace = "com.unbound.patches"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.unbound.patches"
        minSdk = 31
        targetSdk = 33
        versionCode = versionMajor * 100000 + versionMinor * 1000 + versionPatch
        versionName = "${versionMajor}.${versionMinor}.${versionPatch}"
    }

    lint {
        disable += "AppCompatResource"
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    signingConfigs {
        create("release") {
            releaseStoreFile?.also {
                storeFile = rootProject.file(it)
                storePassword = releaseStorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }

    buildTypes {
        all {
            signingConfig =
                if (releaseStoreFile.isNullOrEmpty()) {
                    signingConfigs.getByName("debug")
                } else {
                    signingConfigs.getByName("release")
                }
        }
        getByName("debug") {
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        getByName("release") {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

configurations.all {
    exclude(group = "androidx.appcompat", module = "appcompat")
    exclude(group = "androidx.core", module = "core")
    exclude(group = "androidx.customview", module = "customview")
    exclude(group = "androidx.fragment", module = "fragment")
}

dependencies {
    // Sesl
    implementation("io.github.oneuiproject.sesl:appcompat:1.3.0")
    implementation("io.github.oneuiproject.sesl:material:1.4.0") {
        exclude(group = "io.github.oneuiproject.sesl", module = "viewpager2")
    }
    // AndroidX
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.viewpager2:viewpager2:1.0.0") {
        exclude(group = "androidx.recyclerview", module = "recyclerview")
    }
    // Xposed
    compileOnly("de.robv.android.xposed:api:82")
    implementation("org.lsposed.hiddenapibypass:hiddenapibypass:4.3")
    // Rikka
    implementation("dev.rikka.tools.refine:runtime:3.1.1")

    compileOnly(project(":stub"))
}
