package com.example.dowmloaditems.ui.adapter


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.dowmloaditems.R
import com.example.dowmloaditems.model.Item
import kotlinx.android.synthetic.main.item_list.view.*

const val PROGRESS_ID = "progress"
class DownloadItemAdapter(var items: Array<Item>, val listener: (Item) -> Unit) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        with(holder.itemView) {
            title.text = item.title
            downloadIcon.isVisible = !item.file.exists()
            documentTypeIcon.isVisible = item.file.exists()
            progressBarDocument.isVisible = item.isDownloading
            textProgress.isVisible = item.isDownloading
            setOnClickListener {
                listener(item)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, position, payloads)
        if (payloads.firstOrNull() != null) {
            with(holder.itemView) {
                (payloads.first() as Bundle).getInt(PROGRESS_ID).also {
                    progressBarDocument.progress = it
                    progressBarDocument.isVisible = it < 99
                    textProgress.isVisible = it < 99
                    textProgress.text = "$it %"
                }
            }
        }
    }

    fun setDownloading(item: Item, isDownloading: Boolean) {
        getItem(item)?.isDownloading = isDownloading
        notifyItemChanged(items.indexOf(item))
    }

    fun setProgress(item: Item, progress: Int) {
        getItem(item)?.progress = progress
        notifyItemChanged(items.indexOf(item), Bundle().apply { putInt(PROGRESS_ID, progress) })
    }

    private fun getItem(item: Item) = items.find { item.id == it.id }

}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view)