package me.riafy.finlog.utils.image

/**
 * Launches the system camera or photo picker and hands back the path of a copy
 * saved into this app's own storage. Both platforms use the system-provided
 * picker UI, which handles its own permission prompting - there's nothing here
 * for permissions the way there is for background location in the weather app,
 * so the interface stays down to the two actions that actually need one.
 */
interface ImagePicker {
    /** Null if the user backs out of the camera without taking a photo. */
    suspend fun captureFromCamera(): String?

    /** Null if the user dismisses the picker without choosing an image. */
    suspend fun pickFromGallery(): String?
}
