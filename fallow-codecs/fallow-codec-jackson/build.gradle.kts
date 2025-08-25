plugins {
    `fallow-java`
    `fallow-publish`
    `fallow-repositories`
}

dependencies {
    api(project(":fallow-codecs:fallow-codec-common"))
    api(libs.jackson.databind)
}

fallowPublish {
    artifactId = "fallow-codec-jackson"
}