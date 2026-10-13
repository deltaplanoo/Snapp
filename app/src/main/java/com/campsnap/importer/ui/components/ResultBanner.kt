package com.campsnap.importer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.campsnap.importer.ui.theme.CampSnapAmber
import com.campsnap.importer.ui.theme.StatusError
import com.campsnap.importer.ui.theme.StatusSuccess
import com.campsnap.importer.ui.theme.StatusWarning

@Composable
fun ResultDialog(
    isSuccess: Boolean,
    title: String,
    message: String,
    warning: String? = null,
    onDismiss: () -> Unit
) {
    val statusColor = if (isSuccess) StatusSuccess else StatusError

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = if (isSuccess) AppIcons.CheckCircle else AppIcons.Error,
                contentDescription = null,
                tint = statusColor,
                modifier = Modifier.size(36.dp)
            )
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge
                )
                if (!warning.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(StatusWarning.copy(alpha = 0.15f))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = AppIcons.Warning,
                            contentDescription = null,
                            tint = StatusWarning,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = warning,
                            style = MaterialTheme.typography.bodySmall,
                            color = StatusWarning
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSuccess) StatusSuccess else CampSnapAmber
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Done")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}
