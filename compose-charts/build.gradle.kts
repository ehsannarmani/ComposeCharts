import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.maven.publish)
}
mavenPublishing{
    coordinates(
        groupId = "io.github.ehsannarmani",
        artifactId = "compose-charts",
        version = "1.0.0"
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
        binaries.executable()
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
        binaries.executable()
    }

    android {
        namespace = "ir.ehsannarmani.compose_charts"
        compileSdk = 37
        minSdk = 21
    }

    jvm("desktop")
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
