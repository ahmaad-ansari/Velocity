# Car-Marketplace-Application
## Overview

In response to the need for a modern, user-friendly platform for buying and selling vehicles, we present this proposal for the development of a Car Marketplace Application. This app will provide a feature-rich and interactive experience for both buyers and sellers of automobiles.

This Android application serves as a comprehensive car marketplace allowing users to buy and sell cars. It offers functionalities for user authentication, profile management, car listings, map integration, and more.

<div align="center">
  <img src="https://github.com/ahmaad-ansari/Car-Marketplace-Application/assets/88805493/3b916a1c-6ae2-4a9c-a60b-52f8b44010a8" alt="Screenshot" width="200"/>
  <img src="https://github.com/ahmaad-ansari/Car-Marketplace-Application/assets/88805493/66ca41b5-1beb-496a-becd-44248ec33f84" alt="Screenshot" width="200"/>
  <img src="https://github.com/ahmaad-ansari/Car-Marketplace-Application/assets/88805493/5786e600-d5b8-42d9-ad18-b1e33c7b9dd2" alt="Screenshot" width="200"/>
  <img src="https://github.com/ahmaad-ansari/Car-Marketplace-Application/assets/88805493/53efb2d1-e139-455d-9659-3094d08f598b" alt="Screenshot" width="200"/>
  <img src="https://github.com/ahmaad-ansari/Car-Marketplace-Application/assets/88805493/62a7a312-f213-457f-bba0-5736c6f1d494" alt="Screenshot" width="200"/>
  <img src="https://github.com/ahmaad-ansari/Car-Marketplace-Application/assets/88805493/ff295e6f-44ac-428a-a617-678c168749b7" alt="Screenshot" width="200"/>
  <img src="https://github.com/ahmaad-ansari/Car-Marketplace-Application/assets/88805493/68f60ab0-7934-46db-b088-fed879792389" alt="Screenshot" width="200"/>
  <img src="https://github.com/ahmaad-ansari/Car-Marketplace-Application/assets/88805493/09326597-a7fa-4647-91df-ee7c52a6a4cc" alt="Screenshot" width="200"/>
  <img src="https://github.com/ahmaad-ansari/Car-Marketplace-Application/assets/88805493/7073b823-df2e-47af-9539-6591615df64b" alt="Screenshot" width="200"/>
  <img src="https://github.com/ahmaad-ansari/Car-Marketplace-Application/assets/88805493/ed9ad276-4f6f-44aa-8c9f-0ddcd4ca2dbc" alt="Screenshot" width="200"/>
  <img src="https://github.com/ahmaad-ansari/Car-Marketplace-Application/assets/88805493/827a6a78-09ee-40b1-8867-f3f67e4ac493" alt="Screenshot" width="200"/>
</div>

## Features

### Authentication
- **Signup**: Users can register using their email, password, first name, last name, phone number, and address.
- **Login**: Existing users can sign in to access the application's features.

### Profile Management
- **Profile Viewing**: Users can view their profile information (first name, last name, email, phone number).
- **Profile Editing**: Users can edit their profile details and save changes.
- **Profile Image**: Includes functionality to set a profile image using Glide (commented out in code).

### Marketplace Listings
- **Car Listings**: Displays car listings retrieved from Firebase Firestore.
- **Listing Details**: Each listing includes car make, model, year, odometer reading, and price.
- **Listing Interaction**: Handles item click events for individual car listings.

### Map Integration
- **Google Maps**: Integrates Google Maps to display user location or specified addresses.
- **Location Permissions**: Requests and handles user location permissions.

### Other Components
- **ViewModel Architecture**: Utilizes ViewModel for shared data between fragments or activities.
- **Shared Data**: Manages shared data like car listings, user profiles, and image sources using LiveData.
- **Splash Screen**: Redirects users to the main activity or login/signup screen based on authentication status.

## Setup Instructions
1. **Clone Repository**: Clone this repository to your local machine.
2. **Setup Firebase**: Set up Firebase for authentication and Firestore. Add `google-services.json` to the app module.
3. **Android Studio**: Open the project in Android Studio and sync Gradle files.
4. **Run the App**: Run the application on an emulator or physical device.

## Usage
- **Signup/Login**: Register or sign in to access the application.
- **Profile**: View/edit your profile information.
- **Car Listings**: Browse available car listings.
- **Map**: Explore the map feature (if applicable).

## Technologies/Frameworks Used
- Android Studio
- Firebase (Authentication, Firestore, Cloud Storage)
- Google Maps API
