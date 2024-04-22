package com.example.bluetoothserver.file_download.Networking

import com.example.bluetoothserver.Broucher.Data
import com.google.gson.annotations.SerializedName

data class BroucherResponse(
    @SerializedName("data") val `data`: List<Data>,
    @SerializedName("detailsMessage") val detailsMessage: Any,
    @SerializedName("message") val message: String,
    @SerializedName("newVersionCode") var newVersionCode: String,
    @SerializedName("newVersionNo") var newVersionNo: Int = 0,
    @SerializedName("success") val success: Boolean
)