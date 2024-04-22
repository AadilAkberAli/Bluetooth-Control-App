package com.example.bluetoothserver.file_download

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.example.bluetoothserver.Bluetoothchat.BluetoothChat
import com.example.bluetoothserver.Bluetoothchat.BluetoothChatService
import com.example.bluetoothserver.Broucher.Data
import com.example.bluetoothserver.BuildConfig
import com.example.bluetoothserver.Login.Login
import com.example.bluetoothserver.Login.LoginModel
import com.example.bluetoothserver.R
import com.example.bluetoothserver.Utilities.BluetoothService
import com.example.bluetoothserver.Utilities.EasyPreference
import com.example.bluetoothserver.Utilities.GH
import com.example.bluetoothserver.Utilities.NetworkController
import com.example.bluetoothserver.database.AppDatabase
import com.example.bluetoothserver.file_download.Controller.BluetoothController
import com.example.bluetoothserver.file_download.Models.Selected_PDF_Model
import com.example.bluetoothserver.file_download.Utilities.AppConstants
import com.example.bluetoothserver.file_download.adapter.BroucherAdapter
import com.example.bluetoothserver.file_download.pdfmerger.PDFMerger
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_download_broucher.*
import kotlinx.android.synthetic.main.base_toolbar.*
import java.io.File


class DownloadBroucherActivity : BluetoothChat() {

    private lateinit var viewModel: DownloadViewModel
    private lateinit var timeArray: JsonArray
    private lateinit var connectedstatus : TextView
    private lateinit var btnDevices : Button
    private lateinit var bluetoothController: BluetoothController
    private var hasBluetooth:Boolean=false
    var text = java.util.ArrayList<Selected_PDF_Model>()
    var index = -1
    companion object {
        var selectedFilesForMerge: ArrayList<Data>? = ArrayList()

        fun addOrRemoveFiles(item:Data,isAdded:Boolean)
        {
            if(isAdded)
            {
                selectedFilesForMerge!!.add(item)
            }else{
                selectedFilesForMerge!!.remove(item)
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.constraintbrochureactivity)
        connectedstatus = findViewById(R.id.connectedstatus);
        val txt = findViewById<TextView>(R.id.txtTitle);
        val txtuser = findViewById<TextView>(R.id.txtName);
        val scanbtn = findViewById<Button>(R.id.scan);
        val discoverbtn = findViewById<Button>(R.id.discover);
        easyPreference= EasyPreference.with(this)
        val data = easyPreference.getObject(GH.KEYS.LOGIN_MODEL.name, LoginModel::class.java)
        txt.text = "Download Brochure"
        val mlogout = findViewById<View>(R.id.btnLogout) as ImageView
       btnDevices = findViewById<Button>(R.id.btnDevices)
        bluetoothController = BluetoothController.getInstance(this)

//        hasBluetooth = getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)
//        if(hasBluetooth)
//        {
//            bluetoothController.viewModel.state.observe(this, Observer {
//                if(BluetoothChatService.STATE_CONNECTED==it) {
//                    connectedstatus.setText("Connected")
//                    connectedstatus.setTextColor(Color.GREEN)
//                }else{
//                    connectedstatus.setText("Not Connected")
//                    connectedstatus.setTextColor(Color.RED)
//                }
//            })
//        }
        mlogout.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                showLogoutDialog(
                    this@DownloadBroucherActivity,
                    "Logout",
                    "Sure! you want to Logout?",
                    easyPreference
                )
            }

        })
        scanbtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                scan(view)
            }

        })
        discoverbtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
               discover(view)
            }

        })
        //txtAppVersionName.setText(BuildConfig.FLAVOR+" (TESTING) V " + BuildConfig.VERSION_NAME);
        txtuser.setText(
            data.data.firstName.toString() + " " + data.data.lastName + " (" + data.data.userName + ")"
        )
        viewModel = DownloadViewModel(
            easyPreference, DownloadRepository(
                NetworkController.getInstance()
                    .getApiMethods(this@DownloadBroucherActivity), AppDatabase.getInstance(this), this
            )
        )

        initSearchBox()

        timeArray = JsonArray()
        fetchBrouchers()
        imgBack.setOnClickListener {
            onBackPressed()
        }

    }



    private fun showLogoutDialog(
        context: Context,
        title: String,
        message: String,
        easyPreference: EasyPreference.Builder
    ) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message) // Specifying a listener allows you to take an action before dismissing the dialog.
            // The dialog is automatically dismissed when a dialog button is clicked.
            .setPositiveButton(
                android.R.string.no
            ) { dialog, which -> // Continue with delete operation
                dialog.dismiss()
            }
            .setNegativeButton(
                android.R.string.yes
            ) { dialog, which -> // Continue with delete operation
                val userId = easyPreference.getInt(GH.KEYS.USERID.name, -1)
                val recoveryEmail = easyPreference.getString(GH.KEYS.RECOVERYEMAIL.name, "")
                easyPreference.remove(GH.KEYS.LOGIN_MODEL.name)
                easyPreference.remove(GH.KEYS.USERID.name)
                easyPreference.remove(GH.KEYS.AUTHORIZATION.name)
                easyPreference.remove(GH.KEYS.PASSWORD.name)
                if (userId != -1 && recoveryEmail !== "") {
                    easyPreference.addInt(GH.KEYS.USERID.name, userId)
                        .save()
                    easyPreference.addString(
                        GH.KEYS.RECOVERYEMAIL.name,
                        recoveryEmail
                    ).save()
                }
                GH.getInstance().startActivity(context, Login::class.java)
            } // A null listener allows the button to dismiss the dialog and take no further action.
            //.setIcon(R.drawable.ic_reason_24)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    private var receiver:BroadcastReceiver=object:BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            if(!TextUtils.isEmpty(p1!!.getStringExtra("message"))){
                sendMessage(p1!!.getStringExtra("message"))
            }
        }

    }

    private var connectedreceiver:BroadcastReceiver=object:BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            if(p1!!.getStringExtra("message") == "Connected")
            {
                connectedstatus.setText("Connected")
                connectedstatus.setTextColor(Color.GREEN)
            }
            else
            {
                connectedstatus.setText("Not Connected")
                connectedstatus.setTextColor(Color.RED)
            }
        }

    }

    override fun onStart() {
        super.onStart()
        registerReceiver(receiver, IntentFilter("Custom Bluetooth"))
        registerReceiver(connectedreceiver, IntentFilter("Custom Bluetooth Connectivity"))
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(connectedreceiver)
        unregisterReceiver(receiver)

    }

    override fun onResume() {
        super.onResume()
        if (!AppUtils.isTimeAutomatic(this@DownloadBroucherActivity)) {
            AppUtils.showDateTimeAutoUpdateDialog(
                this@DownloadBroucherActivity,
                "Alert",
                "Unable to Open, Auto DateTime check is disable from settings"
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun fetchBrouchers() {

        if (!AppConstants.isNetworkAvailable(this)) {

            fetchBrouchersFromLocalDb()

            return
        } else {
            llProgressBar.visibility = View.VISIBLE
            viewModel.downloadBrochure(this, GH.getInstance().getAuthorization(this))
                .observe(this, Observer {
                    llProgressBar.visibility = View.GONE
                    if (it != null) {

//                        if (BuildConfig.VERSION_CODE < it.newVersionNo) {
//                            AppUtils.showCustomDialog(
//                                this@DownloadBroucherActivity,
//                                "Version Update", "An update of Doc Connect is available. \n\n"
//                                        + "<a href='" + getString(R.string.link_UAT) + "'> Click here to download apk </a>"
//                            )
//                        }
                        if (it.success) {
                            Log.d("BroucherResponse", Gson().toJson(it.data) + "")

                            val newArray = viewModel.brochureMap(
                                applicationContext.filesDir.absolutePath,
                                it.data as ArrayList,
                                this@DownloadBroucherActivity
                            )

                            viewModel.saveToLocalDb(it.data)

                            Thread {

                               var it =  viewModel.getFromDbIsMerged()

                                    var datas = ArrayList<Data>()
                                    datas = viewModel.convertToBrochure(it,this@DownloadBroucherActivity,applicationContext.filesDir.absolutePath)

                                    if (datas.isNotEmpty()) {
                                        newArray.addAll(datas)
                                        val newArrays = viewModel.map(applicationContext.filesDir.absolutePath, newArray)
                                        runOnUiThread {
                                            list_recycler_view.apply {
                                                layoutManager = GridLayoutManager(this@DownloadBroucherActivity, 2)
                                                adapter = BroucherAdapter(
                                                    this@DownloadBroucherActivity,
                                                    easyPreference,
                                                    newArrays,
                                                    onDownloadClick,
                                                    viewBroucher,
                                                    deleteFile,
                                                    onClickFavorite
                                                )
                                            }
                                            (list_recycler_view.adapter as BroucherAdapter).setMergeFile(this@DownloadBroucherActivity)
                                        }

                                    }


                            }.start()

                            list_recycler_view.apply {
                                layoutManager = GridLayoutManager(this@DownloadBroucherActivity, 2)
                                adapter = BroucherAdapter(
                                    this@DownloadBroucherActivity,
                                    easyPreference,
                                    newArray,
                                    onDownloadClick,
                                    viewBroucher,
                                    deleteFile,
                                    onClickFavorite
                                )
                            }
                            (list_recycler_view.adapter as BroucherAdapter).setMergeFile(this@DownloadBroucherActivity)
                        } else {
                            Log.d("BroucherResponse", "error:" + it.message)
                            AppUtils.showDialog(this,"Alert", it.message)
                        }
                    } else {
                        AppUtils.showDialog(this,"Error", "Something went wrong!")
                    }
                })
        }

    }


    fun resetingMergeFileArray()
    {
        selectedFilesForMerge = ArrayList()
    }

    fun fetchBrouchersFromLocalDb() {

        btnDevices.visibility = View.GONE
        viewModel.getFromDb().observe(this) {

            var data = viewModel.convertToBrochure(it,this@DownloadBroucherActivity,applicationContext.filesDir.absolutePath)

            if (data.isNotEmpty()) {

                val newArray = viewModel.map(applicationContext.filesDir.absolutePath, data)

                list_recycler_view.apply {

                    layoutManager = GridLayoutManager(this@DownloadBroucherActivity, 2)
                    adapter = BroucherAdapter(
                        this@DownloadBroucherActivity,
                        easyPreference,
                        newArray,
                        onDownloadClick,
                        viewBroucher,
                        deleteFile,
                        onClickFavorite
                    )
                }
                (list_recycler_view.adapter as BroucherAdapter).setMergeFile(this@DownloadBroucherActivity)
            } else {
                AppUtils.showDialog(this, "Alert", "No Record Found")
            }
        }

    }

    private fun initSearchBox() {
        editSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {

                if(list_recycler_view.adapter != null)
                {
                    if (s.isNotEmpty()) {
                        val listData = viewModel.filterBrochure(s.toString())
                        (list_recycler_view.adapter as BroucherAdapter).add(ArrayList(listData))

                    } else if (s.isEmpty()) {
                        (list_recycler_view.adapter as BroucherAdapter).add(ArrayList(viewModel.getBrochureList()))
                    }
                }

            }

        })
        selectedFilesForMerge = ArrayList()
        btnDevices.setOnClickListener {
            Log.e("TEST-MERGE", "HELLO MERE")
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                CheckStoragePermission()
            } else {
                showMergeDialog()
            }


        }

    }

    private val deleteFile: (Data) -> Unit = {

        deletefromsharepreference(it.guid)

        llProgressBar.visibility = View.VISIBLE

        viewModel.updateBroucher(applicationContext.filesDir.absolutePath, it)

        llProgressBar.visibility = View.GONE

        fetchBrouchers()
    }

    private fun deletefromsharepreference(filename: String)
    {
        text = ArrayList()
        Log.e("filename", filename)
        val gson = Gson()
        var selectedpagesref =  easyPreference.getString(GH.KEYS.SELECTEDPAGES.name, null)
        // getting data from gson and storing it in a string.
        // getting data from gson and storing it in a string.
        if(selectedpagesref != null)
        {
                Log.e("Checkingsaverefmodelretrieve", selectedpagesref.toString())
                val collectionType = object : TypeToken<java.util.ArrayList<Selected_PDF_Model>>() {}.type
                text = gson.fromJson(selectedpagesref, collectionType)
                Log.e("Size", text.size.toString())
                for (i in 0 until text.size)
                {
                    if(text.get(i).filename.equals(filename, true))
                    {
                        index = i
                        text.removeAt(index)
                        easyPreference.remove(GH.KEYS.SELECTEDPAGES.name)
                        val json = gson.toJson(text)
                        easyPreference.addString(GH.KEYS.SELECTEDPAGES.name, json).save()
                        Log.e("Remainingarray", text.toString())
                        break;
                    }
                }
        }

    }
    private val viewBroucher: (Data) -> Unit = {

        val filePath = it.filesPath
        val fileName = filePath.substring(filePath.lastIndexOf("\\") + 1)
        if (File(applicationContext.filesDir.absolutePath + "/" + fileName).exists()) {

            if (AppUtils.isPDF(fileName)) {
                val intent = Intent(this, PdfViewActivity::class.java)
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                var Filenamecommmand = "rt~v~$fileName"
                sendControlMessage(Filenamecommmand)
                intent.putExtra("File_Name", fileName)
                startActivityForResult(intent, 600)
                viewModel.setSelectedPDFInfo(it)

            } else {
                AppUtils.showDialog(this, "Alert", "View for this file format is not available.")
            }
        } else {
            AppUtils.showDialog(this, "Alert", it.productName + " file not found.")
        }

    }


    private val onClickFavorite: (Data) -> Unit = {

        viewModel.setFavorite(it)

        //updateAdapter()


    }

    private fun updateAdapter() {

        list_recycler_view.apply {
            layoutManager = GridLayoutManager(this@DownloadBroucherActivity, 2)
            adapter = BroucherAdapter(
                this@DownloadBroucherActivity,
                easyPreference,
                viewModel.getBrochureList(),
                onDownloadClick,
                viewBroucher,
                deleteFile,
                onClickFavorite
            )



        }
        (list_recycler_view.adapter as BroucherAdapter).setMergeFile(this@DownloadBroucherActivity)



    }

    private val onDownloadClick: (Data) -> Unit = {

        if (AppConstants.isNetworkAvailable(this)) {
            llProgressBar.visibility = View.VISIBLE
            viewModel.downloadFile(applicationContext.filesDir.absolutePath, it).observe(
                this,
                Observer {
                    llProgressBar.visibility = View.GONE
                    fetchBrouchers()

                })
        } else {
            AppUtils.showDialog( this,"Connectivity Error", "Check internet connection.")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {

            600 -> {
                if (resultCode == RESULT_OK) {
                    if(::timeArray.isInitialized)
                    {
                        timeArray.add(viewModel.getSelectedPDFInfo())
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
//        var intent = Intent(this, CallExecutionActivity::class.java)
//        intent.putExtra("TIME_ARRAY", timeArray.toString())
//        setResult(RESULT_OK, intent)
        super.onBackPressed()
    }


    private fun showMergeDialog() {
        if (selectedFilesForMerge!!.size < 2) {
            Toast.makeText(this@DownloadBroucherActivity, "You need to add at least 2 file", Toast.LENGTH_LONG).show()
        } else {

            val dialog = Dialog(this@DownloadBroucherActivity)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val alertView: View = layoutInflater.inflate(R.layout.file_alert_dialog, null)
            val edittext = alertView.findViewById<View>(R.id.editText2) as EditText
            dialog.setContentView(alertView)
            dialog.setCancelable(true)
            val lp = WindowManager.LayoutParams()
            lp.copyFrom(dialog.window!!.attributes)
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            dialog.show()
            dialog.window!!.attributes = lp
            (dialog.findViewById<View>(R.id.bt_close) as ImageButton).setOnClickListener { dialog.dismiss() }
            (dialog.findViewById<View>(R.id.bt_save) as Button).setOnClickListener {

               /* if (ContextCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    CheckStoragePermission()
                } else {*/
                    llProgressBar.visibility = View.VISIBLE
                    val fileName = edittext.text.toString()
                    if (fileName != "") {
                        val merger = PDFMerger(this@DownloadBroucherActivity, "$fileName.pdf",viewModel)
                        merger.setDataSet(selectedFilesForMerge)
                        merger.setProgress(llProgressBar)
                        merger.execute()
                    } else {
                        llProgressBar.visibility = View.GONE
                        Toast.makeText(this@DownloadBroucherActivity, "File name should not be empty", Toast.LENGTH_LONG).show()

                    }
               // }
                dialog.dismiss()
            }
        }
    }

    private fun CheckStoragePermission(){
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                val alertDialog = AlertDialog.Builder(this).create()
                alertDialog.setTitle("Storage Permission")
                alertDialog.setMessage(
                    "Storage permission is required in order to " +
                            "provide PDF merge feature, please enable permission in app settings"
                )
                alertDialog.setButton(
                    AlertDialog.BUTTON_NEUTRAL, "Settings"
                ) { dialog, id ->
                    val i = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                    )
                    startActivity(i)
                    dialog.dismiss()
                }
                alertDialog.show()
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    2
                )
                Log.d("Checkpermission", "Permission already granted")
            }
        }

        else
        {
            showMergeDialog()
        }
    }

}