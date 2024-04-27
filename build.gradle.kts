
ext {
    // Get Nexus credentials from system environment
    System.getenv("NEXUS_USERNAME")?.let { set("nexusUsername", it) }
    System.getenv("NEXUS_PASSWORD")?.let { set("nexusPassword", it) }
}


project.description = "Object Description Reader (Experimental)"

plugins {
    java
}

repositories {
    mavenCentral()
    maven {
        name = "nexus"
        url = uri("https://nexus.gsissc.myatos.net/repository/GH_DE_MUC_CYSP_DIRX_MAVEN2/")
        credentials {
            username = rootProject.ext.get("nexusUsername") as String?
            password = rootProject.ext.get("nexusPassword") as String?
        }
    }
    maven {
        name = "artifactory"
        url = uri("https://jaspersoft.jfrog.io/artifactory/third-party-ce-artifacts/")
    } // Required by JasperReports, see https://github.com/TIBCOSoftware/jasperreports/issues/234
    maven {
        name = "shibboleth"
        url = uri("https://build.shibboleth.net/nexus/content/repositories/releases/")
    } // Required by org.opensaml:opensaml-saml-impl:4.2.0, required by Apache CXF

    maven {
        name = "cantara"
        url = uri("https://mvnrepo.cantara.no/content/repositories/releases/")
    }
    maven {
        name = "openMind"
        url = uri("https://repository.openmindonline.it/")
    }
    flatDir {
        dirs(
                "$rootDir/libs"
        )
    }
}

dependencies {

    implementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    implementation("junit:junit:4.13.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.8.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.1")

    implementation("com.fasterxml.jackson.core:jackson-annotations:2.16.0-rc1")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.16.0-rc1")
    implementation("com.fasterxml.woodstox:woodstox-core:6.5.0")
    implementation("org.slf4j:slf4j-api:2.0.9")

//    implementation(name("dirx-dxi-commontestutils-9.0-SNAPSHOT")!!)
//    implementation("dirx-dxi-ldap-util-9.0-SNAPSHOT.jar")
//    implementation("dirx-dxc-jdiscover-api-9.0-SNAPSHOT.jar")
//    implementation("dirx-dxi-ldap-util-9.0-SNAPSHOT")
//    implementation("dirx-dxc-logging-9.0-SNAPSHOT")
    implementation(files("$projectDir/libs/dirx-dxc-ldap-9.0-SNAPSHOT.jar"))
    implementation(files("$projectDir/libs/dirx-dxi-commontestutils-9.0-SNAPSHOT.jar"))
    implementation(files("$projectDir/libs/dirx-dxi-ldap-util-9.0-SNAPSHOT.jar"))
    implementation(files("$projectDir/libs/dirx-dxc-jdiscover-api-9.0-SNAPSHOT.jar"))
    implementation(files("$projectDir/libs/dirx-dxi-ldap-util-9.0-SNAPSHOT.jar"))
    implementation(files("$projectDir/libs/dirx-dxc-logging-9.0-SNAPSHOT.jar"))

    //api(fileTree("libs") { include("*.jar") })
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes["Implementation-Title"] = project.description
    }
}
