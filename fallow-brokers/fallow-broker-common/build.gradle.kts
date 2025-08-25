plugins {
    `fallow-java`
    `fallow-publish`
    `fallow-repositories`
}

dependencies {
    api(project(":fallow-codecs:fallow-codec-common"))
    api(libs.wisp)
}

fallowPublish {
    artifactId = "fallow-broker"
}