description = "Ukrainian pymorphy2 dictionaries"

val dictsVersion = "2.4.1.1.1460299261"
version = "${getLibraryVersion()}-$dictsVersion"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

val fetchDicts = tasks.register<Pymorphy2Dicts>("fetchPymorphy2Dicts") {
    lang.set("uk")
    version.set(dictsVersion)
    md5sum.set("f193a4ac7a8e6124e6fd8846f06ccca0")
}

tasks.named("processResources") {
    dependsOn(fetchDicts)
}
