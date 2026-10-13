package com.campsnap.importer

import android.content.Intent
import android.hardware.usb.UsbManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.campsnap.importer.ui.CampSnapScreen
import com.campsnap.importer.ui.ImportViewModel
import com.campsnap.importer.ui.theme.CampSnapImporterTheme

class MainActivity : ComponentActivity() {

    private val viewModel: ImportViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Handle direct launch via USB attachment
        handleUsbIntent(intent)

        setContent {
            CampSnapImporterTheme {
                CampSnapScreen(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleUsbIntent(intent)
    }

    private fun handleUsbIntent(intent: Intent?) {
        if (intent?.action == UsbManager.ACTION_USB_DEVICE_ATTACHED) {
            // Camera plugged in, refresh USB monitor and scan camera
            (application as? CampSnapApplication)?.usbDeviceMonitor?.refreshStatus()
            viewModel.scanCamera()
        }
    }
}
