package com.example.dowmloaditems.base

import com.example.dowmloaditems.model.Item

class Data{
    companion object {
        val listOfItems = arrayOf(
            Item(
                "1",
                "SONG.MP4",
                "https://file-examples-com.github.io/uploads/2017/04/file_example_MP4_480_1_5MG.mp4"
            ),
            Item("2", "Title2.pdf", "https://css4.pub/2015/icelandic/dictionary.pdf"),
            Item("3", "Title3.pdf", "https://css4.pub/2015/icelandic/dictionary.pdf"),
            Item("4", "Title4.pdf", "https://css4.pub/2017/newsletter/drylab.pdf"),
            Item("5", "Title5.pdf", "https://css4.pub/2017/newsletter/drylab.pdf"),
            Item("6", "Title6.pdf", "https://css4.pub/2017/newsletter/drylab.pdf")
        )
    }
}
