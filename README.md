# ExoVideo - Reference App

## Table of Contents

* [Introduction](#introduction)
* [About Huawei Video Kit](#about-huawei-video-kit)
* [About Huawei Account Kit](#about-huawei-account-kit)
* [About Huawei Auth Service](#about-huawei-auth-service)
* [About Google Firebase Authentication](#about-firebase-authentication)
* [About Google Firebase Database](#about-firebase-database)
* [About Google Firebase Storage](#about-firebase-storage)
* [About Exo Player 2.0](#about-exo-player)
* [Providers](#providers)
* [What You Will Need](#what-you-will-need)
* [Getting Started](#getting-started)
* [Using the Application](#using-the-application)
* [Goals](#goals)
* [Features](#features-of-this-application)
* [Screenshots](#screenshots)
* [Project Structure](#project-structure)
* [Libraries](#open-source-libraries-used-in-this-project-are)
* [Contributors](#contributors)
* [License](#license)

Seemless video application platform that you can record share anywhere you want, anytime you want!

<img src="app/src/assets/image/Screenshot 2020-12-25 173830.jpg" width=250></img>
<img src="app/src/assets/image/Screenshot 2020-12-25 173542.jpg" width=250></img>
<img src="app/src/assets/image/Screenshot 2020-12-25 173743.jpg" width=250></img>

## Introduction

## About Huawei Video Kit

Today's users interact with videos to an unprecedented degree. Smoother HD video playback, bolstered by wide-ranging control options, raises the ceiling for your app and makes it more appealing.

## About Huawei Account Kit

The faster your user signs in to your app, the faster they adopt it. There's no quicker and safer way to do this than with their HUAWEI IDs based on two-factor authentication. Access the Huawei ecosystem of users with seamless sign-ins from different devices and grow your user base.

## About Huawei Auth Service

Integrate a client SDK and access our cloud service to build a secure and reliable user authentication system aggregating multiple authentication modes for your app.

## About Google Firebase Authentication

Firebase Authentication aims to make building secure authentication systems easy, while improving the sign-in and onboarding experience for end users. It provides an end-to-end identity solution, supporting email and password accounts, phone auth, and Google, Twitter, Facebook, and GitHub login, and more.

## About Google Firebase Database

The Firebase Realtime Database is a cloud-hosted NoSQL database that lets you store and sync data between your users in realtime.

## About Google Firebase Storage

Cloud Storage for Firebase is a powerful, simple, and cost-effective object storage service built for Google scale. The Firebase SDKs for Cloud Storage add Google security to file uploads and downloads for your Firebase apps, regardless of network quality. You can use our SDKs to store images, audio, video, or other user-generated content. On the server, you can use Google Cloud Storage, to access the same files.

## About Exo Player

ExoPlayer is an application level media player for Android. It provides an alternative to Android’s MediaPlayer API for playing audio and video both locally and over the Internet. ExoPlayer supports features not currently supported by Android’s MediaPlayer API, including DASH and SmoothStreaming adaptive playbacks. Unlike the MediaPlayer API, ExoPlayer is easy to customize and extend, and can be updated through Play Store application updates.

## Providers

This ExoVideo Android Application has been built with flavors.

Database-wise operations are being handled with [Firebase Realtime Database](https://firebase.google.com/products/realtime-database?gclid=Cj0KCQiAuJb_BRDJARIsAKkycUktHz6wCHAwb6XiISXe1UC_BTjBc2Z4mJx0QvEwgep0a5RKAsOqis0aAqeKEALw_wcB) and [Firebase Cloud Storage](https://firebase.google.com/docs/storage/?gclid=Cj0KCQiAuJb_BRDJARIsAKkycUmuCDoLOJ6SukecJihG6xgxILkcOIMQemORlZixVcg1NLdfGmDqk9saAricEALw_wcB).

### DRM

Digital Rights Management.

For more information, see the [page](https://en.wikipedia.org/wiki/Digital_rights_management)

### HMS
For watching videos it uses [Video Kit](https://developer.huawei.com/consumer/en/hms/huawei-videokit/)

For login processes it uses [Huawei Auth Service](https://developer.huawei.com/consumer/en/agconnect/auth-service/), [Huawei Account Kit](https://developer.huawei.com/consumer/en/hms/huawei-accountkit/).

### GMS
For watching videos it uses [Exo Player 2.0](https://exoplayer.dev/) for watching videos.

Also for login procesess app uses [Firebase Authentication](https://firebase.google.com/products/auth?gclid=Cj0KCQiAuJb_BRDJARIsAKkycUkfyihOtGZryyyX5Kx-r4SyFqan5GYzH8tPsckvNnH6VNmM9XD-QNkaAqiBEALw_wcB)

## What You Will Need

**Hardware Requirements**
- A computer that can run Android Studio.
- An Android phone for debugging.

**Software Requirements**
- Android SDK package
- Android Studio 3.X
- API level of at least 23
- HMS Core (APK) 4.X or later (Not needed for Exo Player 2.0)

## Getting Started

### HMS Build

This Video and Auth Service service app uses HUAWEI services. In order to use them, you have to [create an app](https://developer.huawei.com/consumer/en/doc/distribution/app/agc-create_app) first. Before getting started, please [sign-up](https://id1.cloud.huawei.com/CAS/portal/userRegister/regbyemail.html?service=https%3A%2F%2Foauth-login1.cloud.huawei.com%2Foauth2%2Fv2%2Flogin%3Faccess_type%3Doffline%26client_id%3D6099200%26display%3Dpage%26flowID%3D6d751ab7-28c0-403c-a7a8-6fc07681a45d%26h%3D1603370512.3540%26lang%3Den-us%26redirect_uri%3Dhttps%253A%252F%252Fdeveloper.huawei.com%252Fconsumer%252Fen%252Flogin%252Fhtml%252FhandleLogin.html%26response_type%3Dcode%26scope%3Dopenid%2Bhttps%253A%252F%252Fwww.huawei.com%252Fauth%252Faccount%252Fcountry%2Bhttps%253A%252F%252Fwww.huawei.com%252Fauth%252Faccount%252Fbase.profile%26v%3D9f7b3af3ae56ae58c5cb23a5c1ff5af7d91720cea9a897be58cff23593e8c1ed&loginUrl=https%3A%2F%2Fid1.cloud.huawei.com%3A443%2FCAS%2Fportal%2FloginAuth.html&clientID=6099200&lang=en-us&display=page&loginChannel=89000060&reqClientType=89) for a HUAWEI developer account.

After creating the application, you need to [generate a signing certificate fingerprint](https://developer.huawei.com/consumer/en/codelab/HMSPreparation/index.html#3). Then you have to set this fingerprint to the application you created in AppGallery Connect.
- Go to "My Projects" in AppGallery Connect.
- Find your project from the project list and click the app on the project card.
- On the Project Setting page, set SHA-256 certificate fingerprint to the SHA-256 fingerprint you've generated.
![AGC-Fingerprint](https://communityfile-drcn.op.hicloud.com/FileServer/getFile/cmtyPub/011/111/111/0000000000011111111.20200511174103.08977471998788006824067329965155:50510612082412:2800:6930AD86F3F5AF6B2740EF666A56165E65A37E64FA305A30C5EFB998DA38D409.png?needInitFileName=true?needInitFileName=true?needInitFileName=true?needInitFileName=true)

Notice that under app level [build.gradle](https://github.com/Onurcan-Keskin/MapKitReference/blob/master/app/build.gradle) signingConfigs sections are marked as "xxx". Generate sign-in configs for this project to use the app properly.

### GMS Build

## Using the Application

### HMS Build

On initial launch login page welcomes you either by entering your email or using your Huawei ID you may proceed to the Main page.

By sliding up or down you may traverse between videos, give lovely to them, share them or drop a comment to them. If you're curious you may observe about the additional information about videos.

By pressing Home you will refresh the videos or from the Record you can either enter Url's you want to watch or record your own videos to share them appwise globally.

From your Profile page you may change the language to English or Turkish. 

### GMS Build

On initial launch login page welcomes you either by entering your email or using your Google Account you may proceed to the Main page.

By sliding up or down you may traverse between videos, give lovely to them, share them or drop a comment to them. If you're curious you may observe about the additional information about videos.

By pressing Home you will refresh the videos or from the Record you can either enter Url's you want to watch or record your own videos to share them appwise globally.

From your Profile page you may change the language to English or Turkish. 

## Goals

The goal of this application is to show the difference and implementation styles of both both [Video Kit](https://developer.huawei.com/consumer/en/hms/huawei-videokit/) and [Exo Player 2.0](https://exoplayer.dev/)

## Features

- Turkish-English Language support
- Record videos.
- Give lovely to videos, share the m on your favorite platforms or drop a comment to them.
- On your own profile page either open them to share or hide them, or watch on fullscreen.

## Screenshots

<img src="app/src/assets/image/Screenshot 2020-12-25 173830.jpg" width=250></img>
<img src="app/src/assets/image/Screenshot 2020-12-25 173542.jpg" width=250></img>
<img src="app/src/assets/image/Screenshot 2020-12-25 173743.jpg" width=250></img>
<img src="app/src/assets/image/Screenshot 2020-12-25 173524.jpg" width=250></img>
<img src="app/src/assets/image/Screenshot 2020-12-25 173558.jpg" width=250></img>
<img src="app/src/assets/image/Screenshot 2020-12-25 173451.jpg" width=250></img>
<img src="app/src/assets/image/Screenshot 2020-12-25 173631.jpg" width=250></img>
<img src="app/src/assets/image/Screenshot 2020-12-25 173804.jpg" width=250></img>

## Project Structure

This app is designed with MVP design pattern.

## Libraries

### Global

* [Firebase Realtime Database](https://firebase.google.com/products/realtime-database?gclid=Cj0KCQiAuJb_BRDJARIsAKkycUktHz6wCHAwb6XiISXe1UC_BTjBc2Z4mJx0QvEwgep0a5RKAsOqis0aAqeKEALw_wcB)
* [Firebase Cloud Storage](https://firebase.google.com/docs/storage/?gclid=Cj0KCQiAuJb_BRDJARIsAKkycUmuCDoLOJ6SukecJihG6xgxILkcOIMQemORlZixVcg1NLdfGmDqk9saAricEALw_wcB)
* [Picasso](https://square.github.io/picasso/)
* [Okhttp](https://square.github.io/okhttp/)
* [Okio](https://square.github.io/okio/)
* [Lottie](https://airbnb.io/lottie/#/)
* [AndroidSlidingUpPanel](https://github.com/umano/AndroidSlidingUpPanel)
* [Slidr](https://github.com/r0adkll/Slidr)
* [CircleImageView](https://github.com/hdodenhof/CircleImageView)
* [BottomBar](https://github.com/roughike/BottomBar)
* [AnimatedBottomBar](https://github.com/Droppers/AnimatedBottomBar)
* [Dagger](https://dagger.dev/)
* [CameraX](https://developer.android.com/training/camerax)
* [Simple Video View](https://github.com/klinker24/Android-SimpleVideoView)
* [Youtube Extractor](https://github.com/HaarigerHarald/android-youtubeExtractor)
* [Mockito](https://site.mockito.org/)

### HMS

* [Video Kit](https://developer.huawei.com/consumer/en/hms/huawei-videokit/)
* [Huawei Auth Service](https://developer.huawei.com/consumer/en/agconnect/auth-service/)
* [Huawei Account Kit](https://developer.huawei.com/consumer/en/hms/huawei-accountkit/)

### GMS

* [Exo Player 2.0](https://exoplayer.dev/)
* [Firebase Authentication](https://firebase.google.com/products/auth?gclid=Cj0KCQiAuJb_BRDJARIsAKkycUkfyihOtGZryyyX5Kx-r4SyFqan5GYzH8tPsckvNnH6VNmM9XD-QNkaAqiBEALw_wcB)

## Contributors

* Onurcan Keskin

## License

### HMS

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
   
### GMS

Copyright 2017 Google Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
