package com.example.dowmloaditems.model

import android.net.Uri
import android.os.Environment
import androidx.core.net.toUri
import com.example.dowmloaditems.utils.globalContext
import java.io.File

class Item(val id: String, val title: String, val url: String) {
    var isDownloading: Boolean = false
    var progress = 0

    val uriFile: Uri
        get() = file.toUri()
    val file: File
        get() = File(globalContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), title)
}