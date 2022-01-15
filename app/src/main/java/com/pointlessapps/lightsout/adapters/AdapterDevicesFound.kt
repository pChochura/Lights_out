package com.pointlessapps.lightsout.adapters

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.pointlessapps.lightsout.R
import com.pointlessapps.lightsout.databinding.ItemDeviceFoundBinding
import com.pointlessapps.lightsout.model.Device

class AdapterDevicesFound : RecyclerView.Adapter<AdapterDevicesFound.ViewHolder>() {

    private val devices = mutableListOf<Device>()
    private var selectedPosition: Int = -1

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemDeviceFoundBinding.inflate(LayoutInflater.from(parent.context)).apply {
            root.clipToOutline = true
        }
    )

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.imageIcon.setColorFilter(
            Color.HSVToColor(floatArrayOf(devices[position].hue.toFloat(), 1f, 1f))
        )
        holder.binding.root.setBackgroundResource(
            if (position == selectedPosition)
                R.drawable.background_rounded_4dp
            else R.drawable.outline_rounded_4dp
        )
        holder.binding.root.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                holder.binding.root.context,
                if (position == selectedPosition)
                    R.color.lightGrey
                else R.color.darkGrey
            )
        )
        holder.binding.root.setOnClickListener {
            selectedPosition = holder.adapterPosition
            notifyDataSetChanged()
        }
    }

    override fun getItemCount() = devices.size
    override fun getItemId(position: Int) = devices[position].macAddress.hashCode().toLong()

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<Device>) {
        devices.clear()
        devices.addAll(list)
        notifyDataSetChanged()
    }

    fun getSelectedDevice(): Device? = devices.getOrNull(selectedPosition)

    class ViewHolder(val binding: ItemDeviceFoundBinding) : RecyclerView.ViewHolder(binding.root)
}