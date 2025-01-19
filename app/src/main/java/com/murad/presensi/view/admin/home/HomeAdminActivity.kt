package com.murad.presensi.view.admin.home

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.murad.presensi.R
import com.murad.presensi.databinding.ActivityHomeAdminBinding
import com.murad.presensi.view.ViewModelFactory
import com.murad.presensi.view.admin.manage_user.ManageUserActivity
import com.murad.presensi.view.login.LoginActivity
import com.murad.presensi.view.user.home.HomeUserViewModel
import kotlinx.coroutines.launch

class HomeAdminActivity : AppCompatActivity() {
    private lateinit var binding : ActivityHomeAdminBinding

    // Initialize ViewModel
    private val viewModel by viewModels<HomeAdminViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvUsername.text = intent.getStringExtra("username")
        setupListener()
    }

    private fun setupListener(){
        binding.btnCreateUser.setOnClickListener {
            setupView("Buat Pengguna")
        }
        binding.btnDeleteUser.setOnClickListener {
            setupView("Hapus Pengguna")
        }
        binding.btnEditUser.setOnClickListener {
            setupView("Edit Pengguna")
        }
        binding.btnHistory.setOnClickListener {
            setupView("Riwayat")
        }
        binding.btnLogout.setOnClickListener {
            showPopUp()
        }
    }

    private fun setupView(title: String){
        val intent = Intent(this, ManageUserActivity::class.java)
        intent.putExtra("title", title)
        startActivity(intent)
    }

    private fun showPopUp(){
        val dialog = Dialog(this)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.popup_confirm_logout)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val btnConfirm = dialog.findViewById<Button>(R.id.btnConfirm)
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)

        btnConfirm.setOnClickListener {
            dialog.dismiss()
            lifecycleScope.launch {
                viewModel.logout()
            }
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

}