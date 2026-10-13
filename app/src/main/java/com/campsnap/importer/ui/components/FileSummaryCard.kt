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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import java.util.Locale

@Composable
fun FileSummaryCard(
    photosCount: Int,
    totalSizeBytes: Long,
    isScanning: Boolean,
    onRefreshClick: () -> Unit
) {
    val totalSizeMb = totalSizeBytes / (1024.0 * 1024.0)
    val formattedSize = if (totalSizeMb >= 1024.0) {
        String.format(Locale.US, "%.2f GB", totalSizeMb / 1024.0)
    } else {
        String.format(Locale.US, "%.1f MB", totalSizeMb)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CAMERA CONTENTS",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )

                if (isScanning) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = CampSnapAmber
                    )
                } else {
                    IconButton(
                        onClick = onRefreshClick,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = AppIcons.Refresh,
                            contentDescription = "Refresh camera contents",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Photos count stat
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = AppIcons.Image,
                            contentDescription = null,
                            tint = CampSnapAmber,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$photosCount",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = if (photosCount == 1) "Photo Found" else "Photos Found",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Divider line
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(44.dp)
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                )

                // Total size stat
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = AppIcons.SdCard,
                            contentDescription = null,
                            tint = CampSnapTeal,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = formattedSize,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Total Size",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
