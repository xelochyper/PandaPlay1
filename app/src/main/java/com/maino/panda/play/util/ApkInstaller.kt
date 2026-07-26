package com.maino.panda.play.util

import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

/**
 * ApkInstaller handles downloading .apk files with progress tracking
 * and triggering native package installation automatically via FileProvider.
 */
class ApkInstaller(private val context: Context) {

    private val _downloadProgress = MutableStateFlow(0)
    val downloadProgress: StateFlow<Int> = _downloadProgress

    private val _isDownloading = MutableStateFlow(false)
    val isDownloading: StateFlow<Boolean> = _isDownloading

    private val _installStatus = MutableStateFlow<String?>(null)
    val installStatus: StateFlow<String?> = _installStatus

    /**
     * Download APK with real-time byte progress updates
     * and auto-trigger installation upon 100% completion.
     */
    fun downloadAndInstallApk(apkUrl: String, fileName: String, scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            try {
                _isDownloading.value = true
                _downloadProgress.value = 0
                _installStatus.value = "Mengunduh file APK..."

                val destinationFile = File(
                    context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                    if (fileName.endsWith(".apk")) fileName else "$fileName.apk"
                )

                if (destinationFile.exists()) {
                    destinationFile.delete()
                }

                val client = OkHttpClient()
                val request = Request.Builder().url(apkUrl).build()
                val response = client.newCall(request).execute()

                if (!response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        _isDownloading.value = false
                        _installStatus.value = "Gagal mengunduh file (HTTP ${response.code})"
                    }
                    return@launch
                }

                val body = response.body
                if (body == null) {
                    withContext(Dispatchers.Main) {
                        _isDownloading.value = false
                        _installStatus.value = "Respons download kosong"
                    }
                    return@launch
                }

                val contentLength = body.contentLength()
                val inputStream = body.byteStream()
                val outputStream = FileOutputStream(destinationFile)

                val buffer = ByteArray(8 * 1024)
                var bytesRead: Int
                var totalBytesRead = 0L

                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                    totalBytesRead += bytesRead
                    if (contentLength > 0) {
                        val progress = ((totalBytesRead * 100) / contentLength).toInt()
                        _downloadProgress.value = progress
                    }
                }

                outputStream.flush()
                outputStream.close()
                inputStream.close()

                _downloadProgress.value = 100
                _isDownloading.value = false
                _installStatus.value = "Unduhan selesai (100%). Memulai instalasi..."

                withContext(Dispatchers.Main) {
                    installApkFile(destinationFile)
                }

            } catch (e: Exception) {
                Log.e("ApkInstaller", "Download failed", e)
                withContext(Dispatchers.Main) {
                    _isDownloading.value = false
                    _installStatus.value = "Gagal: ${e.localizedMessage}"
                }
            }
        }
    }

    /**
     * Trigger native Android package installation Intent with FileProvider
     */
    fun installApkFile(apkFile: File) {
        if (!apkFile.exists()) {
            _installStatus.value = "File APK tidak ditemukan"
            return
        }

        // Check Unknown Apps Installation permission for Android 8.0 (Oreo) and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!context.packageManager.canRequestPackageInstalls()) {
                _installStatus.value = "Mohon izinkan instalasi aplikasi dari sumber ini"
                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                    data = Uri.parse("package:${context.packageName}")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                return
            }
        }

        try {
            val apkUri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    apkFile
                )
            } else {
                Uri.fromFile(apkFile)
            }

            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(installIntent)
            _installStatus.value = "Membuka installer APK..."
        } catch (e: Exception) {
            Log.e("ApkInstaller", "Error launching APK installer", e)
            _installStatus.value = "Gagal membuka installer: ${e.localizedMessage}"
        }
    }
}
