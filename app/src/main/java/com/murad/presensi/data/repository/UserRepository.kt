package com.murad.presensi.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.murad.presensi.data.local.model.AttendanceModel
import com.murad.presensi.data.local.model.HistoryItem
import com.murad.presensi.data.local.model.Time
import com.murad.presensi.data.local.model.UserAttendance
import com.murad.presensi.data.local.model.UserModel
import com.murad.presensi.data.local.preferences.PrefModel
import kotlinx.coroutines.tasks.await
import kotlin.concurrent.Volatile


class UserRepository(private val preferencesRepository: PreferencesRepository) {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // Function to Register
    suspend fun createUser(
        email: String,
        password: String,
        name: String,
        role: String,
    ): String {
        try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("UID tidak ditemukan")
            Log.d("UserRepository", uid)

            // Create a new document in Firestore
            val user = UserModel(name = name, role = role, id = uid, email = email)
            db.collection("users").document(uid).set(user).await()

            return "Registrasi berhasil"
        } catch (e: Exception) {
            val errorMessage = when (e) {
                is com.google.firebase.auth.FirebaseAuthUserCollisionException -> "Email sudah digunakan"
                is com.google.firebase.auth.FirebaseAuthWeakPasswordException -> "Password terlalu lemah"
                is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> "Format email tidak valid"
                is com.google.firebase.firestore.FirebaseFirestoreException -> "Terjadi kesalahan saat menyimpan pengguna ke Firestore"
                else -> "Kesalahan tak terduga: ${e.message}"
            }
            return errorMessage
        }
    }

    // Function to Login
    suspend fun login(email: String, password: String): Result<UserModel> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("UID tidak ditemukan")

            val snapshot = db.collection("users").document(uid).get().await()
            if (snapshot.exists()) {
                Log.d("FirestoreSnapshot", "Snapshot data: ${snapshot.data}")
                val user = snapshot.toObject<UserModel>()
                    ?: throw Exception("Data pengguna tidak ditemukan")
                Log.d("UserRepository", "Data pengguna: ${user.role}")
                val prefModel = PrefModel(
                    id = uid,
                    username = user.name,
                    isLogin = true,
                    role = user.role,
                )
                preferencesRepository.saveSession(prefModel)
                Log.d("UserRepository", "Data pengguna: $prefModel")
                Result.success(user)
            } else {
                throw Exception("Pengguna tidak ditemukan")
            }
        } catch (e: Exception) {
            val errorMessage = when (e) {
                is com.google.firebase.auth.FirebaseAuthInvalidUserException -> "Akun dengan email ini tidak ditemukan"
                is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> "Email atau password salah"
                is com.google.firebase.firestore.FirebaseFirestoreException -> "Terjadi kesalahan saat mengambil data pengguna dari Firestore"
                is java.net.SocketTimeoutException -> "Batas waktu jaringan habis. Silakan coba lagi"
                else -> "Kesalahan tak terduga: ${e.message}"
            }
            Result.failure(Exception(errorMessage))
        }
    }

    suspend fun resetPassword(email: String): Result<String> {
        val auth = FirebaseAuth.getInstance()

        return try {
            // Send password reset email
            auth.sendPasswordResetEmail(email).await()

            // If successful, return a success result
            Result.success("Email reset kata sandi telah dikirim ke $email")
        } catch (e: Exception) {
            // Handle errors and return a failure result with a descriptive message
            val errorMessage = when (e) {
                is com.google.firebase.auth.FirebaseAuthInvalidUserException -> "Pengguna dengan email ini tidak ditemukan"
                is java.net.SocketTimeoutException -> "Batas waktu jaringan habis. Silakan coba lagi"
                else -> "Kesalahan tak terduga: ${e.message}"
            }
            Result.failure(Exception(errorMessage))
        }
    }

    suspend fun deleteUser(email: String, password: String): String {
        try {
            // Re-authenticate user
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("UID tidak ditemukan")

            // Delete user data from Firestore
            db.collection("users").document(uid).delete().await()
            db.collection("attendance").document(uid).delete().await()

            // Delete Firebase Authentication user
            authResult.user?.delete()?.await()


            return "Pengguna berhasil dihapus"
        } catch (e: Exception) {
            val errorMessage = when (e) {
                is com.google.firebase.auth.FirebaseAuthInvalidUserException -> "Akun dengan email ini tidak ditemukan"
                is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> "Email atau password salah"
                is com.google.firebase.firestore.FirebaseFirestoreException -> "Terjadi kesalahan saat menghapus data pengguna dari Firestore"
                else -> "Kesalahan tak terduga: ${e.message}"
            }
            return errorMessage
        }
    }

    suspend fun getAllUsers(): List<UserModel> {
        return try {
            // Fetch all users from the "users" collection
            val usersSnapshot = db.collection("users").get().await()
            val userList = usersSnapshot.documents.mapNotNull { it.toObject<UserModel>() }
            Log.d("HistoryFragment", "Fetched ${userList.size} users")
            userList // Return the list of users
        } catch (e: Exception) {
            Log.e("HistoryFragment", "Error fetching users: ${e.message}", e)
            emptyList() // Return an empty list in case of an error
        }
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(preferencesRepository: PreferencesRepository): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(preferencesRepository).also { instance = it }
            }
    }
}
