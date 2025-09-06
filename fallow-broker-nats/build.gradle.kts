plugins {
    id("io.github.fallow.java")
    id("io.github.fallow.publish")
}

dependencies {
    api(project(":fallow-codec-common"))
    api(project(":fallow-broker-common"))
    api(libs.jnats)
}

fallowPublish {
    artifactId = "fallow-broker-nats"
}