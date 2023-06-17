plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") // Required to use @Resource annotation
    id("com.android.library")
    id("org.jetbrains.compose")
    id("com.vanniktech.maven.publish")
}

setupModuleForComposeMultiplatform(fullyMultiplatform = true)

android {
    namespace = "cafe.adriel.voyager.routing.typesafe"
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(projects.voyagerRouting)
                compileOnly(compose.runtime)
                compileOnly(libs.composeMultiplatform.runtimeSaveable)
                implementation(libs.kotlin.routing.resources.stack)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.test.coroutines)
                implementation(compose.runtime)
                implementation(libs.composeMultiplatform.runtimeSaveable)
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(libs.junit.api)
                runtimeOnly(libs.junit.engine)
            }
        }

        val androidMain by getting
    }
}
