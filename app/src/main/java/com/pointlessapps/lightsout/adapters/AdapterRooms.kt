package com.pointlessapps.lightsout.adapters

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.pointlessapps.lightsout.R
import com.pointlessapps.lightsout.databinding.ItemRoomAddBinding
import com.pointlessapps.lightsout.databinding.ItemRoomBinding
import com.pointlessapps.lightsout.model.Device
import com.pointlessapps.lightsout.model.RoomWithDevices

class AdapterRooms : RecyclerView.Adapter<AdapterRooms.ViewHolder>() {

    private val rooms = mutableListOf<RoomWithDevices>()
    private var selectedRoom: RoomWithDevices? = rooms.firstOrNull()
    var onAddPressedListener: (() -> Unit)? = null
    var onLongPressedListener: ((RoomWithDevices, position: Int) -> Boolean)? = null
    var onChangedRoomListener: ((RoomWithDevices) -> Unit)? = null
        set(value) {
            field = value
            selectedRoom?.also { value?.invoke(it) }
        }

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        if (viewType == 0) {
            ItemRoomBinding.inflate(LayoutInflater.from(parent.context)).apply {
                root.clipToOutline = true
            }
        } else {
            ItemRoomAddBinding.inflate(LayoutInflater.from(parent.context)).apply {
                root.clipToOutline = true
                root.setOnClickListener { onAddPressedListener?.invoke() }
            }
        }
    )

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder.binding !is ItemRoomBinding) {
            return
        }

        holder.binding.textName.text = rooms[position].room.name
        holder.binding.imageIcon.setImageResource(rooms[position].room.icon)

        holder.binding.root.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                holder.binding.root.context, when (selectedRoom == rooms[position]) {
                    true -> R.color.blue
                    else -> R.color.lightGrey
                }
            )
        )

        holder.binding.root.setOnClickListener {
            if (selectedRoom != rooms[position]) {
                selectedRoom = rooms[position]
                onChangedRoomListener?.invoke(rooms[position])
                notifyDataSetChanged()
            }
        }

        holder.binding.root.setOnLongClickListener {
            onLongPressedListener?.invoke(rooms[position], position) ?: false
        }
    }

    override fun getItemCount() = rooms.size + 1
    override fun getItemViewType(position: Int) = if (position < rooms.size) 0 else 1
    override fun getItemId(position: Int) =
        if (position < rooms.size) rooms[position].room.id.hashCode().toLong() else 0

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<RoomWithDevices>) {
        rooms.clear()
        rooms.addAll(list)
        if (selectedRoom == null || selectedRoom !in list) {
            selectedRoom = list.firstOrNull()?.also {
                onChangedRoomListener?.invoke(it)
            }
        }
        notifyDataSetChanged()
    }

    fun getSelectedRoom(): RoomWithDevices? = selectedRoom

    class ViewHolder(val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root)
}