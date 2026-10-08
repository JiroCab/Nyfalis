import org.gradle.kotlin.dsl.ivy
val mindustryVersion = "v160.6"

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }

        //Downloads the dependencies JAR file from Mindustry releases; does not use any real repository. Surprisingly, this is the most reliable option.
        ivy{
            url = uri("https://github.com/")
            patternLayout{
                val pattern = when(mindustryVersion){
                    "latest" -> "/[organisation]/[module]/releases/[revision]/download/dependencies.jar" //latest stable release
                    "be" -> "/[organisation]/[module]/releases/download/master/[revision].jar" //latest commit (BE)
                    else -> "/[organisation]/[module]/releases/download/[revision]/dependencies.jar" //specific release
                }
                val path = pattern.substring(0, pattern.lastIndexOf('/'))

                artifact("$path/[classifier].jar")
                artifact(pattern.replaceFirst(Regex("""\.jar$"""), "(-[classifier]).jar"))
            }
            metadataSources{ artifact() }

            content{
                if(mindustryVersion == "be"){
                    //BE artifact version is always 'latest'
                    includeVersion("Anuken", "MindustryBuilds", "latest")
                }else{
                    includeVersion("Anuken", "Mindustry", mindustryVersion)
                }
            }
        }
    }
}
