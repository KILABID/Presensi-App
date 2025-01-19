package com.murad.presensi.data.local.model

data class UserModel(
    val id: String = "",            // ID unik pengguna
    val name: String = "",          // Nama pengguna
    val role: String = "",          // Peran pengguna (misalnya Admin, User)
    val email: String = "",         // Email pengguna
)
