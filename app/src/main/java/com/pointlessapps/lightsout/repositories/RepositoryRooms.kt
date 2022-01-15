package com.pointlessapps.lightsout.repositories

import com.pointlessapps.lightsout.AppDatabase
import com.pointlessapps.lightsout.model.Device
import com.pointlessapps.lightsout.model.DeviceRoom
import com.pointlessapps.lightsout.model.RoomWithDevices
import kotlinx.coroutines.flow.Flow

class RepositoryRooms(database: AppDatabase) {

    private val daoRoom = database.daoRoom()

    fun getAll(): Flow<List<RoomWithDevices>> = daoRoom.getAll()

    suspend fun addDevice(device: Device) {
        daoRoom.insertDevice(device)
    }

    suspend fun updateDevice(device: Device) {
        daoRoom.updateDevice(device)
    }

    suspend fun updateDevicesState(devices: List<Device>) {
        daoRoom.updateDeviceState(*devices.toTypedArray())
    }

    suspend fun addRoom(room: DeviceRoom) {
        daoRoom.insertRoom(room)
    }

    suspend fun removeRoom(room: DeviceRoom) {
        daoRoom.removeRoom(room)
    }

    suspend fun removeDevice(device: Device) {
        daoRoom.removeDevice(device)
    }
}