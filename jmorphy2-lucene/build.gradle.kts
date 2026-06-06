description = "Stemmer and tagger based on jmorphy2 for Lucene"

version = getLibraryVersion()

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation("org.apache.lucene:lucene-core:${project.getLuceneVersion()}")
    implementation("org.apache.lucene:lucene-analyzers-common:${project.getLuceneVersion()}")
    testImplementation("org.apache.lucene:lucene-test-framework:${project.getLuceneVersion()}")

    api(project(":jmorphy2-core"))
    api(project(":jmorphy2-nlp"))
    implementation(project(":jmorphy2-dicts-ru"))
    implementation(project(":jmorphy2-dicts-uk"))
    testImplementation(project(path = ":jmorphy2-core", configuration = "tests"))

    // Commented out because of old syntax incompatibility in Gradle 7.3.3 Kotlin DSL
    // testImplementation(project(":jmorphy2-core").sourceSets["test"].output)
}