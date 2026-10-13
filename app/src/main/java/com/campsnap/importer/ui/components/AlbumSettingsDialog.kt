package com.campsnap.importer.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.campsnap.importer.ui.theme.CampSnapAmber

@Composable
fun AlbumSettingsDialog(
    currentAlbumName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var textValue by remember { mutableStateOf(currentAlbumName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Destination Gallery Album",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column {
                Text(
                    text = "Photos will be organized in Android's native gallery under:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = textValue,
                    onValueChange = { textValue = it },
                    label = { Text("Album Name") },
                    placeholder = { Text("Camp Snap") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Path: Pictures/${textValue.trim().ifEmpty { "Camp Snap" }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(textValue.trim().ifEmpty { "Camp Snap" })
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = CampSnapAmber
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}
