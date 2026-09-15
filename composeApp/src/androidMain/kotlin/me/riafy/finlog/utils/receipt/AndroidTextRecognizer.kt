package me.riafy.finlog.utils.receipt

import android.graphics.BitmapFactory
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import me.riafy.finlog.data.models.RecognizedText
import kotlin.coroutines.resume

class AndroidTextRecognizer : ReceiptTextRecognizer {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    override suspend fun recognize(imagePath: String): Result<RecognizedText> {
        val bitmap = BitmapFactory.decodeFile(imagePath)
            ?: return Result.failure(IllegalStateException("Could not read image at $imagePath"))

        return suspendCancellableCoroutine { continuation ->
            recognizer.process(InputImage.fromBitmap(bitmap, 0))
                .addOnSuccessListener { visionText ->
                    val lines = visionText.textBlocks.flatMap { block -> block.lines.map { it.text } }
                    continuation.resume(Result.success(RecognizedText(fullText = visionText.text, lines = lines)))
                }
                .addOnFailureListener { error ->
                    continuation.resume(Result.failure(error))
                }
        }
    }
}
