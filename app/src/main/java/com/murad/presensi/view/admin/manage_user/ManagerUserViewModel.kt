package com.murad.presensi.view.admin.manage_user

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.murad.presensi.data.local.model.AttendanceModel
import com.murad.presensi.data.local.model.HistoryItem
import com.murad.presensi.data.local.model.UserAttendance
import com.murad.presensi.data.local.model.UserModel
import com.murad.presensi.data.repository.AttendanceRepository
import com.murad.presensi.data.repository.UserRepository
import kotlinx.coroutines.launch

class ManagerUserViewModel(private val userRepository: UserRepository, private val attendanceRepository: AttendanceRepository) : ViewModel() {

    private val _resultCreateUser = MutableLiveData<String>()
    val resultCreateUser: LiveData<String> get() = _resultCreateUser

    private val _resultDeleteUser = MutableLiveData<String>()
    val resultDeleteUser: LiveData<String> get() = _resultDeleteUser

    private val _allDataResult = MutableLiveData<List<UserAttendance>>()
    val allDataResult: LiveData<List<UserAttendance>> get() = _allDataResult

    private val _allUsers = MutableLiveData<List<UserModel>>()
    val allUsers: LiveData<List<UserModel>> get() = _allUsers

    private val _historyItems = MutableLiveData<List<HistoryItem>>()
    val historyItems: LiveData<List<HistoryItem>> get() = _historyItems

    private val _attendanceHistory = MutableLiveData<List<UserAttendance>>()
    val attendanceHistory: LiveData<List<UserAttendance>> get() = _attendanceHistory

    private var listenerRegistration: ListenerRegistration? = null



    private val _attendanceLogs = MutableLiveData<List<Map<String, Any>>>()
    val attendanceLogs: LiveData<List<Map<String, Any>>> get() = _attendanceLogs

    // Function to create a user
    fun createUser(email: String, password: String, name: String, role: String) {
        viewModelScope.launch {
            try {
                val result = userRepository.createUser(email, password, name, role)
                _resultCreateUser.value = result
            } catch (e: Exception) {
                Log.e("ManagerUserViewModel", "Error creating user: ${e.message}", e)
            }
        }
    }

    // Function to delete a user
    fun deleteUser(email: String, password: String) {
        viewModelScope.launch {
            try {
                val result = userRepository.deleteUser(email, password)
                _resultDeleteUser.value = result
            } catch (e: Exception) {
                Log.e("ManagerUserViewModel", "Error deleting user: ${e.message}", e)
            }
        }
    }

    // Function to reset the user's password
    fun resetPassword(email: String) {
        viewModelScope.launch {
            try {
                userRepository.resetPassword(email)
            } catch (e: Exception) {
                Log.e("ManagerUserViewModel", "Error resetting password: ${e.message}", e)
            }
        }
    }

    fun startListeningToAttendanceHistory() {
        // Ambil semua pengguna terlebih dahulu
        viewModelScope.launch {
            try {
                val users = userRepository.getAllUsers() // Ambil semua pengguna
                val attendanceList = mutableListOf<UserAttendance>()

                // Loop melalui setiap pengguna untuk mengambil log absensi
                users.forEach { user ->
                    val userId = user.id // Ambil ID pengguna
                    val logsRef = FirebaseFirestore.getInstance()
                        .collection("attendance")
                        .document(userId)
                        .collection("logs")

                    logsRef.get().addOnSuccessListener { logsSnapshot ->
                        logsSnapshot.documents.forEach { doc ->
                            val date = doc.id
                            Log.d("HistoryViewModel", "Date: $date, Data: ${doc.data}") // Log data
                            val attendanceModel = doc.toObject(AttendanceModel::class.java)
                            attendanceModel?.let {
                                attendanceList.add(UserAttendance(id = userId, name= user.name, date = date, history = it))
                                Log.d("HistoryViewModel", "AttendanceList: $attendanceList")
                            }
                        }
                        // Update LiveData setelah semua data diambil
                        _attendanceHistory.value = attendanceList
                    }.addOnFailureListener { exception ->
                        Log.e("HistoryViewModel", "Error getting logs for user $userId", exception)
                    }
                }
            } catch (e: Exception) {
                Log.e("ManagerUser ViewModel", "Error fetching users: ${e.message}", e)
            }
        }
    }

    fun stopListeningToAttendanceHistory() {
        listenerRegistration?.remove()
    }
}
