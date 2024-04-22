package com.example.bluetoothserver.Broucher

import com.example.bluetoothserver.Utilities.PDFDocument
import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("division") var division: String?,
    @SerializedName("divisionId") var divisionId: Int,
    @SerializedName("divisionName") var divisionName: String,
    @SerializedName("filesName") var filesName: String,
    @SerializedName("guid") var guid: String,
    @SerializedName("filesPath") var filesPath: String,
    @SerializedName("group") var group: String?,
    @SerializedName("groupId") var groupId: Int,
    @SerializedName("groupName") var groupName: String,
    @SerializedName("id") var id: Int,
    @SerializedName("isMobileDownload") var isMobileDownload: Boolean,
    @SerializedName("productMaster") var productMaster: String?,
    @SerializedName("productMasterId") var productMasterId: Int,
    @SerializedName("productName") var productName: String,
    @SerializedName("exist") var exist : Boolean,
    @SerializedName("expiryDate") var expiryDate: String,
    @SerializedName("isFavorite") var isFavorite: Boolean,
    var pdfDocument: PDFDocument,
    var isMerged: Boolean,
)