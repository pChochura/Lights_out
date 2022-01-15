package com.pointlessapps.lightsout.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.pointlessapps.lightsout.model.Device
import com.pointlessapps.lightsout.model.DeviceRoom
import com.pointlessapps.lightsout.providers.ProviderWiz
import com.pointlessapps.lightsout.repositories.RepositoryRooms
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class ViewModelMain(private val repositoryRooms: RepositoryRooms) : ViewModel() {

    private val providerWiz = ProviderWiz()
    val rooms = repositoryRooms.getAll().asLiveData()

    fun addDevice(device: Device) {
        viewModelScope.launch(Dispatchers.IO) {
            repositoryRooms.addDevice(device)
        }
    }

    fun updateDevice(device: Device) {
        viewModelScope.launch(Dispatchers.IO) {
            repositoryRooms.updateDevice(device)
        }
    }

    fun addRoom(room: DeviceRoom) {
        viewModelScope.launch(Dispatchers.IO) {
            repositoryRooms.addRoom(room)
        }
    }

    fun removeRoom(room: DeviceRoom) {
        viewModelScope.launch(Dispatchers.IO) {
            repositoryRooms.removeRoom(room)
        }
    }

    fun removeDevice(device: Device) {
        viewModelScope.launch(Dispatchers.IO) {
            repositoryRooms.removeDevice(device)
        }
    }

    fun updateDevicesState() {
        viewModelScope.launch(Dispatchers.IO) {
            providerWiz.getLights {
                println("LOG!, devices: ${it.toTypedArray().contentToString()}")
                launch(Dispatchers.IO) {
                    repositoryRooms.updateDevicesState(it)
                }
            }
        }
    }
}