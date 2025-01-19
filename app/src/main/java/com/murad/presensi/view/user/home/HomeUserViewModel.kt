package com.murad.presensi.view.user.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.murad.presensi.data.local.model.AttendanceModel
import com.murad.presensi.data.local.model.UserAttendance
import com.murad.presensi.data.local.preferences.PrefModel
import com.murad.presensi.data.repository.AttendanceRepository
import com.murad.presensi.data.repository.PreferencesRepository
import kotlinx.coroutines.launch

class HomeUserViewModel(
private val repository: PreferencesRepository,
private val attendanceRepository: AttendanceRepository,
) : ViewModel() {

    private var _isCheckIn = MutableLiveData<Boolean>()
    val isCheckIn: LiveData<Boolean> get() = _isCheckIn

    private val _attendanceHistory = MutableLiveData<List<UserAttendance>>()
    val attendanceHistory: LiveData<List<UserAttendance>> get() = _attendanceHistory

    private var listenerRegistration: ListenerRegistration? = null

    private val checkInAttendance = MutableLiveData<String>()
    val checkIn: LiveData<String> get() = checkInAttendance


    fun getSession(): LiveData<PrefModel> {
        return repository.getSession().asLiveData()
    }

    fun setIsCheckIn(status: Boolean) {
        _isCheckIn.value = status
    }

    suspend fun logout() {
        repository.logout()
    }

    fun saveCheckIn(date: String, long: Double, lat: Double) {
        viewModelScope.launch {
            val result = attendanceRepository.checkIn(date, long, lat)
            checkInAttendance.value = result
        }
    }


    fun saveCheckOut(date: String) {
        viewModelScope.launch {
            attendanceRepository.checkOut(date)
        }
    }

    fun startListeningToAttendanceHistory() {
        val userId = FirebaseAuth.getInstance().currentUser ?.uid ?: return
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
                    val date = doc.id // Get the document ID as the date
                    val attendanceModel = doc.toObject(AttendanceModel::class.java)
                    attendanceModel?.let { UserAttendance(id = userId, date = date, history = it) } // Set `id` as needed
                }
                _attendanceHistory.value = attendanceList
            }
        }
    }

    fun stopListeningToAttendanceHistory() {
        listenerRegistration?.remove()
    }
}