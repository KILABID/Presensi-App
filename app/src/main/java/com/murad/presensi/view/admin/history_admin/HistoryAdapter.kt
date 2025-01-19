package com.murad.presensi.view.admin.history_admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.murad.presensi.R
import com.murad.presensi.data.local.model.UserAttendance
import com.murad.presensi.databinding.HistoryAllUserItemBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter(
    private var historyItems: List<UserAttendance> = emptyList(), // Default to empty list
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(private val binding: HistoryAllUserItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UserAttendance) {
            binding.tvName.text = item.name // Pastikan item.name ada di UserAttendance
            binding.tvAttendanceStatus.text = item.history.status
            binding.tvAttendanceDate.text = item.date
            binding.tvAttendanceTime.text =
                "${item.history.time.checkIn} - ${item.history.time.checkOut}"

            if (item.history.status == "Late") {
                binding.tvAttendanceStatus.setTextColor(binding.root.context.getColor(R.color.danger))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = HistoryAllUserItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = historyItems[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = historyItems.size

    fun setData(newHistoryItems: List<UserAttendance>) {
        // Sort data by date and time in descending order
        historyItems = newHistoryItems.sortedByDescending { user ->
            parseDateTime(user.date, user.history.time.checkIn ?: "")
                ?: Date(0) // Use a default date if parsing fails
        }
        notifyDataSetChanged() // Notify the adapter that the data has changed
    }

    // Helper function to parse date and time
    private fun parseDateTime(dateString: String, timeString: String): Date? {
        return try {
            val format = SimpleDateFormat("EEEE, d MMMM yyyy HH:mm", Locale("id", "ID"))
            // Combine date and time into a single string
            val combinedDateTime = "$dateString $timeString"
            format.parse(combinedDateTime)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}