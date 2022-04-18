/*
 * Copyright 2014 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("rootProject.ext.gradleClasspath")
    }
}

apply(plugin = 'com.android.application')

project.archivesBaseName = "muzei"

repositories {
    mavenCentral()
}

android {
    compileSdkVersion(rootProject.ext.compileSdkVersion)
    buildToolsVersion(rootProject.ext.buildToolsVersion)

    defaultConfig {
        minSdkVersion(17)
        targetSdkVersion(rootProject.ext.targetSdkVersion)
        renderscriptTargetApi(rootProject.ext.targetSdkVersion)
        renderscriptSupportModeEnabled(true)

        versionName = "1.0.0"
        versionCode = 1
    }

    signingConfigs {
        release {
            storeFile(keyProps["store"] != null ? file(keyProps["store"]) : null)
            keyAlias(keyProps["alias"] ?: "")
            storePassword(keyProps["storePass"] ?: "")
            keyPassword(keyProps["pass"] ?: "")
        }
    }

    productFlavors {
        dev {
            // dev utilizes minSDKVersion = 21 to allow the Android gradle plugin
            // to pre-dex each module and produce an APK that can be tested on
            // Android Lollipop without time consuming dex merging processes.
            minSdkVersion(21)
            multiDexEnabled(true)
        }

        prod {
        }
    }

    buildTypes {
        debug {
            versionNameSuffix(" Debug")
        }

        release {
            minifyEnabled(true)
            shrinkResources(true)
            proguardFiles(getDefaultProguardFile('proguard-android.txt'), file('proguard-project.txt'))
            signingConfig(signingConfigs.release)
        }

        publicBeta.initWith(buildTypes.release)
        publicBeta {
            minifyEnabled(true)
            shrinkResources(true)
            proguardFiles(getDefaultProguardFile('proguard-android.txt'), file('proguard-project.txt'))
        }

        publicDebug.initWith(buildTypes.publicBeta)
        publicDebug {
            debuggable(true)
            renderscriptDebuggable(true)
            minifyEnabled(true)
            shrinkResources(true)
            proguardFiles(getDefaultProguardFile('proguard-android.txt'), file('proguard-project.txt'))
        }
    }

    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_1_7)
        targetCompatibility(JavaVersion.VERSION_1_7)
    }
}

dependencies {
    compile('com.squareup.okhttp:okhttp:2.1.0')
    compile('com.squareup.okhttp:okhttp-urlconnection:2.1.0')
    compile('com.squareup.picasso:picasso:2.4.0')
    compile('com.google.android.gms:play-services-wearable:8.3.0')
    compile('de.greenrobot:eventbus:2.4.0')
    compile('com.android.support:appcompat-v7:23.1.1')
    compile('com.android.support:recyclerview-v7:23.1.1')
    compile('com.android.support:design:23.1.1')
    compile('com.android.support:customtabs:23.1.1')
    compile(files('android_libs/AdTechMobileSdk/ADTECHMobileSDK.jar'))

    // :api is included as a transitive dependency from :android-client-common
    // compile project(':api')
    compile(project(':android-client-common'))
    devWearApp(project(path: ':wearable', configuration: 'devRelease'))
    prodWearApp(project(path: ':wearable', configuration: 'prodRelease'))
}