package com.murad.presensi.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.murad.presensi.data.local.preferences.UserPreferences
import com.murad.presensi.utils.UtilsTIme
import kotlinx.coroutines.tasks.await
import java.util.Calendar

class AttendanceRepository(private val userPreferences: UserPreferences) {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val utils = UtilsTIme()

    suspend fun checkIn(date: String, latitude: Double, longitude: Double): String {
        try {
            val currentTime = utils.getDateAndTimeInIndonesia("Asia/Jakarta").second
            val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")

            val userRef = db.collection("attendance").document(userId)
            val logRef = userRef.collection("logs").document(date)

            val logSnapshot = logRef.get().await()
            if (logSnapshot.exists()) {
                return "Already checked in for this date"
            }

            val workStartTime = "08:00:00"
            val status = if (currentTime <= workStartTime) "On Time" else "Late"

            val attendanceData = mapOf(
                "time" to mapOf(
                    "checkIn" to currentTime,
                    "checkOut" to null,
                ),
                "status" to status,
                "location" to mapOf(
                    "latitude" to latitude,
                    "longitude" to longitude
                )
            )

            logRef.set(attendanceData).await()
            userPreferences.checkInStatus(true)
            return "Check-in successful"
        } catch (e: Exception) {
            return "Check-in failed: ${e.message}"
        }
    }


    suspend fun checkOut(date: String): String {
        try {
            val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
            val (_, currentTime) = utils.getDateAndTimeInIndonesia("Asia/Jakarta")

            val userRef = db.collection("attendance").document(userId)
            val logRef = userRef.collection("logs").document(date)

            val logSnapshot = logRef.get().await()
            if (!logSnapshot.exists()) {
                throw Exception("Attendance record for $date not found")
            }

            logRef.update("time.checkOut", currentTime).await()
            userPreferences.checkInStatus(false)
            return "Check-out successful"
        } catch (e: Exception) {
            return "Check-out failed: ${e.message}"
        }
    }

    suspend fun getAttendanceLogs(
        userId: String,
        startDate: String,
        endDate: String,
    ): List<Map<String, Any>> {
        val logs = mutableListOf<Map<String, Any>>()
        val logsRef = db.collection("attendance").document(userId).collection("logs")
        val querySnapshot = logsRef
            .whereGreaterThanOrEqualTo(FieldPath.documentId(), startDate)
            .whereLessThanOrEqualTo(FieldPath.documentId(), endDate)
            .get()
            .await()
        for (document in querySnapshot.documents) {
            logs.add(document.data ?: emptyMap())
        }

        return logs
    }

    suspend fun getUserAttendanceLogs(): List<Map<String, Any>> {
        val allLogs = mutableListOf<Map<String, Any>>()
        try {
            // Ambil semua dokumen dari koleksi users
            val usersSnapshot = db.collection("users").get().await()

            // Hitung tanggal 5 hari yang lalu
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -5)
            val fiveDaysAgo = calendar.time

            // Loop melalui setiap pengguna
            for (userDoc in usersSnapshot.documents) {
                val userId = userDoc.id // Ambil ID pengguna

                // Ambil log absensi dari koleksi attendances berdasarkan ID pengguna
                val logsSnapshot = db.collection("attendances")
                    .document(userId)
                    .collection("logs")
                    .whereGreaterThanOrEqualTo("date", fiveDaysAgo) // Filter log berdasarkan tanggal
                    .get()
                    .await()

                // Loop melalui setiap log dan tambahkan ke allLogs
                for (logDoc in logsSnapshot.documents) {
                    val logData = logDoc.data?.toMutableMap() ?: mutableMapOf()
                    logData["userId"] = userId // Tambahkan userId ke data log
                    logData["date"] = logDoc.id // Tambahkan ID dokumen sebagai tanggal
                    allLogs.add(logData)
                }
            }

            // Urutkan logs berdasarkan tanggal terbaru
            allLogs.sortByDescending { utils.parseDate(it["date"] as String) }

        } catch (e: Exception) {
            Log.e("AttendanceRepository", "Failed to fetch user attendance logs: ${e.message}", e)
        }
        return allLogs
    }

    companion object {
        @Volatile
        private var instance: AttendanceRepository? = null
        fun getInstance(userPreferences: UserPreferences): AttendanceRepository =
            instance ?: synchronized(this) {
                instance ?: AttendanceRepository(userPreferences).also { instance = it }
            }
    }
}
