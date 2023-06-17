import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi
import org.jetbrains.compose.desktop.application.tasks.AbstractNativeMacApplicationPackageTask
import org.jetbrains.compose.experimental.dsl.IOSDevices
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") // required for type-safe routing
    id("com.android.application")
    id("org.jetbrains.compose")
}

setupModuleForComposeMultiplatform(
    fullyMultiplatform = true,
    withKotlinExplicitMode = false,
    // this is required for the Compose iOS Application DSL expect a `uikit` target name.
    iosPrefixName = "uikit"
)

kotlin {
    val macOsConfiguation: KotlinNativeTarget.() -> Unit = {
        binaries {
            executable {
                entryPoint = "main"
                freeCompilerArgs += listOf(
                    "-linker-option", "-framework", "-linker-option", "Metal"
                )
            }
        }
    }
    macosX64(macOsConfiguation)
    macosArm64(macOsConfiguation)
    val uikitConfiguration: KotlinNativeTarget.() -> Unit = {
        binaries {
            executable() {
                entryPoint = "main"
                freeCompilerArgs += listOf(
                    "-linker-option", "-framework", "-linker-option", "Metal",
                    "-linker-option", "-framework", "-linker-option", "CoreText",
                    "-linker-option", "-framework", "-linker-option", "CoreGraphics"
                )
            }
        }
    }
    iosX64("uikitX64", uikitConfiguration)
    iosArm64("uikitArm64", uikitConfiguration)
    iosSimulatorArm64("uikitSimulatorArm64", uikitConfiguration)

    js(IR) {
        browser()
        binaries.executable()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.material)
                implementation(compose.runtime)
                implementation(projects.voyagerRoutingTypesafe)
            }
        }

        val desktopMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }

        val androidMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.appCompat)
                implementation(libs.compose.activity)
                /*implementation(libs.core.ktx)
                implementation(libs.lifecycle.runtime.ktx)
                implementation(libs.activity.appcompat)
                implementation(libs.activity.compose)*/
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "cafe.adriel.voyager.sample.multiplatform.routing.AppKt"
        //mainClass = "dev.programadorthi.routing.voyager.AppKt"
        nativeDistributions {
            targetFormats(Dmg, Msi, Deb)
            packageName = "jvm"
            packageVersion = "1.0.0"
        }
    }
}

compose.desktop.nativeApplication {
    targets(kotlin.targets.getByName("macosX64"))
    distributions {
        targetFormats(Dmg)
        packageName = "RoutingVoyagerApplication"
        packageVersion = "1.0.0"
    }
}

compose.experimental {
    web.application {}

    uikit.application {
        bundleIdPrefix = "cafe.adriel.voyager"
        projectName = "MultiplatformSample"
        deployConfigurations {
            simulator("IPhone8") {
                device = IOSDevices.IPHONE_8
            }
            simulator("IPad") {
                device = IOSDevices.IPAD_MINI_6th_Gen
            }
        }
    }
}

android {
    namespace = "cafe.adriel.voyager.sample.multiplatform.routing"

    defaultConfig {
        applicationId = "cafe.adriel.voyager.sample.multiplatform.routing"
    }
}
