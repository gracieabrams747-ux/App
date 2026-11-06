package com.example.util

import android.app.WallpaperManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

enum class WallpaperTarget {
    HOME,
    LOCK,
    BOTH
}

object WallpaperHelper {

    suspend fun setWallpaperFromResource(
        context: Context,
        resourceId: Int,
        target: WallpaperTarget
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val wallpaperManager = WallpaperManager.getInstance(context)
            val bitmap = BitmapFactory.decodeResource(context.resources, resourceId)
                ?: throw IllegalStateException("No se pudo decodificar el fondo de pantalla")

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    val flags = when (target) {
                        WallpaperTarget.HOME -> WallpaperManager.FLAG_SYSTEM
                        WallpaperTarget.LOCK -> WallpaperManager.FLAG_LOCK
                        WallpaperTarget.BOTH -> WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
                    }
                    try {
                        wallpaperManager.setBitmap(bitmap, null, true, flags)
                    } catch (e: Exception) {
                        // Fallback to setting for all screens if specific screen flag fails
                        wallpaperManager.setBitmap(bitmap)
                    }
                } else {
                    wallpaperManager.setBitmap(bitmap)
                }
            } finally {
                // Keep bitmap management clean
            }
            Unit
        }
    }

    suspend fun saveWallpaperToGallery(
        context: Context,
        resourceId: Int,
        title: String
    ): Result<Uri> = withContext(Dispatchers.IO) {
        runCatching {
            val bitmap = BitmapFactory.decodeResource(context.resources, resourceId)
                ?: throw IllegalStateException("No se pudo leer la imagen")

            val filename = "VirgenMaria_${title.replace(" ", "_")}_${System.currentTimeMillis()}.jpg"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/VirgenMaria")
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }

                val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    ?: throw IllegalStateException("No se pudo crear el archivo en la galería")

                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)
                }

                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                context.contentResolver.update(uri, contentValues, null, null)

                uri
            } else {
                val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val marianFolder = File(picturesDir, "VirgenMaria").apply { mkdirs() }
                val imageFile = File(marianFolder, filename)

                FileOutputStream(imageFile).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
                }

                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DATA, imageFile.absolutePath)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                }
                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                    ?: Uri.fromFile(imageFile)
            }
        }
    }

    suspend fun shareWallpaper(
        context: Context,
        resourceId: Int,
        title: String
    ): Result<Intent> = withContext(Dispatchers.IO) {
        runCatching {
            val bitmap = BitmapFactory.decodeResource(context.resources, resourceId)
                ?: throw IllegalStateException("No se pudo leer la imagen para compartir")

            val cachePath = File(context.cacheDir, "shared_wallpapers").apply { mkdirs() }
            val file = File(cachePath, "VirgenMaria_${title.replace(" ", "_")}.jpg")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
            }

            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            Intent(Intent.ACTION_SEND).apply {
                type = "image/jpeg"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_SUBJECT, title)
                putExtra(Intent.EXTRA_TEXT, "Fondo sagrado de la Virgen María: $title")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        }
    }
}
