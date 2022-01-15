package com.pointlessapps.lightsout.providers

import android.graphics.Color
import com.pointlessapps.lightsout.model.Device
import kotlinx.coroutines.*
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress
import java.util.*
import kotlin.random.Random

class ProviderWiz : Provider() {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val socket = DatagramSocket(null).apply {
        reuseAddress = true
        broadcast = true
        bind(InetSocketAddress(38899))
    }

    override fun update(state: Boolean, color: Int, brightness: Float) {
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        sendData(
            """
            {"method":"setPilot","params":{"state":$state,"r":$r,"g":$g,"b":$b,"dimming":${brightness.toInt()}}}
        """.trimIndent().toByteArray()
        )
    }

    override fun turnOn(color: Int, brightness: Float) {
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        sendData(
            """
            {"method":"setPilot","params":{"state":true,"r":$r,"g":$g,"b":$b,"dimming":${brightness.toInt()}}}
        """.trimIndent().toByteArray()
        )
    }

    override fun turnOff() {
        sendData(
            """
            {"method":"setPilot","params":{"state":false}}
        """.trimIndent().toByteArray()
        )
    }

    override suspend fun findLights(timeout: Long, onDeviceFoundListener: (List<Device>) -> Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            withTimeout(timeout) {
                val socket = DatagramSocket(null)
                socket.reuseAddress = true
                socket.broadcast = true
                socket.bind(InetSocketAddress(38899))
                val buffer = ByteArray(500)
                val packet = DatagramPacket(buffer, buffer.size)
                val devices = mutableMapOf<String, Device>()
                while (true) {
                    socket.receive(packet)
                    if (String(packet.data).contains(""""result":{""")) {
                        packet.address.hostAddress?.also { address ->
                            val hue = Random.nextInt(360)
                            Device.fromPacket(packet, this@ProviderWiz, hue, 10)
                                ?.also fromPacket@{ device ->
                                    if (devices.containsKey(device.macAddress)) {
                                        return@fromPacket
                                    }

                                    val color =
                                        Color.HSVToColor(floatArrayOf(hue.toFloat(), 1f, 1f))
                                    val r = Color.red(color)
                                    val g = Color.green(color)
                                    val b = Color.blue(color)
                                    sendData(
                                        """
                                    {"method":"setPilot","params":{"state":true,"r":$r,"g":$g,"b":$b,"dimming":10}}
                                """.trimIndent().toByteArray(),
                                        address
                                    )
                                    devices[device.macAddress] = (device)
                                    withContext(Dispatchers.Main) {
                                        onDeviceFoundListener(devices.values.toList())
                                    }
                                }
                        }
                    }
                    Arrays.fill(buffer, 0)
                }
            }
        }
        sendData(
            """
            {"method":"getPilot","params":{}}
        """.trimIndent().toByteArray(),
            repeatCount = 3
        )
    }

    override suspend fun getLights(timeout: Long, onDeviceFoundListener: (List<Device>) -> Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            withTimeout(timeout) {
                val socket = DatagramSocket(null)
                socket.reuseAddress = true
                socket.broadcast = true
                socket.bind(InetSocketAddress(38899))
                val buffer = ByteArray(500)
                val packet = DatagramPacket(buffer, buffer.size)
                val devices = mutableMapOf<String, Device>()
                while (true) {
                    socket.receive(packet)
                    println("LOG!, ${String(packet.data)}")
                    if (String(packet.data).contains(""""result":{""")) {
                        packet.address.hostAddress?.also {
                            Device.fromPacket(packet, this@ProviderWiz)?.also fromPacket@{ device ->
                                if (devices.containsKey(device.macAddress)) {
                                    return@fromPacket
                                }
                                devices[device.macAddress] = (device)
                                withContext(Dispatchers.Main) {
                                    onDeviceFoundListener(devices.values.toList())
                                }
                            }
                        }
                    }
                    Arrays.fill(buffer, 0)
                }
            }
        }
        sendData(
            """
            {"method":"getPilot","params":{}}
        """.trimIndent().toByteArray(),
            repeatCount = 3
        )
    }

    private fun sendData(
        data: ByteArray,
        ipAddress: String = "255.255.255.255",
        repeatCount: Int = 1
    ) {
        coroutineScope.launch(Dispatchers.IO) {
            repeat(repeatCount) {
                socket.send(
                    DatagramPacket(
                        data, data.size,
                        InetAddress.getByName(ipAddress), 38899
                    )
                )
                delay(100)
            }
        }
    }
}