# Cloud-Computing-Insta-Clone
This Github respository contains the source code for a Instagram like app that uses the Google service Firebase to store images, authenitcate users, and save user images.

## Table of contents
* [App Structure](#app-structure)
* [Firebase](#firebase)
* [Authentication](#authentication)
* [Cloud Storage](#cloud-storage)
* [Firestore](#firestore)
* [Google Service Paper](#google-service-paper)
* [Demo Video](#demo-video)

## App Structure
The app is an Android app that uses Google Firebase as the backend in order to manage user accounts and photos. Firebase is linked to the app through Android Studio and allows for easy downloads of dependencies and the necessary libraries to support Firebase's services.  This is due to the fact that Android Studio already has a built-in way into their IDE in order to support this.

## Firebase
Firebase is a service provided by Google for creating mobile and web applications. In this service they provide multiple other service’s such as cloud storage, authentication, and one of their databases Firestore. Using Firebase allows for easy scalability so that a developer can focus more on the development side of their application.

## Authentication
Authentication is a service provided through Firebase. It is used in order to create user accounts, sign in users, log out users, and manage user accounts. Authentication is used by enabling it in Android Studio after linking a Google Firebase account to the app and downloading all required dependencies. This project uses Authentication to create user accounts by email and password, log in users, and log out users.

## Cloud Storage
Cloud Storage is a service provided through Firebase. It is used in order to save files to the cloud and is retrievable through URL. Cloud Storage is used by enabling it in Android Studio after linking a Google Firebase account to the app and downloading all required dependencies. This project uses Cloud Storage to store photos uploaded by users to the cloud.

## Firestore
Firestore is a service provided through Firebase. It is used in order to save data through collections in a form very similar to JSON. Firestore is used by enabling it in Android Studio after linking a Google Firebase account to the app and downloading all required dependencies. This project uses Firestore to save user IDs, usernames, and the URLs to the photos saved to Firebase Cloud Storage.

##Google Service Paper
https://github.com/Chris18750/Cloud-Computing-Insta-Clone/blob/main/FirebaseService.pdf

##Demo Video
https://youtu.be/bOXZ67tGiNk
