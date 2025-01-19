package com.murad.presensi.view.user.history

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.murad.presensi.databinding.ActivityHistoryBinding
import com.murad.presensi.view.ViewModelFactory
import com.murad.presensi.view.user.home.HomeUserActivity

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding

    private val viewModel by viewModels<HistoryViewModel> {
        ViewModelFactory.getInstance(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, HomeUserActivity::class.java))
            finish()
        }

        val username = intent.getStringExtra("username").toString()
        Log.d("Username", username)
        binding.tvUsernameDetailHistory.text = username
        setupListHistory()


    }

    private fun setupListHistory() {
        // Setup RecyclerView and Adapter
        val listAdapter = DetailHistoryListAdapter()
        binding.rvHistoryDetail.adapter = listAdapter
        binding.rvHistoryDetail.layoutManager = LinearLayoutManager(this)

        // Observe the attendance history data
        viewModel.startListeningToAttendanceHistory()
        viewModel.attendanceHistory.observe(this) { attendanceHistoryList ->
            Log.d("AttendanceHistory", attendanceHistoryList.toString())
            listAdapter.submitAndSortList(attendanceHistoryList) // Submit and sort the list
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopListeningToAttendanceHistory()
    }
}