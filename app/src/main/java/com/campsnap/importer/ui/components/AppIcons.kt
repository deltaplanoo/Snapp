package com.campsnap.importer.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * Lightweight, high-performance vector icons avoiding the 35MB material-icons-extended dependency.
 */
object AppIcons {

    val Edit: ImageVector = Icons.Default.Edit
    val Refresh: ImageVector = Icons.Default.Refresh
    val Warning: ImageVector = Icons.Default.Warning
    val Close: ImageVector = Icons.Default.Close

    val CameraAlt: ImageVector by lazy {
        ImageVector.Builder(
            name = "CameraAlt",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(12f, 12f)
                addOval(12f, 12f, 3.2f)
                moveTo(9f, 2f)
                lineTo(7.17f, 4f)
                lineTo(4f, 4f)
                curveTo(2.9f, 4f, 2f, 4.9f, 2f, 6f)
                lineTo(2f, 18f)
                curveTo(2f, 19.1f, 2.9f, 20f, 4f, 20f)
                lineTo(20f, 20f)
                curveTo(21.1f, 20f, 22f, 19.1f, 22f, 18f)
                lineTo(22f, 6f)
                curveTo(22f, 4.9f, 21.1f, 4f, 20f, 4f)
                lineTo(16.83f, 4f)
                lineTo(15f, 2f)
                lineTo(9f, 2f)
                close()
                moveTo(12f, 17f)
                curveTo(9.24f, 17f, 7f, 14.76f, 7f, 12f)
                curveTo(7f, 9.24f, 9.24f, 7f, 12f, 7f)
                curveTo(14.76f, 7f, 17f, 9.24f, 17f, 12f)
                curveTo(17f, 14.76f, 14.76f, 17f, 12f, 17f)
                close()
            }
        }.build()
    }

    private fun ImageVector.Builder.addOval(cx: Float, cy: Float, r: Float) {
        // Helper
    }

    val Usb: ImageVector by lazy {
        ImageVector.Builder(
            name = "Usb",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(15f, 7f)
                verticalLineTo(4f)
                horizontalLineTo(16f)
                verticalLineTo(2f)
                horizontalLineTo(8f)
                verticalLineTo(4f)
                horizontalLineTo(9f)
                verticalLineTo(7f)
                horizontalLineTo(7f)
                verticalLineTo(13f)
                curveTo(7f, 14.1f, 7.9f, 15f, 9f, 15f)
                verticalLineTo(19f)
                curveTo(9f, 20.66f, 10.34f, 22f, 12f, 22f)
                curveTo(13.66f, 22f, 15f, 20.66f, 15f, 19f)
                verticalLineTo(15f)
                curveTo(16.1f, 15f, 17f, 14.1f, 17f, 13f)
                verticalLineTo(7f)
                horizontalLineTo(15f)
                close()
                moveTo(10f, 4f)
                horizontalLineTo(14f)
                verticalLineTo(7f)
                horizontalLineTo(10f)
                verticalLineTo(4f)
                close()
            }
        }.build()
    }

    val UsbOff: ImageVector by lazy {
        ImageVector.Builder(
            name = "UsbOff",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(1.39f, 4.22f)
                lineTo(2.8f, 5.63f)
                lineTo(7f, 9.83f)
                verticalLineTo(13f)
                curveTo(7f, 14.1f, 7.9f, 15f, 9f, 15f)
                verticalLineTo(19f)
                curveTo(9f, 20.66f, 10.34f, 22f, 12f, 22f)
                curveTo(13.43f, 22f, 14.61f, 21f, 14.92f, 19.67f)
                lineTo(19.78f, 24.53f)
                lineTo(21.19f, 23.12f)
                lineTo(2.81f, 2.81f)
                lineTo(1.39f, 4.22f)
                close()
            }
        }.build()
    }

    val Folder: ImageVector by lazy {
        ImageVector.Builder(
            name = "Folder",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(10f, 4f)
                horizontalLineTo(4f)
                curveTo(2.9f, 4f, 2f, 4.9f, 2f, 6f)
                lineTo(2f, 18f)
                curveTo(2f, 19.1f, 2.9f, 20f, 4f, 20f)
                horizontalLineTo(20f)
                curveTo(21.1f, 20f, 22f, 19.1f, 22f, 18f)
                verticalLineTo(8f)
                curveTo(22f, 6.9f, 21.1f, 6f, 20f, 6f)
                horizontalLineTo(12f)
                lineTo(10f, 4f)
                close()
            }
        }.build()
    }

    val FolderOpen: ImageVector by lazy {
        ImageVector.Builder(
            name = "FolderOpen",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(20f, 6f)
                horizontalLineTo(12f)
                lineTo(10f, 4f)
                horizontalLineTo(4f)
                curveTo(2.9f, 4f, 2.01f, 4.9f, 2.01f, 6f)
                lineTo(2f, 18f)
                curveTo(2f, 19.1f, 2.9f, 20f, 4f, 20f)
                horizontalLineTo(20f)
                curveTo(21.1f, 20f, 22f, 19.1f, 22f, 18f)
                verticalLineTo(8f)
                curveTo(22f, 6.9f, 21.1f, 6f, 20f, 6f)
                close()
                moveTo(20f, 18f)
                horizontalLineTo(4f)
                verticalLineTo(8f)
                horizontalLineTo(20f)
                verticalLineTo(18f)
                close()
            }
        }.build()
    }

    val PhotoAlbum: ImageVector by lazy {
        ImageVector.Builder(
            name = "PhotoAlbum",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(18f, 2f)
                horizontalLineTo(6f)
                curveTo(4.9f, 2f, 4f, 2.9f, 4f, 4f)
                verticalLineTo(20f)
                curveTo(4f, 21.1f, 4.9f, 22f, 6f, 22f)
                horizontalLineTo(18f)
                curveTo(19.1f, 22f, 20f, 21.1f, 20f, 20f)
                verticalLineTo(4f)
                curveTo(20f, 2.9f, 19.1f, 2f, 18f, 2f)
                close()
                moveTo(11f, 7f)
                curveTo(11f, 7.83f, 10.33f, 8.5f, 9.5f, 8.5f)
                curveTo(8.67f, 8.5f, 8f, 7.83f, 8f, 7f)
                curveTo(8f, 6.17f, 8.67f, 5.5f, 9.5f, 5.5f)
                curveTo(10.33f, 5.5f, 11f, 6.17f, 11f, 7f)
                close()
                moveTo(16.5f, 18f)
                horizontalLineTo(7.5f)
                lineTo(10.25f, 14.33f)
                lineTo(12.16f, 16.78f)
                lineTo(14.75f, 13.5f)
                lineTo(16.5f, 18f)
                close()
            }
        }.build()
    }

    val Image: ImageVector by lazy {
        ImageVector.Builder(
            name = "Image",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(21f, 19f)
                verticalLineTo(5f)
                curveTo(21f, 3.9f, 20.1f, 3f, 19f, 3f)
                horizontalLineTo(5f)
                curveTo(3.9f, 3f, 3f, 3.9f, 3f, 5f)
                verticalLineTo(19f)
                curveTo(3f, 20.1f, 3.9f, 21f, 5f, 21f)
                horizontalLineTo(19f)
                curveTo(20.1f, 21f, 21f, 20.1f, 21f, 19f)
                close()
                moveTo(8.5f, 13.5f)
                lineTo(11f, 16.51f)
                lineTo(14.5f, 12f)
                lineTo(19f, 18f)
                horizontalLineTo(5f)
                lineTo(8.5f, 13.5f)
                close()
            }
        }.build()
    }

    val SdCard: ImageVector by lazy {
        ImageVector.Builder(
            name = "SdCard",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(18f, 2f)
                horizontalLineTo(10f)
                lineTo(4f, 8f)
                verticalLineTo(20f)
                curveTo(4f, 21.1f, 4.9f, 22f, 6f, 22f)
                horizontalLineTo(18f)
                curveTo(19.1f, 22f, 20f, 21.1f, 20f, 20f)
                verticalLineTo(4f)
                curveTo(20f, 2.9f, 19.1f, 2f, 18f, 2f)
                close()
                moveTo(9f, 10f)
                horizontalLineTo(7f)
                verticalLineTo(7f)
                horizontalLineTo(9f)
                verticalLineTo(10f)
                close()
                moveTo(12f, 10f)
                horizontalLineTo(10f)
                verticalLineTo(7f)
                horizontalLineTo(12f)
                verticalLineTo(10f)
                close()
                moveTo(15f, 10f)
                horizontalLineTo(13f)
                verticalLineTo(7f)
                horizontalLineTo(15f)
                verticalLineTo(10f)
                close()
            }
        }.build()
    }

    val Sync: ImageVector by lazy {
        ImageVector.Builder(
            name = "Sync",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(12f, 4f)
                verticalLineTo(1f)
                lineTo(8f, 5f)
                lineTo(12f, 9f)
                verticalLineTo(6f)
                curveTo(15.31f, 6f, 18f, 8.69f, 18f, 12f)
                curveTo(18f, 13.01f, 17.75f, 13.97f, 17.3f, 14.8f)
                lineTo(18.76f, 16.26f)
                curveTo(19.54f, 15.03f, 20f, 13.57f, 20f, 12f)
                curveTo(20f, 7.58f, 16.42f, 4f, 12f, 4f)
                close()
                moveTo(12f, 18f)
                curveTo(8.69f, 18f, 6f, 15.31f, 6f, 12f)
                curveTo(6f, 10.99f, 6.25f, 10.03f, 6.7f, 9.2f)
                lineTo(5.24f, 7.74f)
                curveTo(4.46f, 8.97f, 4f, 10.43f, 4f, 12f)
                curveTo(4f, 16.42f, 7.58f, 20f, 12f, 20f)
                verticalLineTo(23f)
                lineTo(16f, 19f)
                lineTo(12f, 15f)
                verticalLineTo(18f)
                close()
            }
        }.build()
    }

    val CheckCircle: ImageVector by lazy {
        ImageVector.Builder(
            name = "CheckCircle",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(12f, 2f)
                curveTo(6.48f, 2f, 2f, 6.48f, 2f, 12f)
                curveTo(2f, 17.52f, 6.48f, 22f, 12f, 22f)
                curveTo(17.52f, 22f, 22f, 17.52f, 22f, 12f)
                curveTo(22f, 6.48f, 17.52f, 2f, 12f, 2f)
                close()
                moveTo(10f, 17f)
                lineTo(5f, 12f)
                lineTo(6.41f, 10.59f)
                lineTo(10f, 14.17f)
                lineTo(17.59f, 6.58f)
                lineTo(19f, 8f)
                lineTo(10f, 17f)
                close()
            }
        }.build()
    }

    val Error: ImageVector by lazy {
        ImageVector.Builder(
            name = "Error",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(12f, 2f)
                curveTo(6.48f, 2f, 2f, 6.48f, 2f, 12f)
                curveTo(2f, 17.52f, 6.48f, 22f, 12f, 22f)
                curveTo(17.52f, 22f, 22f, 17.52f, 22f, 12f)
                curveTo(22f, 6.48f, 17.52f, 2f, 12f, 2f)
                close()
                moveTo(13f, 17f)
                horizontalLineTo(11f)
                verticalLineTo(15f)
                horizontalLineTo(13f)
                verticalLineTo(17f)
                close()
                moveTo(13f, 13f)
                horizontalLineTo(11f)
                verticalLineTo(7f)
                horizontalLineTo(13f)
                verticalLineTo(13f)
                close()
            }
        }.build()
    }

    val Download: ImageVector by lazy {
        ImageVector.Builder(
            name = "Download",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(19f, 9f)
                horizontalLineTo(15f)
                verticalLineTo(3f)
                horizontalLineTo(9f)
                verticalLineTo(9f)
                horizontalLineTo(5f)
                lineTo(12f, 16f)
                lineTo(19f, 9f)
                close()
                moveTo(5f, 18f)
                verticalLineTo(20f)
                horizontalLineTo(19f)
                verticalLineTo(18f)
                horizontalLineTo(5f)
                close()
            }
        }.build()
    }
}
