package me.riafy.finlog.utils.receipt

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.suspendCancellableCoroutine
import me.riafy.finlog.data.models.RecognizedText
import platform.UIKit.UIImage
import platform.Vision.VNImageRequestHandler
import platform.Vision.VNRecognizeTextRequest
import platform.Vision.VNRecognizedText
import platform.Vision.VNRecognizedTextObservation
import platform.Vision.VNRequestTextRecognitionLevelAccurate
import kotlin.coroutines.resume

class IosTextRecognizer : ReceiptTextRecognizer {

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun recognize(imagePath: String): Result<RecognizedText> {
        val image = UIImage.imageWithContentsOfFile(imagePath)
            ?: return Result.failure(IllegalStateException("Could not read image at $imagePath"))
        val cgImage = image.CGImage
            ?: return Result.failure(IllegalStateException("Image has no bitmap representation"))

        return suspendCancellableCoroutine { continuation ->
            val request = VNRecognizeTextRequest { request, error ->
                if (error != null) {
                    continuation.resume(Result.failure(Exception(error.localizedDescription)))
                    return@VNRecognizeTextRequest
                }

                @Suppress("UNCHECKED_CAST")
                val observations = (request?.results as? List<VNRecognizedTextObservation>).orEmpty()
                // Vision's coordinate space starts at the bottom-left, so sorting by
                // descending y puts the observations back into reading order.
                val sorted = observations.sortedByDescending { observation ->
                    observation.boundingBox.useContents { origin.y }
                }
                val lines = sorted.mapNotNull { observation ->
                    @Suppress("UNCHECKED_CAST")
                    val candidates = observation.topCandidates(1uL) as List<VNRecognizedText>
                    candidates.firstOrNull()?.string()
                }
                continuation.resume(Result.success(RecognizedText(fullText = lines.joinToString("\n"), lines = lines)))
            }
            request.recognitionLevel = VNRequestTextRecognitionLevelAccurate
            request.usesLanguageCorrection = true

            val handler = VNImageRequestHandler(cGImage = cgImage, options = emptyMap<Any?, Any?>())
            try {
                handler.performRequests(listOf(request), null)
            } catch (error: Throwable) {
                continuation.resume(Result.failure(error))
            }
        }
    }
}
