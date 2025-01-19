package com.murad.presensi.view.admin.delete_user

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.murad.presensi.databinding.FragmentDeleteUserBinding
import com.murad.presensi.view.ViewModelFactory
import com.murad.presensi.view.admin.manage_user.ManagerUserViewModel

class DeleteUserFragment : Fragment() {

    private var _binding: FragmentDeleteUserBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<ManagerUserViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDeleteUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()

        binding.btnHapusPengguna.setOnClickListener {
            val email = binding.editTextEmailEdit.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()

            if (validateInput(email, password)) {
                Log.d("DeleteUserFragment", "Email: $email, Password: $password")
                // Tambahkan logika penghapusan user di sini
                viewModel.deleteUser(email, password)
                Toast.makeText(requireContext(), "Pengguna berhasil dihapus", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.resultDeleteUser.observe(viewLifecycleOwner) { result ->
            showToast(result)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            binding.editTextEmailEdit.error = "Email tidak boleh kosong"
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.editTextEmailEdit.error = "Format email tidak valid"
            return false
        }
        if (password.isEmpty()) {
            binding.editTextPassword.error = "Password tidak boleh kosong"
            return false
        }
        if (password.length < 6) {
            binding.editTextPassword.error = "Password minimal 6 karakter"
            return false
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
