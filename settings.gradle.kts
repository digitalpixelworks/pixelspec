pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}


/**
 * Use a local copy of nextlib, if it exists by uncommenting the below lines
 * Assuming, that pixelspec and nextlib have the same parent directory.
 * If this is not the case, please change the nextLibDirPath.
 */

//val nextLibDirPath = "../nextlib"
//if (File(nextLibDirPath).exists()) {
//    includeBuild(nextLibDirPath) {
//        dependencySubstitution {
//            substitute(module("com.github.anilbeesetti.nextlib:nextlib-media3ext")).using(project(":media3ext"))
//            substitute(module("com.github.anilbeesetti.nextlib:nextlib-mediainfo")).using(project(":mediainfo"))
//        }
//    }
//}

rootProject.name = "Pixel Spec"
include(":app")
