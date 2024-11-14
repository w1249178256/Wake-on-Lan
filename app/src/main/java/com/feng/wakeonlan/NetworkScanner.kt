package com.feng.wakeonlan

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.util.Locale
import kotlin.math.log

object NetworkScanner {

    suspend fun scan(context: Context, callback: (List<DeviceInfo>) -> Unit) {
        val devices = mutableListOf<DeviceInfo>()

        // 获取本地 IP 地址和子网掩码
        val localIp = NetworkUtils.getLocalIpAddress(context)
        val subnetMask = NetworkUtils.getSubnetMask(context)

        if (localIp == null || subnetMask == null) {
            callback(emptyList())
            return
        }

        // 计算扫描范围
        val (startIp, endIp) = NetworkUtils.calculateSubnetRange(localIp, subnetMask) ?: run {
            callback(emptyList())
            return
        }

        // 逐一扫描 IP 地址
        withContext(Dispatchers.IO) {
            val startIpLong = NetworkUtils.ipToLong(InetAddress.getByName(startIp))
            val endIpLong = NetworkUtils.ipToLong(InetAddress.getByName(endIp))

            for (ipLong in startIpLong..endIpLong) {

                val ipAddress = NetworkUtils.longToIp(ipLong)

                try {
                    val address = InetAddress.getByName(ipAddress)
                    Log.d(TAG, "scan: $ipAddress, $address")
                    if (address.isReachable(500)) {  // 超时时间设为500ms
                        val macAddress = getMacAddress(ipAddress)  // 通过IP获取MAC地址
                        val vendor = getVendor(macAddress)  // 根据MAC地址获取厂商信息
                        devices.add(DeviceInfo(ipAddress, macAddress, vendor))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        callback(devices)
    }

    private fun getMacAddress(ip: String): String {
        return "00:00:00:00:00:00"  // 替换为实际获取 MAC 地址的方法
    }

    // 根据 MAC 地址前缀查询厂商信息
    private fun getVendor(macAddress: String): String {
        val macPrefix = macAddress.substring(0, 8).replace(":", "-").uppercase(Locale.ROOT)
        return MacVendorDatabase.getVendor(macPrefix) ?: "Unknown Vendor"
    }

    private fun isOnline(ip: String, port: Int = 80, timeout: Int = 500): Boolean {
        return try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(ip, port), timeout)
                true
            }
        } catch (e: Exception) {
            false
        }
    }

    private fun ping(ip: String): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("ping -c 1 $ip")
            val exitValue = process.waitFor()
            exitValue == 0
        } catch (e: Exception) {
            false
        }
    }

}