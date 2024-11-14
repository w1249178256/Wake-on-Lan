package com.feng.wakeonlan

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {

    private lateinit var macAddressEditText: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        macAddressEditText = findViewById(R.id.textInputEditText)
        val scanNetworkButton: View = findViewById(R.id.scanNetworkButton)
        scanNetworkButton.setOnClickListener {
            val intent = Intent(this, NetworkScanActivity::class.java)
            startActivity(intent)
        }
    }

    fun onSendButtonClick(view: View) {
        val inputText = macAddressEditText.text.toString().trim()

        when {
            inputText.isEmpty() -> macAddressEditText.error =
                getString(R.string.mac_address_empty_error)

            !isValidMacAddress(inputText) -> macAddressEditText.error =
                getString(R.string.mac_address_invalid_error)

            else -> {
                macAddressEditText.error = null
                val macAddress = inputText.replace(":", "").replace("-", "")
                val macAddressBytes = ByteArray(6)
                for (i in 0..5) {
                    macAddressBytes[i] =
                        Integer.parseInt(macAddress.substring(i * 2, i * 2 + 2), 16).toByte()
                }
                WakeOnLan.send(macAddressBytes)
                Toast.makeText(this, R.string.wake_on_lan_sent, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isValidMacAddress(macAddress: String): Boolean {
        val macAddressRegex = "([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})"
        return macAddress.matches(macAddressRegex.toRegex())
    }
}