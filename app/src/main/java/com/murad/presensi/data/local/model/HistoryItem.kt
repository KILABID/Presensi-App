package com.murad.presensi.data.local.model


data class HistoryItem(
    val uid: String, // UID dokumen
    val name: String,
    val attendanceStatus: String,
    val attendanceTime: String,
)