// SharedProfilePhotoManager.kt
package com.example.smarparkinapp.ui.theme.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.*
import java.io.*

object SharedProfilePhotoManager {
    private var _profilePhotoBitmap by mutableStateOf<ImageBitmap?>(null)
    val profilePhotoBitmap: ImageBitmap? get() = _profilePhotoBitmap

    private const val FILENAME = "profile_photo.jpg"

    suspend fun loadProfilePhoto(context: Context) {
        withContext(Dispatchers.IO) {
            try {
                val file = File(context.filesDir, FILENAME)
                if (file.exists()) {
                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    bitmap?.let {
                        withContext(Dispatchers.Main) {
                            _profilePhotoBitmap = it.asImageBitmap()
                        }
                    }
                }
            } catch (e: Exception) {
                println("❌ Error al cargar foto compartida: ${e.message}")
            }
        }
    }

    suspend fun saveProfilePhoto(context: Context, uri: Uri): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val file = File(context.filesDir, FILENAME)
                val outputStream = FileOutputStream(file)

                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()

                // Actualizar el estado
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                bitmap?.let {
                    withContext(Dispatchers.Main) {
                        _profilePhotoBitmap = it.asImageBitmap()
                    }
                }

                true
            } catch (e: Exception) {
                false
            }
        }
    }

    fun setProfilePhotoBitmap(bitmap: ImageBitmap?) {
        _profilePhotoBitmap = bitmap
    }

    suspend fun deleteProfilePhoto(context: Context): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(context.filesDir, FILENAME)
                if (file.exists()) {
                    file.delete()
                }
                withContext(Dispatchers.Main) {
                    _profilePhotoBitmap = null
                }
                true
            } catch (e: Exception) {
                false
            }
        }
    }
}