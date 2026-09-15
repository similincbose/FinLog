package me.riafy.finlog.utils.share

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import platform.posix.fclose
import platform.posix.fopen
import platform.posix.fputs

@OptIn(ExperimentalForeignApi::class)
class IosShareService : ShareService {

    override fun share(content: String, filename: String) {
        val path = NSTemporaryDirectory() + filename
        if (!writeTextFile(path, content)) return

        val url = NSURL.fileURLWithPath(path)
        val activityController = UIActivityViewController(activityItems = listOf(url), applicationActivities = null)
        topViewController()?.presentViewController(activityController, animated = true, completion = null)
    }

    /**
     * Plain posix file I/O rather than NSData/NSString - the Objective-C
     * bridging for those has been the source of every prior "won't resolve"
     * surprise on this platform, and fopen/fputs takes a Kotlin String directly.
     */
    private fun writeTextFile(path: String, content: String): Boolean {
        val file = fopen(path, "w") ?: return false
        fputs(content, file)
        fclose(file)
        return true
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
