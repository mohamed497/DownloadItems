package com.example.dowmloaditems.ui.activity


import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dowmloaditems.R
import com.example.dowmloaditems.base.GlobalConstants
import com.example.dowmloaditems.base.RunTimePermission
import com.example.dowmloaditems.base.Data
import com.example.dowmloaditems.model.DownloadResult
import com.example.dowmloaditems.model.Item
import com.example.dowmloaditems.ui.adapter.DownloadItemAdapter
import com.example.dowmloaditems.ui.viewmodel.DownloadViewModel
import com.example.dowmloaditems.utils.downloadFile
import com.example.dowmloaditems.utils.globalContext
import io.ktor.client.HttpClient
import kotlinx.android.synthetic.main.activity_download.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import java.lang.Exception
class DownloadActivity : AppCompatActivity() {
    private var runtimePermission: RunTimePermission = RunTimePermission(this)
    private lateinit var myAdapter: DownloadItemAdapter
    private lateinit var downloadViewModel: DownloadViewModel
    private lateinit var  remoteView: RemoteViews
    private val ktor: HttpClient by inject()

    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder:NotificationCompat.Builder
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        notificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
         notificationBuilder =
            NotificationCompat.Builder(globalContext, GlobalConstants.NOTIFICATION_CHANNEL)

        remoteView = RemoteViews(globalContext.packageName, R.layout.custom_notif)
        remoteView.setImageViewResource(R.id.iv_notif, R.drawable.ic_launcher_background)

        setContentView(R.layout.activity_download)
        setupRecyclerview()

    }

/*    private fun observeOnAlbums(item: Item) {
//        downloadViewModel.observeOnAlbums(this, { downloadResult ->
//            when (downloadResult) {
//                is DownloadResult.Success -> {
//                    myAdapter.setDownloading(item, false)
//                }
//                is DownloadResult.Error -> {
//                    myAdapter.setDownloading(item, false)
//                    setupToast(getString(R.string.downloading_error))
//                }
//                is DownloadResult.Progress -> {
//                    myAdapter.setProgress(item, downloadResult.progress)
//                }
//            }
//        })
//    }

//    private fun initViewModel() {
//        downloadViewModel =
//            ViewModelProvider(this)
//                .get(DownloadViewModel::class.java)
    } */

    private fun setupRecyclerview() {
        with(recyclerView) {
            layoutManager = LinearLayoutManager(this@DownloadActivity)
            DividerItemDecoration(
                context,
                (layoutManager as LinearLayoutManager).orientation
            ).apply {
                addItemDecoration(this)
            }
            myAdapter = DownloadItemAdapter(Data.listOfItems) { item ->
                manageClickAdapter(item)
            }
            adapter = myAdapter
        }
    }

    private fun manageClickAdapter(item: Item) {
        when {
            item.isDownloading -> {
            }
//            item.file.exists() -> openFile(item.file)
            item.file.exists() -> setupToast("File Already Downloaded")
            else -> {
                try {
                    runtimePermission.requestPermission(listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        object : RunTimePermission.PermissionCallback {
                            override fun onGranted() {
                                downloadWithFlow(item)
                            }

                            override fun onDenied() {
                                setupToast(getString(R.string.work_permission))
                            }
                        })
                } catch (e: Exception) {
                    Log.d(DownloadActivity::class.java.simpleName, e.toString())
                }
            }
        }
    }

    private fun downloadWithFlow(item: Item) {
        CoroutineScope(Dispatchers.IO).launch {
            ktor.downloadFile(item.file, item.url).collect { downloadResult ->
                withContext(Dispatchers.Main) {
                    when (downloadResult) {
                        is DownloadResult.Success -> {
                            myAdapter.setDownloading(item, false)

                        }
                        is DownloadResult.Error -> {
                            myAdapter.setDownloading(item, false)
                            setupToast(getString(R.string.downloading_error))
                        }
                        is DownloadResult.Progress -> {
                            createNotificationChannel()
                            displayNotification(item)
                            myAdapter.setProgress(item, downloadResult.progress)
                        }
                    }
                }
            }
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == GlobalConstants.PERMISSION_REQUEST)
            runtimePermission.onRequestPermissionsResult(grantResults)
    }

    fun setupToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                GlobalConstants.NOTIFICATION_CHANNEL,
                GlobalConstants.NOTIFICATION_CHANNEL,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.enableVibration(false)
            notificationManager.createNotificationChannel(channel)
        }
    }
    @SuppressLint("RemoteViewLayout")
    private fun displayNotification(item: Item) {
        remoteView.setTextViewText(
            R.id.tv_notif_progress,
            "${item.title} (${item.progress} complete)"
        )
        remoteView.setTextViewText(R.id.tv_notif_title, GlobalConstants.DOWNLOADING_NOTIFICATION)
        remoteView.setProgressBar(
            R.id.pb_notif,
            90,
            item.progress,
            false
        )
        notificationBuilder
            .setContent(remoteView)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setDefaults(0)

        notificationManager.notify(GlobalConstants.NOTIFICATION_ID, notificationBuilder.build())
    }
}
