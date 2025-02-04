# PresensiApp

## English Version

PresensiApp is an attendance management application that allows users to check in and check out using real-time authentication. This app ensures accurate attendance tracking with an intuitive user interface.

### Main Features

- **User Authentication**: Secure login system using Firebase Authentication.
- **Check-in & Check-out**: Users can mark their attendance with timestamps.
- **Real-time Database**: Stores attendance data securely in Firebase.
- **User-friendly Interface**: Simple and intuitive design for ease of use.

### How to Run the Project

1. **Clone Repository**:
   ```sh
   git clone https://github.com/KILABID/Presensi-App.git
   ```
2. **Open in Android Studio**
3. **Sync Gradle**
4. **Run the Application** on an emulator or physical device.

### Firebase Authentication Setup

To enable Firebase Authentication in your project, follow these steps:

1. **Set Up Firebase Project**:
   - Go to [Firebase Console](https://console.firebase.google.com/)
   - Create a new project and register your app (Android package name must match your project)
   - Download the `google-services.json` file and place it in the `app/` directory

### Technologies Used

- **Kotlin** for Android development
- **Jetpack Compose** for UI
- **Firebase Authentication** for user authentication
- **Firebase Firestore** for attendance data storage

### Main Dependencies

Check `build.gradle.kts` for required dependencies, including:

```kotlin
implementation("com.google.firebase:firebase-auth-ktx:<latest_version>")
implementation("com.google.firebase:firebase-firestore-ktx:<latest_version>")
```

### Contribution

If you want to contribute, please submit a pull request or report issues in the Issues section.


---

## Versi Indonesia

PresensiApp adalah aplikasi manajemen absensi yang memungkinkan pengguna untuk check-in dan check-out menggunakan autentikasi real-time. Aplikasi ini memastikan pencatatan kehadiran yang akurat dengan antarmuka yang intuitif.

### Fitur Utama

- **Autentikasi Pengguna**: Sistem login aman menggunakan Firebase Authentication.
- **Check-in & Check-out**: Pengguna dapat menandai kehadiran dengan timestamp.
- **Database Real-time**: Menyimpan data absensi dengan aman di Firebase.
- **Antarmuka Ramah Pengguna**: Desain sederhana dan intuitif untuk kemudahan penggunaan.

### Cara Menjalankan Proyek

1. **Clone Repository**:
   ```sh
   git clone https://github.com/KILABID/Presensi-App.git
   ```
2. **Buka di Android Studio**
3. **Sinkronisasi Gradle**
4. **Jalankan Aplikasi** di emulator atau perangkat fisik.

### Setup Firebase Authentication

Untuk mengaktifkan Firebase Authentication di proyek Anda, ikuti langkah berikut:

1. **Siapkan Proyek Firebase**:
   - Buka [Firebase Console](https://console.firebase.google.com/)
   - Buat proyek baru dan daftarkan aplikasi (sesuaikan package name)
   - Unduh file `google-services.json` dan letakkan di folder `app/`

### Teknologi yang Digunakan

- **Kotlin** untuk pengembangan aplikasi Android
- **Jetpack Compose** untuk UI
- **Firebase Authentication** untuk autentikasi pengguna
- **Firebase Firestore** untuk penyimpanan data absensi

### Dependencies Utama

Pastikan untuk memeriksa file `build.gradle.kts` untuk dependensi yang digunakan, termasuk:

```kotlin
implementation("com.google.firebase:firebase-auth-ktx:<latest_version>")
implementation("com.google.firebase:firebase-firestore-ktx:<latest_version>")
```

### Kontribusi

Jika ingin berkontribusi, silakan buat pull request atau laporkan masalah melalui Issues.


---

Selamat menggunakan PresensiApp! 🎉
