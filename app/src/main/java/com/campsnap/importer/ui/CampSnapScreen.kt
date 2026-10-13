package com.campsnap.importer.ui

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.campsnap.importer.model.ImportUiState
import com.campsnap.importer.ui.components.AlbumSettingsDialog
import com.campsnap.importer.ui.components.AppIcons
import com.campsnap.importer.ui.components.FileSummaryCard
import com.campsnap.importer.ui.components.HeaderSection
import com.campsnap.importer.ui.components.ImportProgressBar
import com.campsnap.importer.ui.components.ResultDialog
import com.campsnap.importer.ui.components.StorageConfigCard
import com.campsnap.importer.ui.theme.CampSnapAmber
import com.campsnap.importer.ui.theme.StatusSuccess

@Composable
fun CampSnapScreen(
    viewModel: ImportViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showAlbumDialog by remember { mutableStateOf(false) }

    // Launcher for Storage Access Framework OpenDocumentTree
    val treeLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.onTreeUriSelected(uri)
        }
    }

    // Permission launcher for media / storage access
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.all { it }
        if (granted) {
            viewModel.scanCamera()
        }
    }

    // Request permissions on first launch
    LaunchedEffect(Unit) {
        val permissionsToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        permissionLauncher.launch(permissionsToRequest)
    }

    val isImporting = uiState is ImportUiState.Importing
    val isScanning = uiState is ImportUiState.Scanning

    val isUsbConnected = when (val state = uiState) {
        is ImportUiState.Unconfigured -> state.isUsbConnected
        is ImportUiState.Idle -> state.isUsbConnected
        is ImportUiState.Scanning -> state.isUsbConnected
        else -> false
    }

    val configuredPath = when (val state = uiState) {
        is ImportUiState.Idle -> state.configuredUriString
        is ImportUiState.Scanning -> state.configuredUriString
        else -> null
    }

    val currentAlbum = when (val state = uiState) {
        is ImportUiState.Unconfigured -> state.destinationAlbum
        is ImportUiState.Idle -> state.destinationAlbum
        is ImportUiState.Scanning -> state.destinationAlbum
        is ImportUiState.Importing -> state.destinationAlbum
        is ImportUiState.Success -> state.destinationAlbum
        else -> "Camp Snap"
    }

    val photosCount = when (val state = uiState) {
        is ImportUiState.Idle -> state.photosCount
        else -> 0
    }

    val totalBytes = when (val state = uiState) {
        is ImportUiState.Idle -> state.totalSizeBytes
        else -> 0L
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with title and USB connection badge
            HeaderSection(
                isUsbConnected = isUsbConnected,
                onStatusClick = { viewModel.scanCamera() }
            )

            // USB Ready Banner when plugged in
            AnimatedVisibility(
                visible = isUsbConnected && uiState is ImportUiState.Idle && photosCount > 0,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(StatusSuccess.copy(alpha = 0.12f))
                        .border(1.dp, StatusSuccess.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = AppIcons.Usb,
                            contentDescription = null,
                            tint = StatusSuccess,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Camp Snap camera connected! $photosCount new photos ready to transfer.",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = StatusSuccess
                        )
                    }
                }
            }

            // Storage Config Card
            StorageConfigCard(
                isConfigured = configuredPath != null,
                configuredPath = configuredPath,
                destinationAlbum = currentAlbum,
                onSelectFolderClick = { treeLauncher.launch(null) },
                onChangeAlbumClick = { showAlbumDialog = true }
            )

            // Camera Contents / Photo Summary Card
            FileSummaryCard(
                photosCount = photosCount,
                totalSizeBytes = totalBytes,
                isScanning = isScanning,
                onRefreshClick = { viewModel.scanCamera() }
            )

            // Real-time Import Progress Bar
            if (uiState is ImportUiState.Importing) {
                val importingState = uiState as ImportUiState.Importing
                ImportProgressBar(
                    current = importingState.current,
                    total = importingState.total,
                    currentFileName = importingState.currentFileName,
                    percent = importingState.percent,
                    phase = importingState.phase
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Primary Action Buttons
            if (uiState is ImportUiState.Unconfigured) {
                Button(
                    onClick = { treeLauncher.launch(null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CampSnapAmber),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = AppIcons.FolderOpen,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Select Camp Snap Camera Root",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Big One-Click "Import & Clear Camera" Button
                    Button(
                        onClick = { viewModel.startImport(clearCameraAfter = true) },
                        enabled = photosCount > 0 && !isImporting && !isScanning,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CampSnapAmber,
                            disabledContainerColor = CampSnapAmber.copy(alpha = 0.35f)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = AppIcons.Download,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isImporting) "Importing In Progress…" else "Import & Clear Camera",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Secondary "Import Only (Keep on Camera)" Button
                    OutlinedButton(
                        onClick = { viewModel.startImport(clearCameraAfter = false) },
                        enabled = photosCount > 0 && !isImporting && !isScanning,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Import Only (Keep on Camera)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Destination Album Dialog
        if (showAlbumDialog) {
            AlbumSettingsDialog(
                currentAlbumName = currentAlbum,
                onDismiss = { showAlbumDialog = false },
                onConfirm = { newAlbum ->
                    viewModel.updateDestinationAlbum(newAlbum)
                    showAlbumDialog = false
                }
            )
        }

        // Success Dialog
        if (uiState is ImportUiState.Success) {
            val successState = uiState as ImportUiState.Success
            ResultDialog(
                isSuccess = true,
                title = "Import Complete!",
                message = if (successState.clearedFromCamera) {
                    "Successfully imported and verified ${successState.importedCount} photos into Pictures/${successState.destinationAlbum}. Camera storage was safely cleared."
                } else {
                    "Successfully imported and verified ${successState.importedCount} photos into Pictures/${successState.destinationAlbum}. Originals were preserved on the camera."
                },
                warning = successState.warningMessage,
                onDismiss = { viewModel.dismissResult() }
            )
        }

        // Error Dialog
        if (uiState is ImportUiState.Error) {
            val errorState = uiState as ImportUiState.Error
            ResultDialog(
                isSuccess = false,
                title = "Transfer Alert",
                message = errorState.message,
                onDismiss = { viewModel.dismissResult() }
            )
        }
    }
}
