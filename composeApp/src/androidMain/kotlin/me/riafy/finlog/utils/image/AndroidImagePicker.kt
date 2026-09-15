package me.riafy.finlog.utils.image

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.resume

class AndroidImagePicker(private val context: Context) : ImagePicker {

    /** Set by MainActivity: launches the camera writing into the given file, reports whether a photo was saved. */
    var cameraLauncher: ((destinationPath: String, onResult: (Boolean) -> Unit) -> Unit)? = null

    /** Set by MainActivity: launches the system photo picker, reports the chosen image (or null if cancelled). */
    var galleryLauncher: ((onResult: (Uri?) -> Unit) -> Unit)? = null

    override suspend fun captureFromCamera(): String? {
        val launcher = cameraLauncher ?: return null
        val file = newReceiptFile()
        return suspendCancellableCoroutine { continuation ->
            launcher(file.absolutePath) { success ->
                continuation.resume(if (success) file.absolutePath else null)
            }
        }
    }

    override suspend fun pickFromGallery(): String? {
        val launcher = galleryLauncher ?: return null
        val uri = suspendCancellableCoroutine<Uri?> { continuation ->
            launcher { continuation.resume(it) }
        } ?: return null
        return copyToReceiptFile(uri)
    }

    private fun newReceiptFile(): File {
        val dir = File(context.filesDir, "receipts").apply { mkdirs() }
        return File(dir, "receipt_${System.currentTimeMillis()}.jpg")
    }

    private fun copyToReceiptFile(uri: Uri): String? {
        val file = newReceiptFile()
        return try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(file).use { output -> input.copyTo(output) }
            } ?: return null
            file.absolutePath
        } catch (_: Exception) {
            null
        }
    }
}
