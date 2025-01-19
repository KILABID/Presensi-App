package com.murad.presensi.data.local.preferences

import java.net.IDN

data class PrefModel(
    val id: String,
    val isLogin: Boolean = false,
    val isCheckIn: Boolean = false,
    val username: String,
    val role: String,
)
