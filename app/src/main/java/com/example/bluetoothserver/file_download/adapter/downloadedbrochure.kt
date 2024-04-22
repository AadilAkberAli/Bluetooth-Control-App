package com.example.bluetoothserver.file_download.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.NonNull
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.bluetoothserver.Broucher.Data
import com.example.bluetoothserver.R

class downloadedbrochure (
    private val context: Context,
    private var list: ArrayList<Data>,
    private val viewBrouchers: (Data) -> Unit,
) : MultiChoiceAdapter<downloadedbrochure.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater, parent)
    }

    fun add(data: ArrayList<Data>) {
        this.list = data
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val data = list[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int {
        return list.size
    }


    inner class ViewHolder(private val layoutInflater: LayoutInflater, val parent: ViewGroup) :
        RecyclerView.ViewHolder(layoutInflater.inflate(R.layout.broucher_item, parent, false)) {
        private var btnDownload: LinearLayout? = null
        private  var cardconstraint: ConstraintLayout? = null
        private var productName: TextView? = null
        private var updatedDate: TextView? = null
        private var divisionName: TextView? = null
        private var groupName: TextView? = null
        private var btnView: LinearLayout? = null
        private var image: ImageView? = null
        private var btnDelete: AppCompatImageView? = null
        private var btnFavorite: AppCompatImageView? = null
        init {
            btnDownload = itemView.findViewById(R.id.btnDownload)
            productName = itemView.findViewById(R.id.productName)
            updatedDate = itemView.findViewById(R.id.updatedDate)
            divisionName = itemView.findViewById(R.id.divisionName)
            groupName = itemView.findViewById(R.id.groupName)
            btnView = itemView.findViewById(R.id.btnView)
            image = itemView.findViewById(R.id.image)
            btnFavorite = itemView.findViewById(R.id.btnFavorite)
            btnDelete = itemView.findViewById(R.id.btnDelete)
            cardconstraint = itemView.findViewById(R.id.cardconstraint)
        }

        fun bind(data: Data) {
            Log.d("CHECKING_NAME", data.productName)
            productName?.text = data.productName
            updatedDate?.text = ""//"Updated date"
            divisionName?.text = data.divisionName
            groupName?.text = data.groupName
            btnDelete?.visibility = View.GONE
            btnFavorite?.visibility = View.GONE
            btnDownload?.visibility = View.GONE
            btnView?.visibility = View.VISIBLE
            if (data.filesName.substring(data.filesName.lastIndexOf(".") + 1).equals("pdf", true)) {
                image?.setBackgroundResource(R.drawable.img_png)
            }

            if (data.exist) {
                btnView?.visibility = View.VISIBLE

            }

            else
            {
                btnView?.visibility = View.GONE
            }
            btnView?.setOnClickListener {
                viewBrouchers(data)
            }
        }
    }

    override fun setActive(@NonNull view: View, state: Boolean) {
        val relativeLayout = view.findViewById<View>(R.id.container) as RelativeLayout
        if (state) {
            relativeLayout.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.colorAccent
                )
            )
            // DownloadBroucherActivity.Companion.selectedFilesForMerge!!.add(list[selectedItemPositionOfList])

        } else {
            relativeLayout.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    android.R.color.transparent
                )
            )

            // DownloadBroucherActivity.Companion.selectedFilesForMerge!!.remove(list[selectedItemPositionOfList])

        }
    }

    override fun defaultItemViewClickListener(
        holder: ViewHolder?,
        position: Int
    ): View.OnClickListener? {
        return View.OnClickListener {
            /* Toast.makeText(
                 context,
                 "Click on item $position",
                 Toast.LENGTH_SHORT
             ).show()*/
        }
    }

}