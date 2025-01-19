package com.murad.presensi.view.admin.manage_user

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.murad.presensi.R
import com.murad.presensi.databinding.ActivityManageUserBinding
import com.murad.presensi.view.admin.create_user.CreateUserFragment
import com.murad.presensi.view.admin.delete_user.DeleteUserFragment
import com.murad.presensi.view.admin.edit_user.EditUserFragment
import com.murad.presensi.view.admin.history_admin.HistoryFragment

class ManageUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManageUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra("title")
        binding.textView.text = title

        binding.btnBack.setOnClickListener {
            finish()
        }

        val screen = ManageUserScreen.fromTitle(title)
        if (screen != null) {
            setupView(screen)
        } else {
            // Handle case if title does not match any screen
            binding.textView.text = "Invalid Screen"
        }
    }

    private fun setupView(screen: ManageUserScreen) {
        val fragment: Fragment = when (screen) {
            ManageUserScreen.CreateUser -> CreateUserFragment()
            ManageUserScreen.DeleteUser -> DeleteUserFragment()
            ManageUserScreen.EditUser -> EditUserFragment()
            ManageUserScreen.History -> HistoryFragment()
        }

        supportFragmentManager.beginTransaction()
            .replace(binding.container.id, fragment)
            .commit()
    }
}