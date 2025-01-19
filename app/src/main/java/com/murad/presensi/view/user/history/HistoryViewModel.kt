package com.murad.presensi.view.user.history

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.murad.presensi.data.local.model.AttendanceModel
import com.murad.presensi.data.local.model.UserAttendance
import com.murad.presensi.data.repository.PreferencesRepository
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryViewModel(
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {

    private val _attendanceHistory = MutableLiveData<List<UserAttendance>>()
    val attendanceHistory: LiveData<List<UserAttendance>> get() = _attendanceHistory

    private var listenerRegistration: ListenerRegistration? = null

    fun startListeningToAttendanceHistory() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val attendanceRef = FirebaseFirestore.getInstance()
            .collection("attendance")
            .document(userId)
            .collection("logs")

        listenerRegistration = attendanceRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                // Handle the error
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val attendanceList = snapshot.documents.mapNotNull { doc ->
                    val date = doc.id
                    Log.d("HistoryViewModel", "Date: $date, Data: ${doc.data}") // Log data
                    val attendanceModel = doc.toObject(AttendanceModel::class.java)
                    attendanceModel?.let { UserAttendance(id = userId, date = date, history = it) }
                }
                _attendanceHistory.value = attendanceList
            }
        }
    }

    fun stopListeningToAttendanceHistory() {
        listenerRegistration?.remove()
    }
}