plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.detekt) apply false
}
tasks.register<Copy>("installGitHooks") {
    from(file("$rootDir/scripts/pre-commit.sh"))
    into(file("$rootDir/.git/hooks"))
    rename { "pre-commit" }
    filePermissions {
        unix(493)
    }
}

// Авто-установка хуков при сборке
tasks.named("prepareKotlinBuildScriptModel") {
    dependsOn("installGitHooks")
}