package me.riafy.finlog.utils.receipt

import me.riafy.finlog.data.models.RecognizedText

/**
 * On-device OCR for a receipt photo. Answers only "what text is visible?" -
 * turning that into merchant/total/tax/etc. is [ReceiptParser]'s job, kept
 * deliberately separate so the parser never needs to know whether the text
 * came from ML Kit or Vision.
 */
interface ReceiptTextRecognizer {
    suspend fun recognize(imagePath: String): Result<RecognizedText>
}
