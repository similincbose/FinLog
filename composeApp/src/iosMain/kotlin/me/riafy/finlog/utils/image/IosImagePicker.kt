package me.riafy.finlog.utils.image

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSDate
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask
import platform.Foundation.timeIntervalSince1970
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.UIKit.UIViewController
import platform.darwin.NSObject
import kotlin.coroutines.resume

/**
 * Kotlin/Native won't let one class mix a plain Kotlin interface with
 * Objective-C protocol conformance, so the UIKit delegate machinery lives here
 * and [IosImagePicker] below - the actual [ImagePicker] - just forwards to it.
 */
@OptIn(ExperimentalForeignApi::class)
private class ImagePickerDelegate : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {

    private var pendingCompletion: ((String?) -> Unit)? = null

    suspend fun present(sourceType: UIImagePickerControllerSourceType): String? {
        val presenter = topViewController() ?: return null
        return suspendCancellableCoroutine { continuation ->
            pendingCompletion = { path -> continuation.resume(path) }

            val picker = UIImagePickerController()
            picker.sourceType = sourceType
            picker.delegate = this
            presenter.presentViewController(picker, animated = true, completion = null)
        }
    }

    override fun imagePickerController(
        picker: UIImagePickerController,
        didFinishPickingMediaWithInfo: Map<Any?, *>
    ) {
        picker.dismissViewControllerAnimated(true, completion = null)
        val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage
        val path = image?.let { saveToDocuments(it) }
        pendingCompletion?.invoke(path)
        pendingCompletion = null
    }

    override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
        picker.dismissViewControllerAnimated(true, completion = null)
        pendingCompletion?.invoke(null)
        pendingCompletion = null
    }

    private fun saveToDocuments(image: UIImage): String? {
        val data = UIImageJPEGRepresentation(image, 0.85) ?: return null
        val documentsDir = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, true)
            .firstOrNull() as? String ?: return null
        val receiptsDir = "$documentsDir/receipts"
        NSFileManager.defaultManager.createDirectoryAtPath(receiptsDir, true, null, null)

        val path = "$receiptsDir/receipt_${NSDate().timeIntervalSince1970.toLong()}.jpg"
        return if (NSFileManager.defaultManager.createFileAtPath(path, data, null)) path else null
    }

    private fun topViewController(): UIViewController? {
        val root = UIApplication.sharedApplication.keyWindow?.rootViewController ?: return null
        var top = root
        while (true) {
            top = top.presentedViewController ?: break
        }
        return top
    }
}

class IosImagePicker : ImagePicker {

    private val delegate = ImagePickerDelegate()

    override suspend fun captureFromCamera(): String? =
        delegate.present(UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera)

    override suspend fun pickFromGallery(): String? =
        delegate.present(UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary)
}
