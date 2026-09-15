package me.riafy.finlog.utils.receipt

import me.riafy.finlog.data.models.ParsedReceipt

data class ScannedReceipt(val imagePath: String, val parsed: ParsedReceipt)

/**
 * Hands the receipt just OCR'd and parsed on the Scan screen to the Review
 * screen that follows it. A NavHost destination can only cross a route boundary
 * with primitive arguments, and this is a one-shot, same-process transfer with
 * no reason to support deep-linking - a single Koin-scoped holder is simpler
 * than encoding a whole parsed receipt into a navigation argument.
 */
class PendingReceiptHolder {

    private var pending: ScannedReceipt? = null

    fun set(receipt: ScannedReceipt) {
        pending = receipt
    }

    /** Reads and clears in one step - the review screen consumes this exactly once. */
    fun take(): ScannedReceipt? {
        val value = pending
        pending = null
        return value
    }
}
