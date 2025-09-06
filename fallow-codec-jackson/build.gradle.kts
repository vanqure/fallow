plugins {
    id("io.github.fallow.java")
    id("io.github.fallow.publish")
}

dependencies {
    api(project(":fallow-codec-common"))
    api(libs.jackson.databind)
}

fallowPublish {
    artifactId = "fallow-codec-jackson"
}