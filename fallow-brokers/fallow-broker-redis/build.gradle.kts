plugins {
    id("io.github.fallow.java")
    id("io.github.fallow.publish")
}

dependencies {
    api(project(":fallow-codecs:fallow-codec-common"))
    api(project(":fallow-brokers:fallow-broker-common"))
    api(libs.lettuce.core)
}

fallowPublish {
    artifactId = "fallow-broker-redis"
}