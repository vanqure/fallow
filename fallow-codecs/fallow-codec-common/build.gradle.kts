plugins {
    `fallow-java`
    `fallow-publish`
    `fallow-repositories`
}

dependencies {
    api(libs.wisp)
}

fallowPublish {
    artifactId = "fallow-codec"
}