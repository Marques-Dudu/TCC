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
        //Adiciona o repositório do JitPack, que permite importar bibliotecas diretamente de repositórios GitHub
        maven(url = "https://jitpack.io")
    }

}

rootProject.name = "SafeWay App"
include(":app")
