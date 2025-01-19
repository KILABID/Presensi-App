package com.murad.presensi.view.admin.create_user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.murad.presensi.databinding.FragmentCreateUserBinding
import com.murad.presensi.view.ViewModelFactory
import com.murad.presensi.view.admin.manage_user.ManagerUserViewModel

class CreateUserFragment : Fragment() {

    private var _binding: FragmentCreateUserBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<ManagerUserViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCreateUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpinner()
        setupListeners()
        observeViewModel()
    }

    private fun setupSpinner() {
        val roles = listOf("admin", "user")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerRole.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnDaftar.setOnClickListener {
            val username = binding.editTextUsername.text.toString().trim()
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()
            val confirmPassword = binding.editTextConfirmPassword.text.toString().trim()
            val selectedRole = binding.spinnerRole.selectedItem.toString()

            if (validateInput(username, email, password, confirmPassword)) {
                viewModel.createUser(email, password, username, selectedRole)
            }
        }
    }

    private fun validateInput(
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        when {
            username.isEmpty() -> {
                showToast("Username tidak boleh kosong")
                return false
            }
            email.isEmpty() -> {
                showToast("Email tidak boleh kosong")
                return false
            }
            password.isEmpty() -> {
                showToast("Password tidak boleh kosong")
                return false
            }
            password != confirmPassword -> {
                showToast("Password dan Konfirmasi Password tidak cocok")
                return false
            }
        }
        return true
    }

    private fun observeViewModel() {
        viewModel.resultCreateUser.observe(viewLifecycleOwner) { result ->
            showToast(result)
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
