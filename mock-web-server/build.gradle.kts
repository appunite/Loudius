@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("java-library")
    alias(libs.plugins.kotlin.jvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(libs.bundles.okhttp)
    implementation(libs.bundles.mock)
    implementation(libs.bundles.strikt)
    implementation("io.github.oshai:kotlin-logging-jvm:5.1.0")

    testImplementation(libs.junit)
}
