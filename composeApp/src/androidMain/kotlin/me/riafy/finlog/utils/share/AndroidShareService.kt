package me.riafy.finlog.utils.share

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

class AndroidShareService(private val context: Context) : ShareService {

    override fun share(content: String, filename: String) {
        val dir = File(context.cacheDir, "exports").apply { mkdirs() }
        val file = File(dir, filename)
        file.writeText(content)

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(sendIntent, "Export expenses").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }
}
