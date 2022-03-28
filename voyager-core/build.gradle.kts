import org.jetbrains.compose.compose

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("com.vanniktech.maven.publish")
}

setupModuleForComposeMultiplatform()

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                compileOnly(compose.runtime)
                compileOnly(libs.composeMultiplatform.runtimeSaveable)
                implementation(libs.coroutines.core)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(kotlin("test"))
                implementation(libs.coroutines.test)
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(libs.junit.api)
                runtimeOnly(libs.junit.engine)
            }
        }

        val androidTest by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(libs.junit.api)
                runtimeOnly(libs.junit.engine)
            }
        }
    }
}
