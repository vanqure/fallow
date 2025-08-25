plugins {
    `fallow-java`
    `fallow-publish`
    `fallow-repositories`
}

dependencies {
    api(project(":fallow-codecs:fallow-codec-jackson"))
    api(libs.msgpack.jackson.dataformat)
}

fallowPublish {
    artifactId = "fallow-codec-jackson"
}