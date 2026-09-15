package me.riafy.finlog.utils.share

/**
 * Hands a text file to the platform's native share sheet - the CSV export
 * flow's only use for it today, but kept generic in case another export
 * format needs it later.
 */
interface ShareService {
    fun share(content: String, filename: String)
}
