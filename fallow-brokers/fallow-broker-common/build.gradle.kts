plugins {
    id("io.github.fallow.java")
    id("io.github.fallow.publish")
}

dependencies {
    api(project(":fallow-codecs:fallow-codec-common"))
    api(libs.wisp)
}

fallowPublish {
    artifactId = "fallow-broker"
}