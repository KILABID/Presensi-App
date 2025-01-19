package com.murad.presensi.view.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.murad.presensi.data.local.model.UserModel
import com.murad.presensi.databinding.ActivityLoginBinding
import com.murad.presensi.view.ViewModelFactory
import com.murad.presensi.view.admin.home.HomeAdminActivity
import com.murad.presensi.view.user.home.HomeUserActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    // Initialize ViewModel
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Observe login result
        viewModel.loginResult.observe(this) { result ->
//            Log.d("Data", result.toString())
            when {
                result.isFailure -> {
                    showToast(result.toString())
                }

                result.isSuccess -> {
                    result.getOrNull()?.let { user ->
                        Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show()
                        // Proceed with login if successful
                        performLogin(user)
                    }
                }
            }
        }


        // Observe loading state
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.buttonLogin.isEnabled = !isLoading // Disable button during loading
        }

        // Set login button click listener
        binding.buttonLogin.setOnClickListener {
            val email = binding.editTextUsername.text.toString()
            val password = binding.editTextPassword.text.toString()

            // Validate inputs
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in both email and password.", Toast.LENGTH_SHORT)
                    .show()
            } else {
                // Call login function if input is valid
                viewModel.login(email, password)
            }
        }
    }

    private fun getSession() {
        viewModel.getSession().observe(this) { user ->
            Log.d("Data", "Session: ${user}")
            if (!user.isLogin) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                when(user.role){
                    "admin" -> {
                        val intent = Intent(this, HomeAdminActivity::class.java)
                        intent.putExtra("username", user.username)
                        intent.putExtra("role", user.role)
                        startActivity(intent)
                        finish()
                    }
                    "user" -> {
                        val intent = Intent(this, HomeUserActivity::class.java)
                        intent.putExtra("username", user.username)
                        intent.putExtra("role", user.role)
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun showToast(msg: String){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun performLogin(userData: UserModel) {
        when (userData.role) {
            "admin" -> {
                val intent = Intent(this, HomeAdminActivity::class.java)
                intent.putExtra("username", userData.name)
                intent.putExtra("role", userData.role)
                startActivity(intent)
                finish()
            }

            "user" -> {
                val intent = Intent(this, HomeUserActivity::class.java)
                intent.putExtra("username", userData.name)
                intent.putExtra("role", userData.role)
                startActivity(intent)
                finish()
            }

            else -> {
                Toast.makeText(this, "Invalid role.", Toast.LENGTH_SHORT).show()
                throw Exception("Role not found")
            }
        }
    }
}
