package com.example.bluetoothserver.file_download

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.bluetoothserver.Broucher.Data
import com.example.bluetoothserver.Utilities.ApiMethods
import com.example.bluetoothserver.Utilities.NetworkController
import com.example.bluetoothserver.database.AppDatabase
import com.example.bluetoothserver.database.Broucher
import com.example.bluetoothserver.file_download.Networking.BroucherResponse
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*


class DownloadRepository(private val apiMethod : ApiMethods, private val appDatabase: AppDatabase, private val mContext: Context) {
    fun downloadBrochure(token: String): MutableLiveData<BroucherResponse> {

        val liveData = MutableLiveData<BroucherResponse>()
        val call = NetworkController.getInstance().getApiMethods(mContext).getBrochuresRequest(token)
        call.enqueue(
            object : Callback<BroucherResponse?> {
                override fun onResponse(
                    call: Call<BroucherResponse?>,
                    response: Response<BroucherResponse?>
                ) {
                    Log.v("API DEBUG : ",  ""+response?.body().toString() )

                    liveData.value = response.body()
                }

                override fun onFailure(call: Call<BroucherResponse?>, t: Throwable) {
                    Log.v("API DEBUG : ", t?.message.toString())
                    liveData.value = null
                }

            }
        )

        return liveData

    }

    fun getBroucherFromDb(): LiveData<List<Broucher>> {
        return appDatabase.broucherDao().loadAllAvailableBrouchers(getCurrentDateString())
    }

    fun getBroucherFromDbIsMerged(): List<Broucher> {
        return appDatabase.broucherDao().loadAllAvailableBrouchersIsMerged(getCurrentDateString(),true)
    }

    fun updateBroucherStatus(fileName: String) {
        GlobalScope.launch (Dispatchers.IO) {
            appDatabase.broucherDao().updateBroucherByFileName(false,fileName)
        }

    }
    fun getCurrentDateString(): String {
        val sdfo = SimpleDateFormat("yyyy-MM-dd")
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        val formatedDate = sdfo.format(sdfo.parse(year.toString() + "-" + (month + 1) + "-" + day))
        return formatedDate
    }


    fun deleteBroucher(fileName: String) {
        GlobalScope.launch (Dispatchers.IO) {
            appDatabase.broucherDao().deleteByFileName(fileName)
        }

    }

    fun downloadPDF(url: String, filePath: String, fileName : String)  : MutableLiveData<String> {

        var result = MutableLiveData<String>()

        val call = NetworkController.getInstance().getApiMethods(mContext).downloadFile(url)
        call.enqueue(
            object : Callback<ResponseBody> {

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    val filePath = saveFile(response.body(), filePath)

                    updateBroucherStatus(fileName)

                    result.value = filePath
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.v("API DEBUG : ", t?.message.toString())
                    result.value = null
                }

            }
        )
        return result
    }


    private fun saveFile(body: ResponseBody?, pathWhereYouWantToSaveFile: String):String{
        if (body==null)
            return ""
        var input: InputStream? = null

        try {

            val fileSize = body.contentLength()
            var fileSizeDownloaded: Long = 0


            input = body.byteStream()
            //val file = File(getCacheDir(), "cacheFileAppeal.srl")
            val fos = FileOutputStream(pathWhereYouWantToSaveFile)
            fos.use { output ->
                val buffer = ByteArray(4 * 1024) // or other buffer size
                var read: Int
                while (input.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)

                    fileSizeDownloaded += read.toLong()

                    //calulateProgress(fileSize.toDouble(),fileSizeDownloaded.toDouble())
                    //print("file downloading $fileSizeDownloaded of $fileSize")
                }

                output.flush()

            }
            return pathWhereYouWantToSaveFile
        } catch (e:Exception){
            Log.e("saveFile",e.toString())
        }
        finally {
            input?.close()
        }
        return ""
    }

    fun saveToDb(data: List<Data>) {

        Observable.just(Unit).map {

            appDatabase.broucherDao().deleteAll(false)

            data.forEach {

                val broucherModel = Broucher(
                    it.division,
                    it.divisionId,
                    it.divisionName,
                    it.filesName,
                    it.filesPath,
                    it.group,
                    it.groupId,
                    it.groupName,
                    it.id,
                    it.isMobileDownload,
                    it.productMaster,
                    it.productMasterId,
                    it.productName,
                    it.exist,
                    it.expiryDate,
                    it.isFavorite,
                    it.isMerged
                )

                appDatabase.broucherDao().insert(broucherModel)
            }

        }.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe()
    }


    fun saveToDb(data: Data) {

        Observable.just(data).map {

                val broucherModel = Broucher(
                    it.division,
                    it.divisionId,
                    it.divisionName,
                    it.filesName,
                    it.filesPath,
                    it.group,
                    it.groupId,
                    it.groupName,
                    it.id,
                    it.isMobileDownload,
                    it.productMaster,
                    it.productMasterId,
                    it.productName,
                    it.exist,
                    it.expiryDate,
                    it.isFavorite,
                    it.isMerged
                )

            if(appDatabase.broucherDao().isRowIsExist(broucherModel.filesName))
            {
                appDatabase.broucherDao().deleteByFileName(broucherModel.filesName)
                appDatabase.broucherDao().insert(broucherModel)

            }else{
                appDatabase.broucherDao().insert(broucherModel)
            }


        }.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe()
    }
}