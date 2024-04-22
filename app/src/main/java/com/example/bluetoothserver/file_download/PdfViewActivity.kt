package com.example.bluetoothserver.file_download

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.*
import android.text.method.KeyListener
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bluetoothserver.Broucher.Data
import com.example.bluetoothserver.R
import com.example.bluetoothserver.Utilities.EasyPreference
import com.example.bluetoothserver.Utilities.GH
import com.example.bluetoothserver.Utilities.NetworkController
import com.example.bluetoothserver.database.AppDatabase
import com.example.bluetoothserver.file_download.Controller.BluetoothController
import com.example.bluetoothserver.file_download.Models.Selected_PDF_Model
import com.example.bluetoothserver.file_download.adapter.PdfImagesAdapter
import com.example.bluetoothserver.file_download.adapter.downloadedbrochure
import com.example.bluetoothserver.file_download.remoteService.Constants
import com.example.bluetoothserver.touchimageadapter.Itemtouch
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnErrorListener
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shockwave.pdfium.PdfDocument
import kotlinx.android.synthetic.main.activity_pdf_view.*
import kotlinx.android.synthetic.main.broucher_item.*
import kotlinx.android.synthetic.main.buttons_controls.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*


/*import kotlinx.android.synthetic.main.activity_pdf_view.llProgressBar*/

class PdfViewActivity : AppCompatActivity(), OnPageChangeListener, OnLoadCompleteListener,OnErrorListener {

    private  lateinit var recycler_view_pdf: RecyclerView
    private lateinit var viewModel: DownloadViewModel
    private lateinit var imagesAdapter: PdfImagesAdapter
    private lateinit var downloadedpdfadapter: downloadedbrochure
    private lateinit var Nopdfavialable: TextView
    private var photos = java.util.LinkedList<PageSelected>()
    private var  imagefiles= java.util.LinkedList<PageSelected>()
    lateinit var bluetoothController: BluetoothController
    private var INTERVAL:Long = 1000 * 5
    var pdfPage = 0
    var pageCount: Int = 0
    var isEditing:Boolean =true
    var pdfviewpress: Boolean = false
    lateinit var currentpages: EditText
    var isplay = true
    var isplayloop = true
    var filteredlist = ArrayList<Data>()
    var positionarray = ArrayList<Int>()
    private lateinit var easyPreference: EasyPreference.Builder
    private lateinit var drawer: DrawerLayout
    private var actionBarDrawerToggle: ActionBarDrawerToggle? = null
    lateinit var bottomSheetDialog: BottomSheetDialog
    lateinit var select_all_checkbox: CheckBox
    lateinit var  recycler_view_selectedpdf: RecyclerView
    lateinit var select_page: Button
    lateinit var save_settings : Button
    lateinit var pages: IntArray
    lateinit var selectedpages:Selected_PDF_Model
    var text = ArrayList<Selected_PDF_Model>()
    private lateinit var mHandler: Handler
    private lateinit var mHandlerTask : Runnable
    private lateinit var pdfFile: File
    var recentpage= 0;
    private lateinit var variable : KeyListener ;
    companion object {
        var FILE_NAME: String = "File_Name"
        var PDF:PDFView? = null
        //var myDefaultScrollHandle:MyDefaultScrollHandle? = null

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.drawerlayout)
        // Initialize the send button with a listener that for click events
        bottomSheetDialog = BottomSheetDialog(this)
        variable = editpagenum.getKeyListener();
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_layout)
        recycler_view_selectedpdf = findViewById(R.id.recycler_view_selectedpdf)
        select_all_checkbox = findViewById(R.id.select_all_checkbox)
        save_settings = findViewById(R.id.save_settings)
        val play: LinearLayout? = bottomSheetDialog.findViewById(R.id.play)
        val downloaded: LinearLayout? = bottomSheetDialog.findViewById(R.id.downloaded)
        val uploadLinearLaySout: LinearLayout? = bottomSheetDialog.findViewById(R.id.uploadLinearLaySout)
        PDF = pdfView
        PDF
        easyPreference = EasyPreference.with(this)
        recycler_view_pdf = findViewById(R.id.recycler_view_pdf)
        Nopdfavialable = findViewById(R.id.Nopdfavialable)
        val mplaybutton = findViewById<ImageButton>(R.id.playbutton)
        val mnextbutton = findViewById<ImageButton>(R.id.nextbutton)
        val mpreviousbutton = findViewById<ImageButton>(R.id.previousbutton)
        val menus = findViewById<ImageButton>(R.id.menus)
        val pagenum = findViewById<EditText>(R.id.editpagenum)
        val pdfviews = findViewById<ImageButton>(R.id.downloadedpdfs)
        GH.getInstance().ShowProgressDialog(this@PdfViewActivity)
        val fileName = intent.extras?.get(FILE_NAME).toString()
       pdfFile = File(applicationContext.filesDir, fileName)
        photos =  pdfToBitmap(pdfFile);
        select_page = findViewById(R.id.select_page)
        val playicon = bottomSheetDialog.findViewById<ImageView>(R.id.playicon)
        val playtext = bottomSheetDialog.findViewById<TextView>(R.id.playtext)
        drawer = findViewById(R.id.my_drawer_layout)
       actionBarDrawerToggle =
            ActionBarDrawerToggle(this, drawer, R.string.nav_open, R.string.nav_close)

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button

        var currentpagenum = pageCount + 1;
        drawer.addDrawerListener(actionBarDrawerToggle!!)
        actionBarDrawerToggle!!.syncState()
        currentpages = findViewById<EditText>(R.id.editpagenum)
        currentpages.setText(currentpagenum.toString());
        recentpage = currentpagenum
        viewModel = DownloadViewModel(
            easyPreference, DownloadRepository(
                NetworkController.getInstance()
                    .getApiMethods(this@PdfViewActivity), AppDatabase.getInstance(this), this
            )
        )
        mHandler = Handler()
        mHandlerTask = object : Runnable {
            override fun run() {
                if(isplayloop){
                    runOnUiThread {
                        if (recentpage < pdfView.pageCount) {
                            pageChangeFromButton(true, recentpage)
                        }
                        else
                        {
                            mpreviousbutton.isClickable = true
                            mpreviousbutton.isEnabled = true
                            editpagenum.keyListener = variable
                            mnextbutton.isClickable = true
                            mnextbutton.isEnabled = true
                            stopRepeatingTask()
                            playicon?.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                            playtext?.setText("Play")
                            isplayloop = false

                        }
                    }
                }
                mHandler.postDelayed(this, INTERVAL)
            }
        }
        pagenum.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if(isEditing)
                {
                    if(s.toString().isNotEmpty())
                    {
                        var currentPage: Int = s.toString().toInt()
                        pdfView.jumpTo(currentPage)
                    }
                }


            }

            override fun afterTextChanged(s: Editable) {


            }
        }
        )

        mplaybutton.setOnClickListener { v -> // Send a message using content of the edit text widget
            Log.d("Checking", "HELLO WORLD")
            if (isplay) {
                isplay = true
                Log.d("Main", "Play_Arrow")
                (v as ImageButton).setImageResource(R.drawable.ic_baseline_pause_24)
                startRepeatingTask()

//                val intent = Intent(this, WebViewActivity::class.java)
//                startActivity(intent)

            } else {
                (v as ImageButton).setImageResource(R.drawable.ic_baseline_play_arrow_24)
                isplay = false
                stopRepeatingTask()
            }
        }
        pdfviews.setOnClickListener {

            recycler_view_pdf.visibility = View.VISIBLE

        }
        play?.setOnClickListener {
            bottomSheetDialog.cancel()
            if (isplay) {
                mpreviousbutton.isClickable = false
                mpreviousbutton.isEnabled = false
                editpagenum.keyListener = null;

                mnextbutton.isClickable = false
                mnextbutton.isEnabled = false
                isplayloop = true
                Log.d("Main", "Play_Arrow")
                playicon?.setImageResource(R.drawable.ic_baseline_pause_24)
                playtext?.setText("Pause")
                startRepeatingTask()
                isplay = false
//                val intent = Intent(this, WebViewActivity::class.java)
//                startActivity(intent)

            } else {
                mpreviousbutton.isClickable = true
                mpreviousbutton.isEnabled = true

                mnextbutton.isClickable = true
                mnextbutton.isEnabled = true
                editpagenum.keyListener = variable
                isplayloop = false
                playicon?.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                playtext?.setText("Play")
                stopRepeatingTask()
                isplay = true
//                sendControlMessage("rt~Pause")
            }

        }
        downloaded?.setOnClickListener {

            this.openCloseNavigationDrawer(it, "Download")
            bottomSheetDialog.cancel()

        }
        uploadLinearLaySout?.setOnClickListener {

            this.openCloseNavigationDrawer(it, "upload")
            bottomSheetDialog.cancel()
        }
        menus.setOnClickListener {

            showBottomSheetDialog()

        }
        //object: View.OnClickListener {..}
        save_settings.setOnClickListener(
            object: View.OnClickListener {
                override fun onClick(p0: View?) {
                    if(imagesAdapter.getSelectedPages().isEmpty())
                    {
                        Toast.makeText(this@PdfViewActivity, "No page is selected", Toast.LENGTH_LONG).show()
                    }
                    else
                    {
                        pages = imagesAdapter.getSelectedPages()
                        imagefiles = imagesAdapter.getList()
                        selectedpages = Selected_PDF_Model(fileName, pages, imagefiles)
                        showDialogsavesetting(this@PdfViewActivity,"Save Changes", "Are you sure you want to save changes?",selectedpages, fileName)
                        Log.e("pref", imagesAdapter.getList().toString())
                    }
                }

            }
        )

        mpreviousbutton.setOnClickListener {
            if(TextUtils.isEmpty(currentpages.text.toString()))
            {
                currentpages.setText(currentpagenum.toString());
            }
            else
            {
                if(pdfView != null)
                {
                    if(currentpages.text.toString().toInt() > 1)
                    {
                        pageChangeFromButton(false, currentpages.text.toString().toInt())
                    }
//                sendControlMessage("Previous")
                }
            }

             }
        mnextbutton.setOnClickListener {
            if(pdfView != null)
            {
                if(TextUtils.isEmpty(currentpages.text.toString()))
                {
                    currentpages.setText(currentpagenum.toString());
                }
                else
                {
                    if(currentpages.text.toString().toInt() < pageCount)
                    {
                        pageChangeFromButton(true, currentpages.text.toString().toInt())
                    }
                }

//                sendControlMessage("Next")
            }}





        var selectedpagesref =  easyPreference.getString(GH.KEYS.SELECTEDPAGES.name, null)
        if(selectedpagesref == null)
        {
            imagesAdapter = PdfImagesAdapter(this@PdfViewActivity, photos, select_all_checkbox, pdfFile, false)
            select_all_checkbox.isChecked = true
            imagesAdapter.selectAll()
            displayFromUri(pdfFile.toUri())
            var Filenamecommmand = "rt~v~$fileName"
            sendControlMessage(Filenamecommmand)
        }
        else
        {

                val gson = Gson()
                // getting data from gson and storing it in a string.
                // getting data from gson and storing it in a string.
                Log.e("Checkingsaverefmodelretrieve", selectedpagesref.toString())
                val collectionType = object : TypeToken<ArrayList<Selected_PDF_Model>>() {}.type
                text = gson.fromJson(selectedpagesref, collectionType)
                var index = -1

                for (i in 0 until text.size)
                {
                    if(text.get(i).filename.equals(fileName, true))
                    {
                        index = i
                        break
                    }
                }
//                Log.e("Checkingpreferencefilename-------> ", text.get(i).toString())

                Log.e("Index", index.toString())
                if (index >= 0 )
                {  imagesAdapter = PdfImagesAdapter(this@PdfViewActivity, text.get(index).imagefile, select_all_checkbox, pdfFile, true)
                    for(items in text.get(index).imagefile)
                    {
                        positionarray.add(items.positionOfPage)
                    }
                    val arrayList = imagesAdapter.getSelectedPages().toCollection(ArrayList())
                    select_all_checkbox.isChecked = imagesAdapter.getcheckchange()
                    displayFromUriSharepreference(pdfFile.toUri(), imagesAdapter.getSelectedPages())
                    var Filenamecommmand = "rt~v~$fileName"
//                    sendControlMessage(Filenamecommmand + Constants.SEPARATE  +  arrayList + Constants.SEPARATE  + positionarray)
                }

                else
                {
                    imagesAdapter = PdfImagesAdapter(this@PdfViewActivity, photos, select_all_checkbox, pdfFile, false)
                    select_all_checkbox.isChecked = true
                    imagesAdapter.selectAll()
                    displayFromUri(pdfFile.toUri())
                }




        }
        recycler_view_selectedpdf.visibility= View.VISIBLE
        recycler_view_selectedpdf.setLayoutManager(LinearLayoutManager(this@PdfViewActivity,LinearLayoutManager.VERTICAL, false))
        recycler_view_selectedpdf.setHasFixedSize(true)
        recycler_view_selectedpdf.setAdapter(imagesAdapter)
        val callback: ItemTouchHelper.Callback = Itemtouch(imagesAdapter)
        val helper = ItemTouchHelper(callback)
        helper.attachToRecyclerView(recycler_view_selectedpdf)

        Log.e("COMPLETED", "COMPLETED")
        select_page.setOnClickListener {
            // llProgressBar.visibility = View.VISIBLE
            displayFromUri(pdfFile.toUri())

        }

        select_all_checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            when {
                buttonView.isPressed -> {
                    if (isChecked) {
                        //Do Whatever you want in isChecked
                        imagesAdapter.selectAllindexs()
                    } else {
                        imagesAdapter.unSelectAll()
                    }
                }
            }
        }

        button_hide_show.setOnClickListener {
//            if (expandable_layout_0.isExpanded()) {
//                button_hide_show.setImageDrawable(getDrawable(R.drawable.up_arrow))
//                //button_hide_show.text = "Show"
//                expandable_layout_0.collapse()
//            }  else {
//                // button_hide_show.text = "Hide"
//                button_hide_show.setImageDrawable(getDrawable(R.drawable.down_arrow))
//                expandable_layout_0.expand()
//
//            }
        }
        back_page_button.setOnClickListener{
            if(pdfView != null)
            {
//                pageChangeFromButton(false)
            }
        }
        next_page_button.setOnClickListener{
            if(pdfView != null)
            {
//                pageChangeFromButton(true)
            }
        }
        recycler_view_pdf.visibility = View.VISIBLE
        fetchBrouchersFromLocalDb()
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(receiver, IntentFilter("Custom Message Receive"))
    }


    override fun onDestroy() {
        super.onDestroy()
        stopRepeatingTask()
        unregisterReceiver(receiver)
        isplayloop = false
    }
    fun startRepeatingTask() {
        mHandlerTask.run()
    }

    fun stopRepeatingTask() {
        mHandler.removeCallbacks(mHandlerTask)
    }

    fun showDialogsavesetting(context: Context, title: String, message: String, models: Selected_PDF_Model, filename : String) {
        var match: Boolean = false
        try {
            AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message) // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes) { dialog, which ->
                    val gson = Gson()

                    if(text != null)
                    {
                        
                        for (i in 0 until text.size)
                        {
                            if(text.get(i).filename.equals(filename, true))
                            {
                                text[i] = models;
                                match = true
                            }
                        }

                    }

                    if(!match)
                    {
                        text.add(models)
                    }
                    val json = gson.toJson(text)
                    easyPreference.addString(GH.KEYS.SELECTEDPAGES.name, json).save()
                    Toast.makeText(this@PdfViewActivity, "Save Successfully", Toast.LENGTH_LONG).show()
                    dialog.dismiss()
                } // A null listener allows the button to dismiss the dialog and take no further action.
//                .setNegativeButton(android.R.string.no, null)
                //.setIcon(R.drawable.ic_reason_24)
                .setNegativeButton(android.R.string.no) { dialog, which ->
                    dialog.dismiss()
                } //
                .show()
        } catch (e: Exception) {

        }
    }

    private fun showBottomSheetDialog() {

        bottomSheetDialog.show()
    }
    fun openCloseNavigationDrawer(view: View, state: String) {
        if(state == "Download")
        {
            if (drawer.isDrawerOpen(GravityCompat.END)) {
                drawer.closeDrawer(GravityCompat.END)
            } else {
                drawer.openDrawer(GravityCompat.END)
                recycler_view_pdf.visibility = View.VISIBLE
            }
        }
        else
        {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START)
            } else {
                drawer.openDrawer(GravityCompat.START)
            }
        }
    }
    private var receiver: BroadcastReceiver =object: BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            if(!TextUtils.isEmpty(p1!!.getStringExtra("message"))){
                var readmessage = p1!!.getStringExtra("message")
            }
        }

    }

    private val viewBrouchers: (Data) -> Unit = {
        val filePath = it.filesPath
        val fileName = filePath.substring(filePath.lastIndexOf("\\") + 1)

                if (File(applicationContext.filesDir.absolutePath + "/" + fileName).exists()) {
                    if (AppUtils.isPDF(fileName)) {
                        val intent = Intent(this, PdfViewActivity::class.java)
                        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.putExtra("File_Name", fileName)
                        finish();
                        startActivityForResult(intent, 600)
                        var Filenamecommmand: String  = "rt~v~" + fileName
//                        Toast.makeText(this@PdfViewActivity, Filenamecommmand, Toast.LENGTH_LONG).show()
                        sendControlMessage(Filenamecommmand)

                        viewModel.setSelectedPDFInfo(it)

                    } else {
                        AppUtils.showDialog(this, "Alert", "View for this file format is not available.")
                    }
                } else {
                    AppUtils.showDialog(this, "Alert", it.productName + " file not found.")
                }



    }


    private fun sendControlMessage(s: String) {
        var intent =Intent()
        intent.setAction("Custom Bluetooth")
        intent.putExtra("message",s)
        sendBroadcast(intent)
    }


    fun fetchBrouchersFromLocalDb() {
        var getallfiles = Search_Dir()
        var newArray: ArrayList<Data>
        filteredlist.clear()
        val getcurrentfile:String = intent.getStringExtra("File_Name").toString()
        viewModel.getFromDb().observe(this) {

            var data = viewModel.convertToBrochure(it,this@PdfViewActivity,applicationContext.filesDir.absolutePath)
            if (data.isNotEmpty()) {

                if (getallfiles != null) {
                        data.forEach {
                            val file = File(it.filesPath)
                            val fileName = file.path.split("\\").toTypedArray().last()
                            for(files in getallfiles)
                            {
                                if(files.name == fileName && getcurrentfile != files.name)
                                {

                                        filteredlist.add(it)

                                }
                            }

                        }

                }
                else
                {
                    Toast.makeText(this@PdfViewActivity, "No PDF Exist", Toast.LENGTH_LONG).show()
                    return@observe
                }

                if(filteredlist.isEmpty())
                {
                    Nopdfavialable.visibility = View.VISIBLE
                }
                else
                {
                    Nopdfavialable.visibility = View.GONE
                    recycler_view_pdf.apply {
                        newArray = viewModel.map(applicationContext.filesDir.absolutePath, filteredlist)
                        recycler_view_pdf.setLayoutManager(LinearLayoutManager(this@PdfViewActivity,LinearLayoutManager.VERTICAL, false))
                        recycler_view_pdf.setHasFixedSize(true)
                        adapter = downloadedbrochure(
                            this@PdfViewActivity,
                            newArray,
                            viewBrouchers,
                        )

                    }
                }


            } else {
                AppUtils.showDialog(this, "Alert", "No Record Found")
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    @kotlin.Throws(java.lang.Exception::class, java.lang.IllegalStateException::class)
    open fun pdfToBitmap(pdfFile: java.io.File?): java.util.LinkedList<PageSelected> {
        if (pdfFile == null || pdfFile.exists() == false) {
            throw java.lang.IllegalStateException("PDF File Does Not Exist")
        }
        var bitmaps: LinkedList<PageSelected> = LinkedList<PageSelected>()
        //  var bitmaps: java.util.LinkedList<android.graphics.Bitmap> =
        //    java.util.LinkedList<android.graphics.Bitmap>()
        try {
            var renderer: android.graphics.pdf.PdfRenderer? = android.graphics.pdf.PdfRenderer(
                android.os.ParcelFileDescriptor.open(
                    pdfFile,
                    android.os.ParcelFileDescriptor.MODE_READ_ONLY
                )
            )
            var bitmap: android.graphics.Bitmap
           pageCount = renderer!!.getPageCount()
            for (i in 0 until pageCount) {
                var page: android.graphics.pdf.PdfRenderer.Page? = renderer.openPage(i)
                var width: Int = page!!.getWidth()
                var height: Int = page!!.getHeight()

                /* FOR HIGHER QUALITY IMAGES, USE:
                int width = context.getResources().getDisplayMetrics().densityDpi / 72 * page.getWidth();
                int height = context.getResources().getDisplayMetrics().densityDpi / 72 * page.getHeight();
                */bitmap = android.graphics.Bitmap.createBitmap(
                    width,
                    height,
                    android.graphics.Bitmap.Config.ARGB_8888
                )
                page!!.render(
                    bitmap,
                    null,
                    null,
                    android.graphics.pdf.PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                )
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos) //bm is the bitmap object

                val b: ByteArray = baos.toByteArray()
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
                    val encoder: Base64.Encoder = Base64.getEncoder()
                    val encoded: String = encoder.encodeToString(b)
                    bitmaps!!.add(PageSelected(encoded,false,0))
                } else{
                    val encode = android.util.Base64.encodeToString(b, android.util.Base64.DEFAULT);
                    bitmaps!!.add(PageSelected(encode,false,0))
                }

                // close the page
                page.close()
            }
            // close the renderer
            renderer!!.close()
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
        return bitmaps!!
    }



    private fun displayFromUriSharepreference(uri: Uri, pagenum: IntArray) {

        pages   = pagenum

        if(pages.isNotEmpty())
        {
            pdfView.fromUri(uri)
                .pages(*pages)
                .defaultPage(0)
                .onPageChange(this)
                .enableSwipe(true)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .enableAnnotationRendering(true)
                .onError(this)
                .load()
        }

    }

        fun displayFromUri(uri: Uri) {
        pages   = imagesAdapter.getSelectedPages()

        if(pages.isNotEmpty())
        {
            pdfView.fromUri(uri)
                .pages(*pages)
                .defaultPage(0)
                .onPageChange(this)
                .enableSwipe(true)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .enableAnnotationRendering(true)
                .onError(this)
                .load()
        }


    }

    override fun onPageChanged(page: Int, pageCount: Int) {
        isEditing = false
        if(!isEditing)
        {
            currentpages.setText(page.toString());
        }
        isEditing = true
        Log.e("Umair", "Cannot load page $page")
        var pagenumbers = "_rt~pgch~$page"
//        Toast.makeText(this@PdfViewActivity, pagenumbers, Toast.LENGTH_LONG).show()
        recentpage = page
        sendControlMessage(pagenumbers)

    }

    override fun loadComplete(nbPages: Int) {
        val meta = pdfView.documentMeta
        GH.getInstance().HideProgressDialog()
        //llProgressBar.visibility = View.GONE
        Log.e("Umair", "title = " + meta.title)
        Log.e("Umair", "author = " + meta.author)
        Log.e("Umair", "subject = " + meta.subject)
        Log.e("Umair", "keywords = " + meta.keywords)
        Log.e("Umair", "creator = " + meta.creator)
        Log.e("Umair", "producer = " + meta.producer)
        Log.e("Umair", "creationDate = " + meta.creationDate)
        Log.e("Umair", "modDate = " + meta.modDate)

        printBookmarksTree(pdfView.tableOfContents, "-")
    }

    fun printBookmarksTree(tree: List<PdfDocument.Bookmark>, sep: String) {
        for (b in tree) {
            Log.e("Umair", String.format("%s %s, p %d", sep, b.title, b.pageIdx))
            if (b.hasChildren()) {
                printBookmarksTree(b.children, "$sep-")
            }
        }
    }

    override fun onError( t: Throwable?) {
        Log.e("Umair", "Cannot load page $t")
        GH.getInstance().HideProgressDialog()
    }

    override fun onBackPressed() {
        stopRepeatingTask()
        var intent = Intent(this, DownloadBroucherActivity::class.java)
        // intent.putExtra("TIME_ARRAY", timeArray.toString())
        setResult(RESULT_OK, intent)
        sendControlMessage("rt~back")
        isplayloop = false
        super.onBackPressed()
        /* setResult(RESULT_OK, null)
         super.onBackPressed()*/
    }


    private fun pageChangeFromButton(isNext:Boolean, currentpagenum: Int)
    {
        isEditing = false
        if(imagesAdapter!= null)
        {

            // var pages   = imagesAdapter.getSelectedPages()

            if(isNext)
            {
                var currentPage =  currentpagenum
                currentPage++
                pdfView.jumpTo(currentPage)
                if(!isEditing)
                {
                    currentpages.setText(currentPage.toString())
                }

            }else{
                var currentPage =  currentpagenum
                currentPage--
                pdfView.jumpTo(currentPage)

                if(!isEditing)
                {
                    currentpages.setText(currentPage.toString())
                }

            }

        }
        isEditing = true
    }

    inner class MinMaxFilter() : InputFilter {
        private var intMin: Int = 0
        private var intMax: Int = 0

        // Initialized
        constructor(minValue: Int, maxValue: Int) : this() {
            this.intMin = minValue
            this.intMax = maxValue
        }

        override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dStart: Int, dEnd: Int): CharSequence? {
            try {
                val input = Integer.parseInt(dest.toString() + source.toString())
                if (isInRange(intMin, intMax, input)) {
                    return null
                }
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }
            return ""
        }

        // Check if input c is in between min a and max b and
        // returns corresponding boolean
        private fun isInRange(a: Int, b: Int, c: Int): Boolean {
            return if (b > a) c in a..b else c in b..a
        }
    }

    fun Search_Dir() : Array<out File>? {
        var f=File(applicationContext.filesDir.absolutePath + "/").listFiles()
        for (files in f){
            if(files.toString().contains(".pdf",true)) {
                Log.e("Files---------->", "${files.name}")
            }
        }
        return f
    }
}

