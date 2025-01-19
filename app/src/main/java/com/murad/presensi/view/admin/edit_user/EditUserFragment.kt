package com.murad.presensi.view.admin.edit_user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.murad.presensi.databinding.FragmentEditUserBinding
import com.murad.presensi.view.ViewModelFactory
import com.murad.presensi.view.admin.manage_user.ManagerUserViewModel

class EditUserFragment : Fragment() {
    private var _binding: FragmentEditUserBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<ManagerUserViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentEditUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set OnClickListener for the button
        binding.buttonKirimReset.setOnClickListener {
            val email = binding.editTextEmailEdit.text.toString().trim()

            // Validate email input
            if (email.isEmpty()) {
                Toast.makeText(requireContext(), "Email is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(requireContext(), "Please enter a valid email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else {
                viewModel.resetPassword(email)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
