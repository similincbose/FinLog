import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
    id("app.cash.sqldelight")
}

/** Versions are declared inline, as in the rest of our projects **/
val koinVersion = "4.2.2"
val coroutinesVersion = "1.11.0"
val settingsVersion = "1.3.0"
val navigationVersion = "2.9.2"
val lifecycleVersion = "2.11.0"
val dateTimeVersion = "0.8.0"
val sqlDelightVersion = "2.1.0"

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    // No iosX64: Compose Multiplatform 1.12 dropped the Intel simulator target.
    // iosArm64 is the device, iosSimulatorArm64 the Apple Silicon simulator.
    listOf(iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            // SQLDelight's native-driver links against the system sqlite3 C API;
            // the static framework doesn't pull it in on its own.
            linkerOpts.add("-lsqlite3")
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.materialIconsExtended)

            // Navigation & ViewModel
            implementation("org.jetbrains.androidx.navigation:navigation-compose:$navigationVersion")
            implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
            implementation("org.jetbrains.androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion")

            // Koin - dependency injection
            implementation("io.insert-koin:koin-core:$koinVersion")
            implementation("io.insert-koin:koin-compose:$koinVersion")
            implementation("io.insert-koin:koin-compose-viewmodel:$koinVersion")

            // Coroutine
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")

            // Date & time
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:$dateTimeVersion")

            // Multiplatform Settings - shared preference
            implementation("com.russhwolf:multiplatform-settings:$settingsVersion")
            implementation("com.russhwolf:multiplatform-settings-no-arg:$settingsVersion")

            // SQLDelight - local database
            implementation("app.cash.sqldelight:runtime:$sqlDelightVersion")
            implementation("app.cash.sqldelight:coroutines-extensions:$sqlDelightVersion")
        }

        androidMain.dependencies {
            implementation(compose.preview)
            implementation("androidx.activity:activity-compose:1.13.0")
            implementation("androidx.core:core-ktx:1.17.0")
            implementation("io.insert-koin:koin-android:$koinVersion")
            implementation("app.cash.sqldelight:android-driver:$sqlDelightVersion")
        }

        iosMain.dependencies {
            implementation("app.cash.sqldelight:native-driver:$sqlDelightVersion")
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

sqldelight {
    databases {
        create("FinlogDatabase") {
            packageName.set("me.riafy.finlog.database")
        }
    }
}

android {
    // Follows the Kotlin source package, not the applicationId. The two are
    // independent, and renaming the source package is a separate change.
    namespace = "me.riafy.finlog"
    compileSdk = 37
    compileSdkMinor = 1

    defaultConfig {
        applicationId = "dev.similin.finlog"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
    }

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")

    buildFeatures {
        buildConfig = true
    }
    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    packaging {
        resources {
            excludes += setOf(
                "META-INF/LICENSE.md",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
                "META-INF/DEPENDENCIES"
            )
        }
    }
    dependencies {
        debugImplementation(compose.uiTooling)
    }
}
