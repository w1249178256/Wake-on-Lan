package com.feng.wakeonlan

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class NetworkScanActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DeviceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_network_scan)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = DeviceAdapter()
        recyclerView.adapter = adapter
        scanNetwork()
    }

    private fun scanNetwork() {
        Toast.makeText(this, "正在扫描局域网...", Toast.LENGTH_SHORT).show()
        lifecycleScope.launch {
            NetworkScanner.scan(this@NetworkScanActivity){ devices ->
                runOnUiThread {
                    adapter.updateDevices(devices)
                    Toast.makeText(
                        this@NetworkScanActivity,
                        "扫描完成，共找到 ${devices.size} 台设备",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

}