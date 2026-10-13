package com.campsnap.importer.model

/**
 * State representation for the Camp Snap Importer UI.
 */
sealed interface ImportUiState {
    /**
     * Initial state when no camera folder has been selected yet.
     */
    data class Unconfigured(
        val isUsbConnected: Boolean = false,
        val destinationAlbum: String = "Camp Snap"
    ) : ImportUiState

    /**
     * Camera folder is configured and app is ready to import or idle.
     */
    data class Idle(
        val isUsbConnected: Boolean,
        val configuredUriString: String,
        val photosCount: Int,
        val totalSizeBytes: Long,
        val destinationAlbum: String,
        val isScanning: Boolean = false
    ) : ImportUiState

    /**
     * Photos are actively being scanned.
     */
    data class Scanning(
        val isUsbConnected: Boolean,
        val configuredUriString: String,
        val destinationAlbum: String
    ) : ImportUiState

    /**
     * Photo transfer and verification in progress.
     */
    data class Importing(
        val current: Int,
        val total: Int,
        val currentFileName: String,
        val percent: Int,
        val phase: ImportPhase,
        val destinationAlbum: String
    ) : ImportUiState

    /**
     * Successfully transferred and verified batch.
     */
    data class Success(
        val importedCount: Int,
        val clearedFromCamera: Boolean,
        val destinationAlbum: String,
        val warningMessage: String? = null
    ) : ImportUiState

    /**
     * An error occurred during scanning, permission handling, or transfer.
     */
    data class Error(
        val message: String,
        val canRetry: Boolean = true
    ) : ImportUiState
}
