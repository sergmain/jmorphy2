description = "NLP based on pymorphy2 dictionaries"

version = getLibraryVersion()

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    api(project(":jmorphy2-core"))

    testImplementation("junit:junit:${Versions.junit}")
    testImplementation(project(":jmorphy2-dicts-ru"))
    testImplementation(project(path = ":jmorphy2-core", configuration = "tests"))
 
//   testImplementation(project(":jmorphy2-core").dependencyProject.sourceSets["test"].output)
}

tasks.withType<Test> {
    exclude("**/*Benchmark*")
    outputs.upToDateWhen { false }
    testLogging {
        showStandardStreams = true
    }
}