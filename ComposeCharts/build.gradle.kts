plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    `maven-publish`
}


afterEvaluate {
    publishing {
        publications {
            // Creates a Maven publication called "release".
            create<MavenPublication>("maven") {
                // Applies the component for the release build variant.
                from(components.findByName("release"))

                // You can then customize attributes of the publication as shown below.
                groupId = "com.github.ehsannarmani"
                artifactId = "compose-charts"
            }
        }
    }
}
android {
    namespace = "ir.ehsannarmani.compose_charts"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class).all {
        kotlinOptions.freeCompilerArgs = listOf("-Xcontext-receivers")
    }
}

dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
}