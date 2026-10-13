package com.campsnap.importer.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Monitors USB hardware attachments and detachments using Android UsbManager.
 * Emits real-time connection status to update UI badges and auto-trigger scans.
 */
class UsbDeviceMonitor(
    private val context: Context,
    private val scope: CoroutineScope
) {
    private val usbManager = context.getSystemService(Context.USB_SERVICE) as? UsbManager

    private val _isDeviceConnected = MutableStateFlow(checkInitialDeviceConnected())
    val isDeviceConnected: StateFlow<Boolean> = _isDeviceConnected.asStateFlow()

    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                    val device = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(UsbManager.EXTRA_DEVICE, UsbDevice::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                    }
                    _isDeviceConnected.value = isLikelyCameraOrStorage(device) || hasAnyUsbDevice()
                }
                UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                    _isDeviceConnected.value = hasAnyUsbDevice()
                }
            }
        }
    }

    init {
        val filter = IntentFilter().apply {
            addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
            addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(usbReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            context.registerReceiver(usbReceiver, filter)
        }
    }

    fun refreshStatus() {
        _isDeviceConnected.value = hasAnyUsbDevice()
    }

    private fun checkInitialDeviceConnected(): Boolean {
        return hasAnyUsbDevice()
    }

    private fun hasAnyUsbDevice(): Boolean {
        val manager = usbManager ?: return false
        val devices = manager.deviceList.values
        if (devices.isEmpty()) return false

        // Check if any connected USB device matches Mass Storage or has USB peripherals
        return devices.any { isLikelyCameraOrStorage(it) } || devices.isNotEmpty()
    }

    private fun isLikelyCameraOrStorage(device: UsbDevice?): Boolean {
        if (device == null) return false
        // Class 8 is Mass Storage
        if (device.deviceClass == UsbConstants.USB_CLASS_MASS_STORAGE) return true

        // Inspect interfaces
        for (i in 0 until device.interfaceCount) {
            val iface = device.getInterface(i)
            if (iface.interfaceClass == UsbConstants.USB_CLASS_MASS_STORAGE) {
                return true
            }
        }
        // Fallback: If device class is miscellaneous or vendor specific, could still be camera
        return true
    }

    fun unregister() {
        try {
            context.unregisterReceiver(usbReceiver)
        } catch (e: Exception) {
            // Receiver might not be registered or already unregistered
        }
    }
}
