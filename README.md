Personal Tutoring Service
An Android application that connects students with personal tutors, built with Android Studio and powered by Firebase.
---
Table of Contents
Overview
Features
Tech Stack
Prerequisites
Getting Started
Project Structure
Firebase Setup
Build & Run
---
Overview
Personal Tutoring Service is a mobile application designed to streamline the experience of finding, booking, and managing personal tutoring sessions. 
Whether you're a student looking for academic support or a tutor offering your expertise, this app provides a seamless platform to connect.
---
Features
Browse and search for tutors by subject or availability
Book and manage tutoring sessions
Real-time messaging between students and tutors
User authentication and profile management
Session history and progress tracking
---
Tech Stack
Layer	                Technology
Platform	            Android (Java/Kotlin)
Build System	        Gradle 8.3
Backend / Auth	        Firebase
Google Services	        `com.google.gms:google-services:4.4.1`
Min Android Support	    AndroidX
---
Prerequisites
Before getting started, make sure you have the following installed:
Android Studio (Hedgehog or later recommended)
JDK 11 or higher
Android SDK (API level 21+ recommended)
A Firebase project (see Firebase Setup)
---
Getting Started
Clone the repository
```bash
   git clone https://github.com/bevanphilip/personaltutoringservice.git
   cd PersonalTutoringService
   git checkout origin/master
   ```
Open in Android Studio
Open Android Studio and select File → Open, then navigate to the cloned directory.
You can use existing Firebase project https://console.firebase.google.com/project/personal-tutoring-servic-cfc35/overview or 
Set up new Firebase (see Firebase Setup)
Sync Gradle
Android Studio will prompt you to sync Gradle files. Click Sync Now to download all dependencies.
Build and run the project on an emulator or physical device.

---
Project Structure
```
PersonalTutoringService/
├── app/                    # Main application module
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/       # Application source code
│   │   │   ├── res/        # Layouts, drawables, strings, etc.
│   │   │   └── AndroidManifest.xml
│   │   └── test/           # Unit tests
│   └── build.gradle        # Module-level build config
├── build.gradle            # Project-level build config
├── settings.gradle         # Project settings
├── gradle.properties       # Gradle JVM and AndroidX settings
└── gradlew / gradlew.bat   # Gradle wrapper scripts
```
---
Firebase Setup
This project uses Firebase for backend services. To connect your own Firebase project:
Go to the Firebase Console and create a new project.
Register an Android app using your package name (found in `app/build.gradle`).
Download the `google-services.json` file provided by Firebase.
Remove the existing google-services.json from `/app` directory and place downloaded `google-services.json` inside the `/app` directory.
Enable any Firebase services you need (e.g., Authentication, Firestore, Realtime Database).
> ⚠️ **Important:** Never commit `google-services.json` or `local.properties` to version control.
---
Build & Run
Clean the project:
```bash
./gradlew clean
```
Build the project:
```bash
./gradlew build
```
Run project:
Select emulator on top of Android Studio and hit play button.
```
Built with using Android Studio and Firebase.