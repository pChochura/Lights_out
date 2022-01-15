package com.pointlessapps.lightsout.daos

import androidx.room.*
import com.pointlessapps.lightsout.model.Device
import com.pointlessapps.lightsout.model.DeviceRoom
import com.pointlessapps.lightsout.model.RoomWithDevices
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface DaoDeviceRoom {

    @Transaction
    @Query("SELECT * FROM table_rooms")
    fun getAll(): Flow<List<RoomWithDevices>>

    @Transaction
    @Query("SELECT * FROM table_rooms WHERE id = :id")
    suspend fun getById(id: UUID): RoomWithDevices

    @Insert
    suspend fun insertDevice(vararg devices: Device)

    @Update
    suspend fun updateDevice(vararg devices: Device)

    @Query("UPDATE table_devices SET `on` = :state WHERE mac_address = :macAddress")
    suspend fun updateDeviceState(macAddress: String, state: Boolean)

    @Transaction
    suspend fun updateDeviceState(vararg devices: Device) {
        devices.forEach { updateDeviceState(it.macAddress, it.on) }
    }

    @Insert
    suspend fun insertRoom(vararg rooms: DeviceRoom)

    @Delete
    suspend fun removeRoom(vararg rooms: DeviceRoom)

    @Delete
    suspend fun removeDevice(vararg devices: Device)
}