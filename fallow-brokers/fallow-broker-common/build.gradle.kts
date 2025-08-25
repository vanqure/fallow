plugins {
    `fallow-java`
    `fallow-publish`
    `fallow-repositories`
}

dependencies {
    api(project(":fallow-codecs:fallow-codec-common"))
    api(libs.wisp)
    testImplementation(project(":fallow-codecs:fallow-codec-jackson"))
    testImplementation(project(":fallow-brokers:fallow-broker-redis"))
}

fallowPublish {
    artifactId = "fallow-broker"
}