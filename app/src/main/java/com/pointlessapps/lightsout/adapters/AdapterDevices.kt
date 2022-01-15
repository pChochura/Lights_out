package com.pointlessapps.lightsout.adapters

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.pointlessapps.lightsout.R
import com.pointlessapps.lightsout.databinding.ItemDeviceAddBinding
import com.pointlessapps.lightsout.databinding.ItemDeviceBinding
import com.pointlessapps.lightsout.model.Device

class AdapterDevices : RecyclerView.Adapter<AdapterDevices.ViewHolder>() {

    private val devices = mutableListOf<Device>()
    var onLongPressedListener: ((Device, position: Int) -> Boolean)? = null
    var onPressedListener: ((Device, position: Int) -> Unit)? = null
    var onAddPressedListener: (() -> Unit)? = null

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        if (viewType == 0) {
            ItemDeviceBinding.inflate(LayoutInflater.from(parent.context)).apply {
                root.clipToOutline = true
            }
        } else {
            ItemDeviceAddBinding.inflate(LayoutInflater.from(parent.context)).apply {
                root.clipToOutline = true
                root.setOnClickListener { onAddPressedListener?.invoke() }
            }
        }
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder.binding !is ItemDeviceBinding) {
            return
        }

        holder.binding.textName.text = devices[position].name
        holder.binding.imageIcon.setImageResource(devices[position].icon)
        holder.binding.imageSwitch.setImageResource(
            when (devices[position].on) {
                true -> R.drawable.switch_on
                else -> R.drawable.switch_off
            }
        )
        holder.binding.root.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                holder.binding.root.context,
                when (devices[position].on) {
                    true -> R.color.blue
                    else -> R.color.lightGrey
                }
            )
        )

        val tintColor = ContextCompat.getColor(
            holder.binding.root.context, when (devices[position].on) {
                true -> R.color.darkGrey
                else -> R.color.grey
            }
        )
        holder.binding.imageSwitch.setColorFilter(tintColor)
        holder.binding.imageIcon.setColorFilter(tintColor)
        holder.binding.textName.setTextColor(tintColor)
        holder.binding.imageIcon.backgroundTintList = ColorStateList.valueOf(tintColor)

        holder.binding.root.setOnClickListener {
            onPressedListener?.invoke(devices[position], position)
        }
        holder.binding.root.setOnLongClickListener {
            onLongPressedListener?.invoke(devices[position], position) ?: false
        }
    }

    override fun getItemCount() = devices.size + 1
    override fun getItemViewType(position: Int) = if (position < devices.size) 0 else 1
    override fun getItemId(position: Int) =
        if (position < devices.size) devices[position].macAddress.hashCode().toLong() else 0

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<Device>) {
        devices.clear()
        devices.addAll(list)
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root)
}