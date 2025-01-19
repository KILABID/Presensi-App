package com.murad.presensi.view.admin.history_admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.murad.presensi.R
import com.murad.presensi.data.local.model.UserModel

class UserAdapter(
    private var userList: List<UserModel>,
    private val onUserClick: (UserModel) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.userName)
        val userEmail: TextView = itemView.findViewById(R.id.userEmail)
        val userRole: TextView = itemView.findViewById(R.id.userRole)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.userName.text = user.name
        holder.userEmail.text = user.email
        holder.userRole.text = user.role

        // Set onClickListener to invoke the lambda
        holder.itemView.setOnClickListener {
            onUserClick(user)
        }
    }

    override fun getItemCount(): Int = userList.size

    fun setData(newUserList: List<UserModel>) {
        userList = newUserList
        notifyDataSetChanged()
    }
}
