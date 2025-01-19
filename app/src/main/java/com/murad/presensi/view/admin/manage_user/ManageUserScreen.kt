package com.murad.presensi.view.admin.manage_user

sealed class ManageUserScreen(val title: String) {
    object CreateUser : ManageUserScreen("Buat Pengguna")
    object DeleteUser : ManageUserScreen("Hapus Pengguna")
    object EditUser : ManageUserScreen("Edit Pengguna")
    object History : ManageUserScreen("Riwayat")

    companion object {
        fun fromTitle(title: String?): ManageUserScreen? {
            return when (title) {
                CreateUser.title -> CreateUser
                DeleteUser.title -> DeleteUser
                EditUser.title -> EditUser
                History.title -> History
                else -> null
            }
        }
    }
}
