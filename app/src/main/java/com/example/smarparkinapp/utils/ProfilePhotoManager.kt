// ProfilePhotoManager.kt
package com.example.smarparkinapp.ui.theme.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*

class ProfilePhotoManager(private val context: Context) {

    companion object {
        private const val FILENAME = "profile_photo.jpg"
        private const val TAG = "ProfilePhotoManager"
    }

    suspend fun saveProfilePhoto(uri: Uri): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val outputStream = context.openFileOutput(FILENAME, Context.MODE_PRIVATE)

            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            Log.d(TAG, "✅ Foto guardada localmente")
            true
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al guardar foto: ${e.message}")
            false
        }
    }

    suspend fun getProfilePhoto(): ImageBitmap? = withContext(Dispatchers.IO) {
        return@withContext try {
            val file = File(context.filesDir, FILENAME)
            if (!file.exists()) {
                Log.d(TAG, "ℹ️ No hay foto guardada")
                return@withContext null
            }

            val inputStream = FileInputStream(file)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            if (bitmap != null) {
                Log.d(TAG, "✅ Foto cargada desde almacenamiento local")
                bitmap.asImageBitmap()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al cargar foto: ${e.message}")
            null
        }
    }

    suspend fun deleteProfilePhoto(): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val file = File(context.filesDir, FILENAME)
            if (file.exists()) {
                val deleted = file.delete()
                if (deleted) {
                    Log.d(TAG, "✅ Foto eliminada")
                } else {
                    Log.d(TAG, "❌ No se pudo eliminar la foto")
                }
                deleted
            } else {
                Log.d(TAG, "ℹ️ No hay foto para eliminar")
                true
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al eliminar foto: ${e.message}")
            false
        }
    }

    fun hasProfilePhoto(): Boolean {
        val file = File(context.filesDir, FILENAME)
        return file.exists()
    }
}