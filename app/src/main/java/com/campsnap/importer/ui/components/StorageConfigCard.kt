package com.campsnap.importer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campsnap.importer.ui.theme.CampSnapAmber
import com.campsnap.importer.ui.theme.CampSnapTeal

@Composable
fun StorageConfigCard(
    isConfigured: Boolean,
    configuredPath: String?,
    destinationAlbum: String,
    onSelectFolderClick: () -> Unit,
    onChangeAlbumClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "STORAGE & ROUTING",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Camera Source Root
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CampSnapAmber.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isConfigured) AppIcons.Folder else AppIcons.FolderOpen,
                            contentDescription = null,
                            tint = CampSnapAmber,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Camera Storage",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (isConfigured) (configuredPath ?: "Configured Root") else "Not Selected",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                if (!isConfigured) {
                    Button(
                        onClick = onSelectFolderClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CampSnapAmber
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Select")
                    }
                } else {
                    OutlinedButton(
                        onClick = onSelectFolderClick,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Change")
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Destination Album
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CampSnapTeal.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = AppIcons.PhotoAlbum,
                            contentDescription = null,
                            tint = CampSnapTeal,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Destination Native Album",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Pictures/$destinationAlbum",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                IconButton(onClick = onChangeAlbumClick) {
                    Icon(
                        imageVector = AppIcons.Edit,
                        contentDescription = "Edit destination album name",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
