import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.maven.publish)
}
mavenPublishing{
    coordinates(
        groupId = "io.github.ehsannarmani",
        artifactId = "compose-charts",
        version = "0.2.5"
    )
    pom{
        name.set("Compose Charts")
        description.set("https://github.com/ehsannarmani/ComposeCharts")
        inceptionYear.set("2024")
        url.set("https://github.com/ehsannarmani/ComposeCharts")

        licenses {
            license {
                name.set("MIT")
                url.set("https://opensource.org/licenses/MIT")
            }
        }

        // Specify developers information
        developers {
            developer {
                id.set("ehsannarmani")
                name.set("Ehsan Narmani")
                email.set("ehsan.enk.narmani@gmail.com")
            }
        }

        // Specify SCM information
        scm {
            url.set("https://github.com/ehsannarmani/ComposeCharts")
        }
    }
    publishToMavenCentral()
    signAllPublications()
}
kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName = "compose-charts"
        browser {
            commonWebpackConfig {
                outputFileName = "compose-charts.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static(project.projectDir.path)
                }
            }
        }
        binaries.library()
    }

    js {
        outputModuleName = "compose-charts"
        browser {
            commonWebpackConfig {
                outputFileName = "compose-charts.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static(project.projectDir.path)
                }
            }
        }
        binaries.library()
    }

    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
        }
        publishLibraryVariants("release","debug")
    }

    jvm("desktop")
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.foundation)
            // @Preview annotation isn't currently used, if that changes uncomment this line
            // and add a dependency on ui-tooling to the androidDebug variant
            //implementation(libs.compose.ui.tooling.preview)
        }
    }
}

android {
    namespace = "ir.ehsannarmani.compose_charts"
    compileSdk = 36

    defaultConfig {
        minSdk = 23
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
}
