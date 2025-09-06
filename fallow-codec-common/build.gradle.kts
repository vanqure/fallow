plugins {
    id("io.github.fallow.java")
    id("io.github.fallow.publish")
}

dependencies {
    api(libs.wisp)
}

fallowPublish {
    artifactId = "fallow-codec"
}