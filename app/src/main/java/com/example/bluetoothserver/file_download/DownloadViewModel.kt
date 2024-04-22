package com.example.bluetoothserver.file_download

import android.content.Context
import android.net.Uri
import android.system.ErrnoException
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bluetoothserver.BuildConfig
import com.example.bluetoothserver.Utilities.EasyPreference
import com.example.bluetoothserver.Broucher.Data
import com.example.bluetoothserver.Utilities.PDFDocument
import com.example.bluetoothserver.database.Broucher
import com.example.bluetoothserver.file_download.Networking.BroucherResponse
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class DownloadViewModel(
    private val easyPreference: EasyPreference.Builder,
    private val repository: DownloadRepository
): ViewModel() {

//    private var ratting: String = "2.0"
    private lateinit var _fileName: String
    private var prdouctFileMasterId = 0
    private var productId = 0
    private lateinit var productName: String
    private var groupId = 0
    private lateinit var groupName: String
    private var divisionId = 0
    private lateinit var divisionName: String
    private lateinit var fileGuidName: String

    private lateinit var startTime: Date
    private lateinit var endTime: Date
    private var listData = mutableListOf<Data>()


     fun downloadFile(path: String, data: Data) : LiveData<String>{
        var result = MutableLiveData<String>()
         try{
             viewModelScope.launch {
                 val baseUrl = getDownloadBrochureURL()
                 val filePath = data.filesPath
                 val fileName = filePath.substring(filePath.lastIndexOf("\\") + 1)
                 val url = baseUrl + fileName
                 val pathWhereYouWantToSaveFile = path + "/" + fileName
                 result = repository.downloadPDF(url,pathWhereYouWantToSaveFile,fileName)


             }
         } catch (e: ErrnoException){
             e.printStackTrace()
         }

         return result
    }



    fun calulateProgress(totalSize:Double,downloadSize:Double):Double{
        return ((downloadSize/totalSize)*100)
    }

    fun downloadBrochure(context: Context, token:String): LiveData<BroucherResponse>{
        return repository.downloadBrochure(token)
    }

    fun getFromDb() : LiveData<List<Broucher>> {
        return repository.getBroucherFromDb()
    }

    fun getFromDbIsMerged() : List<Broucher>{
        return repository.getBroucherFromDbIsMerged()
    }

    fun map(path: String, data: ArrayList<Data>) : ArrayList<Data> {

        listData = ArrayList<Data>()
        listData.addAll(data)

        data.map {
            val filePath = it.filesPath
            val fileName = filePath.substring(filePath.lastIndexOf("\\") + 1)
            _fileName = it.filesName
            prdouctFileMasterId = it.id
            productId = it.productMasterId
            productName = it.productName
            groupId = it.groupId
            groupName = it.groupName
            divisionId = it.divisionId
            divisionName = it.divisionName
            fileGuidName = it.guid
            it.isFavorite = !easyPreference.getString(it.guid, "").equals("")
            it.exist =
                File( path + "/" + fileName).exists()
        }

        return filterFavorite(ArrayList(data))

    }

    fun updateBroucher(path:String, data: Data) {

        val filePath = data.filesPath
        val fileName = filePath.substring(filePath.lastIndexOf("\\") + 1)

        val file = File( path + "/" + fileName)
        val deleted = file.delete()

        if (deleted) {
            easyPreference.remove(data.guid).save()
            if(data.isMerged)
            {
                repository.deleteBroucher(fileName)
            }else{
                repository.updateBroucherStatus(fileName)
            }

        }
    }

    fun setSelectedPDFInfo(it: Data) {
        _fileName = it.filesName
        prdouctFileMasterId = it.id
        productId = it.productMasterId
        productName = it.productName
        groupId = it.groupId
        groupName = it.groupName
        divisionId = it.divisionId
        divisionName = it.divisionName
        fileGuidName = it.guid

        startTime = Date()
    }

    fun getSelectedPDFInfo(): JsonObject {
        endTime = Date()
        if (!this::startTime.isInitialized) {
            startTime = Date()
        }
        val duration = endTime.time - startTime.time
        val body = JsonObject()
        body.addProperty("FileName", _fileName)
        body.addProperty("PrdouctFileMasterId", prdouctFileMasterId)
        body.addProperty("ProductId", productId)
        body.addProperty("ProductName", productName)
        body.addProperty("GroupId", groupId)
        body.addProperty("GroupName", groupName)
        body.addProperty("DivisionId", divisionId)
        body.addProperty("DivisionName", divisionName)
        body.addProperty("FileGuidName", fileGuidName)
//        body.addProperty("ratting", ratting)
        body.addProperty("Duration", TimeUnit.MILLISECONDS.toSeconds(duration))
        Log.d("Duration", _fileName + " " + duration)
        return body
    }

    fun getDownloadBrochureURL(): String {
        var URL = ""
        if (BuildConfig.FLAVOR.equals("development")) {
            URL = "http://10.0.1.46:9480/DocConnectFiles/"
        } else if (BuildConfig.FLAVOR.equals("qa")) {
            URL = "https://qa-docinfo.samipharma.com.pk/DocConnectFiles/"
        } else if (BuildConfig.FLAVOR.equals("uat")) {
            URL = "https://uat-docinfoapi.samipharma.com.pk/DocConnectFiles/"
        } else if (BuildConfig.FLAVOR.equals("production")) {
            URL = "https://docinfo.samipharma.com.pk/DocConnectFiles/"
        }

        return URL
    }

    fun saveToLocalDb(data: List<Data>) {
        repository.saveToDb(data)
    }

    fun saveToLocalDb(data: Data) {
        repository.saveToDb(data)
    }

    fun brochureMap(path:String, data: List<Data>,context: Context) : ArrayList<Data> {

        listData = ArrayList<Data>()
        listData.addAll(data)

        data.map {
            val filePath = it.filesPath
            val fileName =
                filePath.substring(filePath.lastIndexOf("\\") + 1)
            it.guid = fileName
            it.isFavorite =
                !easyPreference.getString(it.guid, "").equals("")
            it.exist =
                File(path + "/" + fileName).exists()
            var fileData = path + "/" + fileName
            val imageUri: Uri = Uri.fromFile(File(path + "/" + fileName))//fileData.toString().toUri()
            val document = PDFDocument(context, imageUri)
            it.pdfDocument =document
        }

        return filterFavorite(ArrayList(data))

    }

    fun filterBrochure(s: String) : ArrayList<Data> {

        listData.filter {
            it.productName.contains(s,true) ||
                    it.groupName.contains(s,true) ||
                    it.divisionName.contains(s,true)
        }.also {

            return ArrayList(filterFavorite(ArrayList(it)))
        }

    }

    fun getBrochureList() : ArrayList<Data> {
        return ArrayList(filterFavorite(ArrayList(listData)))
    }

    private fun filterFavorite(listData: ArrayList<Data>) : ArrayList<Data> {
        val newArray = CopyOnWriteArrayList<Data>()
        newArray.addAll(listData)

        for (brochure in newArray) {
            if (brochure.isFavorite) {
                newArray.remove(brochure)
                newArray.add(0, brochure)
            }

            if(!brochure.exist){
                newArray.remove(brochure)
                newArray.add(brochure)
            }

        }

        return ArrayList(newArray)

    }

    fun convertToBrochure(it: List<Broucher>,context: Context,fileDirectoryPath:String) : ArrayList<Data> {

        val list: ArrayList<Data> = arrayListOf()
        it.forEach {
            val filePath = it.filesPath
            val guid = filePath.substring(filePath.lastIndexOf("\\") + 1)

            var fileData = Uri.fromFile(File(fileDirectoryPath + "/" + guid))//fileDirectoryPath+"/"+guid
            val imageUri: Uri =  Uri.fromFile(File(fileDirectoryPath + "/" + guid))//fileData.toString().toUri()
            val document = PDFDocument(context, imageUri)

            list.add(Data(it.division, it.divisionId, it.divisionName, it.filesName, guid, it.filesPath, it.group,  it.groupId, it.groupName, it.id, it.isMobileDownload, it.productMaster, it.productMasterId, it.productName, it.exist,
                it.expiryDate,it.isFavorite,document,it.isMerged))
        }

        return list
    }

    fun setFavorite(it: Data) {
        if (it.isFavorite) {
            easyPreference.remove(it.guid).save()
        } else {
            easyPreference.addString(it.guid, it.guid).save()
        }
    }


}