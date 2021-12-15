package com.example.dowmloaditems

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val link = "https://www.pexels.com/photo/1214259/download/?search_query=free%20download&tracking_id=jolliuh8k4"
        val path = Environment.getExternalStorageDirectory().toString()+"/saved_images.png"
        Thread({
            download(link,path)
            runOnUiThread({
                //Update UI
            })
        }).start()
    }
    fun download(link: String, path: String) {
            URL(link).openStream().use { input ->
                FileOutputStream(File(path)).use { output ->
                    input.copyTo(output)
                }

        }

    }
}