package com.pointlessapps.lightsout.model

import android.graphics.Color
import androidx.annotation.DrawableRes
import androidx.annotation.IntRange
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.pointlessapps.lightsout.providers.Provider
import org.json.JSONObject
import java.net.DatagramPacket
import java.util.*

@Entity(
    foreignKeys = [ForeignKey(
        entity = DeviceRoom::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("room_id"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )],
    tableName = "table_devices"
)
data class Device(
    @PrimaryKey @ColumnInfo(name = "mac_address") val macAddress: String,
    @ColumnInfo(name = "room_id") var roomId: UUID,
    val provider: Provider,
    val ip: String,
    @IntRange(from = 10, to = 100) var brightness: Int,
    @IntRange(from = 0, to = 360) var hue: Int,
    var on: Boolean = false,
    var name: String = "",
    @DrawableRes var icon: Int = 0,
) {

    fun switch() = if (on) turnOn() else turnOff()
    fun turnOff() = provider.turnOff()
    fun turnOn() =
        provider.turnOn(Color.HSVToColor(floatArrayOf(hue.toFloat(), 1f, 1f)), brightness.toFloat())

    fun update() = provider.update(
        on,
        Color.HSVToColor(floatArrayOf(hue.toFloat(), 1f, 1f)),
        brightness.toFloat()
    )

    companion object {
        fun fromPacket(
            packet: DatagramPacket,
            provider: Provider,
            hue: Int? = null,
            dimming: Int? = null
        ): Device? {
            return runCatching {
                JSONObject(String(packet.data)).getJSONObject("result").let { result ->
                    val ip = packet.address.hostAddress ?: ""
                    val macAddress = result.getString("mac")
                    val on = result.getBoolean("state")
                    val r = result.getInt("r")
                    val g = result.getInt("g")
                    val b = result.getInt("b")
                    val hsvColor = FloatArray(3)
                    Color.RGBToHSV(r, g, b, hsvColor)
                    Device(
                        macAddress,
                        UUID.randomUUID(),
                        provider,
                        ip,
                        dimming ?: result.getInt("dimming"),
                        hue ?: hsvColor[0].toInt(),
                        on
                    )
                }
            }.getOrNull()
        }
    }
}
