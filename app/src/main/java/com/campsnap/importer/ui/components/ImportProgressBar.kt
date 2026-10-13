package com.campsnap.importer.ui.components

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.campsnap.importer.model.ImportPhase
import com.campsnap.importer.ui.theme.CampSnapAmber

@Composable
fun ImportProgressBar(
    current: Int,
    total: Int,
    currentFileName: String,
    percent: Int,
    phase: ImportPhase
) {
    val progressFraction = if (total > 0) current.toFloat() / total else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progressFraction.coerceIn(0f, 1f),
        label = "progress"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, CampSnapAmber.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = AppIcons.Sync,
                        contentDescription = null,
                        tint = CampSnapAmber,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (phase) {
                            ImportPhase.SCANNING -> "Scanning Camera..."
                            ImportPhase.COPY_AND_VERIFY -> "Copying & Verifying..."
                            ImportPhase.PRUNING_CAMERA -> "Pruning Camera Storage..."
                            ImportPhase.COMPLETE -> "Transfer Finalized"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Text(
                    text = "$percent%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CampSnapAmber
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = CampSnapAmber,
                trackColor = CampSnapAmber.copy(alpha = 0.15f)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Photo $current of $total",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = currentFileName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
        }
    }
}
