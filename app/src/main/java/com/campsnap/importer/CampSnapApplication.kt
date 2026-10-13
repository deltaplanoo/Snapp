package com.campsnap.importer

import android.app.Application
import com.campsnap.importer.data.CampSnapImporter
import com.campsnap.importer.data.CampSnapImporterEngine
import com.campsnap.importer.data.PreferencesManager
import com.campsnap.importer.data.UsbDeviceMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class CampSnapApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    lateinit var preferencesManager: PreferencesManager
        private set

    lateinit var usbDeviceMonitor: UsbDeviceMonitor
        private set

    lateinit var importerEngine: CampSnapImporterEngine
        private set

    override fun onCreate() {
        super.onCreate()
        preferencesManager = PreferencesManager(this)
        usbDeviceMonitor = UsbDeviceMonitor(this, applicationScope)
        importerEngine = CampSnapImporter(this)
    }
}
