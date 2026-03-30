// Top-level build file where you can add configuration options common to all sub-projects/modules.
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.6.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    id("org.jetbrains.kotlin.kapt") version "1.9.0" apply false
}
//A partir de aquí
buildscript {
    repositories {
        // Otras repositorios si es necesario
        google()
        mavenCentral()
        maven(url = "https://maven.pkg.jetbrains.space/public/p/kotlinx-serialization/maven")
    }
    dependencies {
        classpath("com.google.gms:google-services:4.4.2")
    }

       dependencies {

           // Agrega la dependencia del complemento de Google Services
           classpath("com.google.gms:google-services") // O la versión más reciente disponible
       }


}
