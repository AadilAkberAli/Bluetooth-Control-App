package com.example.bluetoothserver.file_download

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.text.HtmlCompat
import com.example.bluetoothserver.Broucher.Data
import com.example.bluetoothserver.R
import java.text.SimpleDateFormat
import java.util.*

class AppUtils {

    companion object {
        fun isTimeAutomatic(c: Context): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                Settings.Global.getInt(c.contentResolver, Settings.Global.AUTO_TIME, 0) == 1
            } else {
                Settings.System.getInt(c.contentResolver, Settings.System.AUTO_TIME, 0) == 1
            }
        }
        fun showDialog(context: Context, title: String, message: String) {
            try {
                AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(message) // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.ok) { dialog, which ->
                        // Continue with delete operation
                        dialog.dismiss()
                    } // A null listener allows the button to dismiss the dialog and take no further action.
//                .setNegativeButton(android.R.string.no, null)
                    //.setIcon(R.drawable.ic_reason_24)
                    .show()
            } catch (e: Exception) {

            }
        }
        @RequiresApi(Build.VERSION_CODES.N)
        fun showCustomDialog(
            context: Context,
            title: String,
            message: String
        ) {

            val viewGroup: ViewGroup = (context as Activity).findViewById(android.R.id.content)
            var view = LayoutInflater.from(context).inflate(
                R.layout.check_version_custon_dialog,
                viewGroup,
                false
            )
            var textTitle = view.findViewById<TextView>(R.id.textTitle)
            var textMsg = view.findViewById<TextView>(R.id.textMsg)
            var buttonOk = view.findViewById<Button>(R.id.buttonOk)

            textTitle.text = title

            textMsg.isClickable = true
            textMsg.movementMethod = LinkMovementMethod.getInstance()


            textMsg.text = Html.fromHtml(message, HtmlCompat.FROM_HTML_MODE_LEGACY)

//            buttonOk.visibility = View.GONE
            buttonOk.setOnClickListener{
//                Toast.makeText(context, "downloading...", Toast.LENGTH_SHORT).show()


            }

            AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(false)
//                .setPositiveButton("Update app", okButtonListener)
                /*.setTitle(title)
                .setMessage(message)

             .setPositiveButton(android.R.string.ok, okButtonListener)  // A null listener allows the button to dismiss the dialog and take no further action.
            .setNegativeButton(android.R.string.cancel){ dialog, which ->

                dialog.dismiss()
            }*/.show()
        }
        fun isPDF(file: String?): Boolean {
            if (file?.substring(file?.lastIndexOf(".")!! + 1).equals("pdf", true)) {
                return true
            }

            return false
        }

        fun settingExpiryMergedFile(mergedFilesArray: ArrayList<Data>):String
        {
            var minExpiryDate = ""
            val sdfo = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")//2022-01-05T00:00:00
            for( item in mergedFilesArray)
            {
                if(minExpiryDate.equals(""))
                {
                    minExpiryDate = item.expiryDate
                }else{
                    val firstFileDate: Date? = sdfo.parse(minExpiryDate)
                    val secondFileDate: Date? = sdfo.parse(item.expiryDate)
                    if (firstFileDate?.compareTo(secondFileDate)!! > 0) {
                        minExpiryDate = item.expiryDate
                    }

                }
            }


            return minExpiryDate
        }

        fun showDateTimeAutoUpdateDialog(context: Context, title: String, message: String) {
            AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message) // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
//                    .setPositiveButton(R.string.label_cancel) { dialog, which ->
//                        // Continue with delete operation
//                        dialog.dismiss()
//                    } // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton("Settings") { dialog, which ->
                    // Continue with delete operation
                    dialog.dismiss()
                    (context as Activity).startActivityForResult(
                        Intent(Settings.ACTION_DATE_SETTINGS),
                        0
                    )

                }
//                    .setIcon(R.drawable.ic_reason_24)
                .setCancelable(false)
                .show()
        }
    }
}
