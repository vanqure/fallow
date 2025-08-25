plugins {
    `fallow-java`
    `fallow-publish`
    `fallow-repositories`
}

dependencies {
    api(project(":fallow-codecs:fallow-codec-common"))
    api(project(":fallow-brokers:fallow-broker-common"))
    api(libs.lettuce.core)
}

fallowPublish {
    artifactId = "fallow-broker-redis"
}