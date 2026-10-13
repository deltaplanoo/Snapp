package com.campsnap.importer.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campsnap.importer.ui.theme.CampSnapAmber
import com.campsnap.importer.ui.theme.StatusSuccess

@Composable
fun HeaderSection(
    isUsbConnected: Boolean,
    onStatusClick: () -> Unit = {}
) {
    val statusColor by animateColorAsState(
        targetValue = if (isUsbConnected) StatusSuccess else Color(0xFF8E95A0),
        label = "statusColor"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CampSnapAmber.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = AppIcons.CameraAlt,
                        contentDescription = "Camp Snap Icon",
                        tint = CampSnapAmber,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Camp Snap Importer",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Automated Photo Ingestion",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Connection Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(statusColor.copy(alpha = 0.15f))
                    .border(1.dp, statusColor.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                    .clickable { onStatusClick() }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(statusColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = if (isUsbConnected) AppIcons.Usb else AppIcons.UsbOff,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isUsbConnected) "Connected" else "Not Detected",
                        color = statusColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
