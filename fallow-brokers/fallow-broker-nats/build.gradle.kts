plugins {
    `fallow-java`
    `fallow-publish`
    `fallow-repositories`
}

dependencies {
    api(project(":fallow-codecs:fallow-codec-common"))
    api(project(":fallow-brokers:fallow-broker-common"))
    api(libs.jnats)
}

fallowPublish {
    artifactId = "fallow-broker-nats"
}