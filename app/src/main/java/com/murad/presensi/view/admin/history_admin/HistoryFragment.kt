package com.murad.presensi.view.admin.history_admin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.murad.presensi.databinding.FragmentHistoryBinding
import com.murad.presensi.view.ViewModelFactory
import com.murad.presensi.view.admin.manage_user.ManagerUserViewModel

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<ManagerUserViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView and Adapter
        val historyAdapter = HistoryAdapter(emptyList())
        binding.rvHistoryAllUser.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistoryAllUser.adapter = historyAdapter

        viewModel.startListeningToAttendanceHistory()
        viewModel.attendanceHistory.observe(viewLifecycleOwner){
            history ->
            Log.d("HistoryFragment", "History data received Listening: $history")
            historyAdapter.setData(history) // Update the adapter with new data

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopListeningToAttendanceHistory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear the binding reference to avoid memory leaks
    }
}