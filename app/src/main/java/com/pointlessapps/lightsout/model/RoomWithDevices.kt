package com.pointlessapps.lightsout.model

import androidx.room.Embedded
import androidx.room.Relation

data class RoomWithDevices(
    @Embedded val room: DeviceRoom,
    @Relation(
        entity = Device::class,
        entityColumn = "room_id",
        parentColumn = "id",
    )
    val devices: MutableList<Device>
)