package com.example.dowmloaditems.ui.viewmodel
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.dowmloaditems.model.DownloadResult
import com.example.dowmloaditems.model.Item
import com.example.dowmloaditems.utils.downloadFile
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DownloadViewModel: ViewModel() {

    private val downloadItem = MutableLiveData<DownloadResult>()

    fun observeOnAlbums(lifecycle: LifecycleOwner, downloadResult: Observer<DownloadResult>) {
        downloadItem.observe(lifecycle, downloadResult)
    }
     fun downloadWithFlow(item: Item, ktor: HttpClient) {
        CoroutineScope(Dispatchers.IO).launch {
            ktor.downloadFile(item.file, item.url).collect { downloadResult ->
                withContext(Dispatchers.Main) {
                            downloadItem.value = downloadResult
                }
            }
        }
    }
}