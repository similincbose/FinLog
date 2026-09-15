package me.riafy.finlog

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import me.riafy.finlog.utils.image.AndroidImagePicker
import org.koin.android.ext.android.inject
import java.io.File

class MainActivity : ComponentActivity() {

    private val imagePicker: AndroidImagePicker by inject()

    /** Set for the duration of a single camera launch. */
    private var onCameraResult: ((Boolean) -> Unit)? = null

    /** Set for the duration of a single gallery launch. */
    private var onGalleryResult: ((Uri?) -> Unit)? = null

    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        onCameraResult?.invoke(success)
        onCameraResult = null
    }

    private val pickImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        onGalleryResult?.invoke(uri)
        onGalleryResult = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // The picker lives in the DI graph but launching either flow needs an Activity
        imagePicker.cameraLauncher = { destinationPath, onResult ->
            onCameraResult = onResult
            val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", File(destinationPath))
            takePicture.launch(uri)
        }
        imagePicker.galleryLauncher = { onResult ->
            onGalleryResult = onResult
            pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        setContent { App() }
    }

    override fun onDestroy() {
        imagePicker.cameraLauncher = null
        imagePicker.galleryLauncher = null
        onCameraResult = null
        onGalleryResult = null
        super.onDestroy()
    }
}
