package com.pointlessapps.lightsout

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pointlessapps.lightsout.adapters.AdapterDevices
import com.pointlessapps.lightsout.adapters.AdapterRooms
import com.pointlessapps.lightsout.databinding.ActivityMainBinding
import com.pointlessapps.lightsout.decorations.GridSpacingItemDecoration
import com.pointlessapps.lightsout.dialogs.DeviceAddDialog
import com.pointlessapps.lightsout.dialogs.DeviceDetailsDialog
import com.pointlessapps.lightsout.dialogs.RoomAddDialog
import com.pointlessapps.lightsout.model.Device
import com.pointlessapps.lightsout.model.RoomWithDevices
import com.pointlessapps.lightsout.viewModels.ViewModelMain
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: ViewModelMain by viewModel()
    private val adapterDevices = AdapterDevices()
    private val adapterRooms = AdapterRooms()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.listRooms.apply {
            layoutManager = LinearLayoutManager(applicationContext, RecyclerView.HORIZONTAL, false)
            adapter = adapterRooms
            addItemDecoration(
                DividerItemDecoration(
                    applicationContext,
                    RecyclerView.HORIZONTAL
                ).apply {
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.divider_horizontal_16dp
                    )?.let(this::setDrawable)
                })
        }
        adapterRooms.onChangedRoomListener = { room -> adapterDevices.updateList(room.devices) }
        adapterRooms.onAddPressedListener = { launchRoomAddView() }
        adapterRooms.onLongPressedListener = { room, _ ->
            launchRoomRemoveView(room)
            true
        }

        binding.listDevices.apply {
            layoutManager = GridLayoutManager(applicationContext, 2, RecyclerView.VERTICAL, false)
            adapter = adapterDevices
            addItemDecoration(GridSpacingItemDecoration(2, 16.px, false))
        }
        adapterDevices.onPressedListener = { device, position ->
            device.on = !device.on
            device.switch()
            viewModel.updateDevice(device)
            adapterDevices.notifyItemChanged(position)
        }
        adapterDevices.onLongPressedListener = { device, _ ->
            launchDeviceDetailsView(device)
            true
        }
        adapterDevices.onAddPressedListener = { launchDeviceAddView() }

        viewModel.rooms.observe(this) {
            println("LOG!, updated")
            println("LOG!, list: ${it.toTypedArray().contentToString()}")
            adapterRooms.updateList(it)
        }
        viewModel.updateDevicesState()
    }

    private fun launchDeviceDetailsView(device: Device) {
        DeviceDetailsDialog(device).show(
            supportFragmentManager,
            DeviceDetailsDialog::class.java.simpleName
        )
    }

    private fun launchDeviceAddView() {
        adapterRooms.getSelectedRoom()?.also {
            DeviceAddDialog(it).show(
                supportFragmentManager,
                DeviceAddDialog::class.java.simpleName
            )
        }
    }

    private fun launchRoomAddView() {
        RoomAddDialog().show(
            supportFragmentManager,
            RoomAddDialog::class.java.simpleName
        )
    }

    private fun launchRoomRemoveView(room: RoomWithDevices) {
        Dialog(this).apply {
            setContentView(R.layout.dialog_remove)
            findViewById<View>(R.id.buttonCancel).setOnClickListener { dismiss() }
            findViewById<View>(R.id.buttonRemove).setOnClickListener {
                viewModel.removeRoom(room.room)
                dismiss()
            }
        }.show()
    }
}