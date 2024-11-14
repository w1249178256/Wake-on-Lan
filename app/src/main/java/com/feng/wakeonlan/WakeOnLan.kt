package com.feng.wakeonlan

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

object WakeOnLan {
    private const val PORT = 9
    fun send(macAddress: ByteArray): Boolean {
        return try {
            val bytes = ByteArray(6 + 16 * macAddress.size)
            for (i in 0..5) {
                bytes[i] = 0xff.toByte()
            }
            for (i in 1..16) {
                System.arraycopy(macAddress, 0, bytes, i * 6, macAddress.size)
            }
            val address = InetAddress.getByName("255.255.255.255")
            val packet = DatagramPacket(bytes, bytes.size, address, PORT)
            DatagramSocket().use { socket ->
                socket.send(packet)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

}
