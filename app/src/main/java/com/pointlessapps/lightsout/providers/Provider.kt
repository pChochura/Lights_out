package com.pointlessapps.lightsout.providers

import com.pointlessapps.lightsout.model.Device
import java.util.concurrent.TimeUnit

sealed class Provider {
    abstract fun update(state: Boolean, color: Int, brightness: Float)
    abstract fun turnOn(color: Int, brightness: Float)
    abstract fun turnOff()
    abstract suspend fun findLights(
        timeout: Long = TimeUnit.SECONDS.toMillis(5),
        onDeviceFoundListener: (List<Device>) -> Unit
    )
    abstract suspend fun getLights(
        timeout: Long = TimeUnit.SECONDS.toMillis(5),
        onDeviceFoundListener: (List<Device>) -> Unit
    )
}