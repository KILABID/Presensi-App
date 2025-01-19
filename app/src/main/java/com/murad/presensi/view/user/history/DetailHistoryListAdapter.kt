package com.murad.presensi.view.user.history

import com.murad.presensi.databinding.DetailItemHistoryBinding
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.murad.presensi.data.local.model.UserAttendance
import java.text.SimpleDateFormat
import java.util.*

class DetailHistoryListAdapter : ListAdapter<UserAttendance, DetailHistoryListAdapter.ViewHolder>(DIFF_CALLBACK) {

    class ViewHolder(private val binding: DetailItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: UserAttendance) {
            binding.apply {
                // Tampilkan tanggal dan waktu check-in & check-out
                tvTanggal.text = "Tanggal : ${data.date}"
                tvJam.text = "Jam : ${data.history.time.checkIn ?: "N/A"} - ${data.history.time.checkOut ?: "N/A"}"
                tvStatus.text = "Status : ${data.history.status}"

                // Cek status dan ubah warna teks jika statusnya "Late"
                tvStatus.setTextColor(
                    if (data.history.status == "Late") Color.RED else Color.BLACK
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DetailItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val history = getItem(position)
        holder.bind(history)
    }

    // Function to submit and sort list
    fun submitAndSortList(list: List<UserAttendance>) {
        val sortedList = list.sortedByDescending { userAttendance ->
            parseDate(userAttendance.date)
        }
        submitList(sortedList)
    }

    // Helper function to parse date
    private fun parseDate(dateString: String): Date? {
        return try {
            val format = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID"))
            format.parse(dateString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UserAttendance>() {
            override fun areItemsTheSame(
                oldItem: UserAttendance,
                newItem: UserAttendance
            ): Boolean {
                // Compare unique identifiers (e.g., date) to check if they are the same item
                return oldItem.date == newItem.date
            }

            override fun areContentsTheSame(
                oldItem: UserAttendance,
                newItem: UserAttendance
            ): Boolean {
                // Compare the contents of the items
                return oldItem == newItem
            }
        }
    }
}
