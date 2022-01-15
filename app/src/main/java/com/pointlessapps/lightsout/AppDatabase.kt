package com.pointlessapps.lightsout

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pointlessapps.lightsout.converters.ConverterProvider
import com.pointlessapps.lightsout.daos.DaoDeviceRoom
import com.pointlessapps.lightsout.model.Device
import com.pointlessapps.lightsout.model.DeviceRoom

@Database(
    entities = [
        DeviceRoom::class,
        Device::class
    ],
    version = 14
)
@TypeConverters(
    value = [
        ConverterProvider::class
    ]
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun daoRoom(): DaoDeviceRoom

    companion object {
        fun get(context: Context) = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_db"
        ).fallbackToDestructiveMigration().build()
    }
}