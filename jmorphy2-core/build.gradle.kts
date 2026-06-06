description = "Java port of pymorphy2"

version = getLibraryVersion()

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation("commons-io:commons-io:${Versions.commonsIo}")
    implementation("org.noggit:noggit:${Versions.noggit}")
    implementation("commons-codec:commons-codec:${Versions.commonsCodec}")

    testImplementation("junit:junit:${Versions.junit}")
    testImplementation(project(":jmorphy2-dicts-ru"))
    testImplementation(project(":jmorphy2-dicts-uk"))
}

tasks.withType<Test> {
    exclude("**/*Benchmark*")
    outputs.upToDateWhen { false }
    testLogging {
        showStandardStreams = true
    }
}

tasks.register<Jar>(
    name = "jarTest"
) {
    dependsOn("testClasses")
    archiveClassifier.set("tests")
    from(sourceSets["test"].output)
}

configurations.register("tests") {
    extendsFrom(configurations.testImplementation.get())
}

artifacts {
    add("tests", tasks.getByName("jarTest"))
}
