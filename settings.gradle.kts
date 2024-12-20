fun MavenArtifactRepository.url(s: String) {
}

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") } // Thêm JitPack repository vào danh sách các kho lưu trữ Maven mà Gradle sử dụng để tìm kiếm và tải xuống các dependency cho project
    }
}

rootProject.name = "Road_Pothole_Detection_13"
include(":app")
 