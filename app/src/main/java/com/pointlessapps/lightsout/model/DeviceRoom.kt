package com.pointlessapps.lightsout.model

import androidx.annotation.DrawableRes
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.util.*

@Entity(tableName = "table_rooms")
data class DeviceRoom(
    @PrimaryKey val id: UUID,
    var name: String,
    @DrawableRes var icon: Int
)