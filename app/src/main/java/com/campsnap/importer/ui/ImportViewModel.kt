package com.campsnap.importer.ui

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.campsnap.importer.CampSnapApplication
import com.campsnap.importer.data.CampSnapImporterEngine
import com.campsnap.importer.data.PreferencesManager
import com.campsnap.importer.data.UsbDeviceMonitor
import com.campsnap.importer.model.CameraPhoto
import com.campsnap.importer.model.ImportPhase
import com.campsnap.importer.model.ImportUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ViewModel managing the photo import lifecycle, SAF tree URI persistence,
 * USB connection detection, and real-time import progress.
 */
class ImportViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val app = application as CampSnapApplication
    private val preferencesManager: PreferencesManager = app.preferencesManager
    private val importer: CampSnapImporterEngine = app.importerEngine
    private val usbMonitor: UsbDeviceMonitor = app.usbDeviceMonitor

    private val _uiState = MutableStateFlow<ImportUiState>(ImportUiState.Unconfigured())
    val uiState: StateFlow<ImportUiState> = _uiState.asStateFlow()

    private var currentPhotos: List<CameraPhoto> = emptyList()
    private var scanJob: Job? = null
    private var importJob: Job? = null

    init {
        // Observe tree URI and USB connection updates
        viewModelScope.launch {
            combine(
                preferencesManager.treeUriFlow,
                preferencesManager.destinationAlbumFlow,
                usbMonitor.isDeviceConnected
            ) { treeUri, destinationAlbum, isUsbConnected ->
                Triple(treeUri, destinationAlbum, isUsbConnected)
            }.collect { (treeUri, destinationAlbum, isUsbConnected) ->
                if (treeUri == null) {
                    _uiState.value = ImportUiState.Unconfigured(
                        isUsbConnected = isUsbConnected,
                        destinationAlbum = destinationAlbum
                    )
                } else {
                    // Tree URI exists, trigger scan if not currently importing
                    val currentState = _uiState.value
                    if (currentState !is ImportUiState.Importing) {
                        scanCameraInternal(treeUri, destinationAlbum, isUsbConnected)
                    }
                }
            }
        }
    }

    /**
     * Handles user selection of the camera folder via SAF OpenDocumentTree.
     */
    fun onTreeUriSelected(uri: Uri) {
        viewModelScope.launch {
            try {
                // Persist read/write permissions for future sessions
                val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                app.contentResolver.takePersistableUriPermission(uri, takeFlags)

                // Save in DataStore
                preferencesManager.saveTreeUri(uri)

                val album = preferencesManager.destinationAlbumFlow.first()
                val isUsb = usbMonitor.isDeviceConnected.value
                scanCameraInternal(uri, album, isUsb)
            } catch (e: Exception) {
                _uiState.value = ImportUiState.Error(
                    message = "Could not persist permissions for selected directory: ${e.localizedMessage ?: e.message}"
                )
            }
        }
    }

    /**
     * Manually triggers a scan of the camera.
     */
    fun scanCamera() {
        viewModelScope.launch {
            val treeUri = preferencesManager.treeUriFlow.first()
            val album = preferencesManager.destinationAlbumFlow.first()
            val isUsb = usbMonitor.isDeviceConnected.value

            if (treeUri == null) {
                _uiState.value = ImportUiState.Unconfigured(
                    isUsbConnected = isUsb,
                    destinationAlbum = album
                )
            } else {
                scanCameraInternal(treeUri, album, isUsb)
            }
        }
    }

    private fun scanCameraInternal(treeUri: Uri, destinationAlbum: String, isUsbConnected: Boolean) {
        scanJob?.cancel()
        scanJob = viewModelScope.launch {
            _uiState.value = ImportUiState.Scanning(
                isUsbConnected = isUsbConnected,
                configuredUriString = treeUri.lastPathSegment ?: treeUri.toString(),
                destinationAlbum = destinationAlbum
            )

            try {
                val photos = importer.scanCameraPhotos(treeUri)
                currentPhotos = photos
                val totalBytes = photos.sumOf { it.sizeBytes }

                _uiState.value = ImportUiState.Idle(
                    isUsbConnected = isUsbConnected,
                    configuredUriString = treeUri.lastPathSegment ?: treeUri.toString(),
                    photosCount = photos.size,
                    totalSizeBytes = totalBytes,
                    destinationAlbum = destinationAlbum,
                    isScanning = false
                )
            } catch (e: Exception) {
                _uiState.value = ImportUiState.Error(
                    message = "Failed to scan camera directory: ${e.localizedMessage ?: e.message}"
                )
            }
        }
    }

    /**
     * Starts the two-phase transfer: Copy & Verify -> Safe Prune.
     */
    fun startImport(clearCameraAfter: Boolean = true) {
        if (_uiState.value is ImportUiState.Importing) return

        importJob?.cancel()
        importJob = viewModelScope.launch {
            val treeUri = preferencesManager.treeUriFlow.first()
            val destinationAlbum = preferencesManager.destinationAlbumFlow.first()

            if (treeUri == null) {
                _uiState.value = ImportUiState.Error("Camera folder is not configured.")
                return@launch
            }

            _uiState.value = ImportUiState.Importing(
                current = 0,
                total = currentPhotos.size,
                currentFileName = "Preparing transfer...",
                percent = 0,
                phase = ImportPhase.COPY_AND_VERIFY,
                destinationAlbum = destinationAlbum
            )

            try {
                val result = importer.importAndClearPhotos(
                    treeUri = treeUri,
                    destinationAlbum = destinationAlbum,
                    clearCameraAfter = clearCameraAfter,
                    onProgress = { progress ->
                        _uiState.value = ImportUiState.Importing(
                            current = progress.currentIndex,
                            total = progress.totalCount,
                            currentFileName = progress.currentFileName,
                            percent = progress.percentage,
                            phase = progress.phase,
                            destinationAlbum = destinationAlbum
                        )
                    }
                )

                if (result.errorMessage != null && result.successfullyImported == 0) {
                    _uiState.value = ImportUiState.Error(
                        message = result.errorMessage
                    )
                } else {
                    _uiState.value = ImportUiState.Success(
                        importedCount = result.successfullyImported,
                        clearedFromCamera = result.successfullyDeleted > 0,
                        destinationAlbum = destinationAlbum,
                        warningMessage = result.errorMessage
                    )
                }
            } catch (e: Exception) {
                _uiState.value = ImportUiState.Error(
                    message = "Import interrupted: ${e.localizedMessage ?: e.message}"
                )
            }
        }
    }

    /**
     * Updates the destination album name in Preferences DataStore.
     */
    fun updateDestinationAlbum(albumName: String) {
        viewModelScope.launch {
            preferencesManager.saveDestinationAlbum(albumName)
        }
    }

    /**
     * Clears persisted tree URI to allow selecting a different drive/folder.
     */
    fun clearConfiguredFolder() {
        viewModelScope.launch {
            preferencesManager.clearTreeUri()
            currentPhotos = emptyList()
            _uiState.value = ImportUiState.Unconfigured(
                isUsbConnected = usbMonitor.isDeviceConnected.value,
                destinationAlbum = preferencesManager.destinationAlbumFlow.first()
            )
        }
    }

    /**
     * Resets state back to Idle and triggers a fresh scan.
     */
    fun dismissResult() {
        scanCamera()
    }
}
