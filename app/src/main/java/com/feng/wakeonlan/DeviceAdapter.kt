package com.feng.wakeonlan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DeviceAdapter : RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {
    private val devices = mutableListOf<DeviceInfo>()

    fun updateDevices(newDevices: List<DeviceInfo>) {
        devices.clear()
        devices.addAll(newDevices)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_device, parent, false)
        return DeviceViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val device = devices[position]
        holder.bind(device)
    }

    override fun getItemCount(): Int = devices.size

    class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ipTextView: TextView = itemView.findViewById(R.id.ipTextView)
        private val macTextView: TextView = itemView.findViewById(R.id.macTextView)
        private val vendorTextView: TextView = itemView.findViewById(R.id.vendorTextView)

        fun bind(device: DeviceInfo) {
            ipTextView.text = device.ip
            macTextView.text = device.macAddress
            vendorTextView.text = device.vendor
        }
    }
}