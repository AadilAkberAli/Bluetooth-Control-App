package com.example.bluetoothserver.file_download.Models

import com.example.bluetoothserver.file_download.PageSelected
import com.google.gson.annotations.SerializedName
import java.util.*

data class Selected_PDF_Model(
    @SerializedName("filename") var filename: String,
    @SerializedName("pagesnum") var pagesnum: IntArray,
    @SerializedName("imagefile") var imagefile: LinkedList<PageSelected>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Selected_PDF_Model

        if (filename != other.filename) return false
        if (!pagesnum.contentEquals(other.pagesnum)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = filename.hashCode()
        result = 31 * result + pagesnum.contentHashCode()
        return result
    }
}