# Book Explorer 📚

Book Explorer is a modern Android application built using the latest industry standards. It allows users to browse, search, and manage their favorite books using the Open Library API.

## ✨ Features

- **Book Browsing**: Discover popular titles in the home section.
- **Search**: Fast and intuitive book search across the massive Open Library database.
- **Book Details**: Detailed information including authors, descriptions, and cover art.
- **Favorites**: Save your favorite books to local storage for quick access.
- **Dark Mode**: Full support for a dark theme to protect your eyes.
- **Responsive Design**: A modern user interface built with Material Design 3.

## 🛠️ Tech Stack

The application is built using modern libraries and design patterns:

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Modern Declarative UI)
- **Styling**: Material Design 3
- **Networking**: Retrofit & OkHttp
- **State Management**: Modern ViewModel & StateFlow
- **Data Storage**: Jetpack DataStore (Local favorites)
- **Navigation**: Jetpack Compose Navigation
- **Images**: Coil (online image loading)

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug or newer
- JDK 17
- Android device (API 24+) or emulator

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/Loleku/bookexplorer.git
   ```
2. Open the project in **Android Studio**.
3. Wait for the Gradle sync to complete.
4. Select your target device and click **Run**.

## 📁 Project Structure

The application follows **Clean Architecture** principles and layer separation:

- `data/`: Data layer containing API (Retrofit), data models, repositories, and DataStore handling.
- `ui/`: Presentation layer containing screens, Compose components, themes, and ViewModels.
- `MainActivity.kt`: Main entry point and navigation handling.

## 📄 License

This project was created for educational purposes. Code is available under the MIT License.
