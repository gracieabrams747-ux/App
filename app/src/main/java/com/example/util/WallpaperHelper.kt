package com.example.util

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

            applyWallpaper(wallpaperManager, bitmap, target)
        }
    }

    private fun applyWallpaper(
        wallpaperManager: WallpaperManager,
        bitmap: Bitmap,
        target: WallpaperTarget
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val flags = when (target) {
                WallpaperTarget.HOME -> WallpaperManager.FLAG_SYSTEM
                WallpaperTarget.LOCK -> WallpaperManager.FLAG_LOCK
                WallpaperTarget.BOTH -> WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
            }
            wallpaperManager.setBitmap(bitmap, null, true, flags)
        } else {
            wallpaperManager.setBitmap(bitmap)
        }
    }
}
