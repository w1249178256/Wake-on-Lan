package com.feng.wakeonlan

object MacVendorDatabase {

    private val macVendors = mapOf(
        "00-14-22" to "Dell",
        "00-1A-2B" to "Cisco",
        // 继续添加更多的 MAC 厂商映射
    )

    fun getVendor(macPrefix: String): String? {
        return macVendors[macPrefix]
    }
}