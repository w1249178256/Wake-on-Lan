package com.feng.wakeonlan

import android.content.Context
import android.net.wifi.WifiManager
import java.net.InetAddress
import java.net.UnknownHostException

object NetworkUtils {

    // 获取当前连接的Wi-Fi的IP地址
    fun getLocalIpAddress(context: Context): String? {
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val ipAddress = wifiManager.connectionInfo.ipAddress

        // 将IP地址转换为字符串格式
        return if (ipAddress != 0) {
            InetAddress.getByAddress(
                byteArrayOf(
                    (ipAddress and 0xff).toByte(),
                    (ipAddress shr 8 and 0xff).toByte(),
                    (ipAddress shr 16 and 0xff).toByte(),
                    (ipAddress shr 24 and 0xff).toByte()
                )
            ).hostAddress
        } else {
            null
        }
    }

    // 获取子网掩码
    fun getSubnetMask(context: Context): String? {
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val dhcpInfo = wifiManager.dhcpInfo
        val netmask = dhcpInfo.netmask

        // 将子网掩码转换为字符串格式
        return if (netmask != 0) {
            InetAddress.getByAddress(
                byteArrayOf(
                    (netmask and 0xff).toByte(),
                    (netmask shr 8 and 0xff).toByte(),
                    (netmask shr 16 and 0xff).toByte(),
                    (netmask shr 24 and 0xff).toByte()
                )
            ).hostAddress
        } else {
            null
        }
    }

    // 根据IP地址和子网掩码计算起始和结束IP
    fun calculateSubnetRange(ipAddress: String, subnetMask: String): Pair<String, String>? {
        try {
            val ip = ipToLong(InetAddress.getByName(ipAddress))
            val mask = ipToLong(InetAddress.getByName(subnetMask))

            val network = ip and mask
            val broadcast = network or mask.inv() // 取反获取广播地址

            return longToIp(network) to longToIp(broadcast)
        } catch (e: UnknownHostException) {
            e.printStackTrace()
        }
        return null
    }

    // 将IP地址转换为Long类型
    fun ipToLong(ip: InetAddress): Long {
        return ip.address.fold(0L) { acc, byte -> (acc shl 8) or (byte.toLong() and 0xff) }
    }

    // 将Long类型转换为IP地址字符串
    fun longToIp(ip: Long): String {
        return "${(ip shr 24) and 0xff}.${(ip shr 16) and 0xff}.${(ip shr 8) and 0xff}.${ip and 0xff}"
    }
}