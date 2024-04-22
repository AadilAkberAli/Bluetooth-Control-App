package com.example.bluetoothserver.file_download.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.bluetoothserver.Broucher.Data
import com.example.bluetoothserver.R
import com.example.bluetoothserver.Utilities.EasyPreference
import com.example.bluetoothserver.Utilities.GH
import com.example.bluetoothserver.file_download.Controller.BluetoothController
import com.example.bluetoothserver.file_download.Utilities.MergeButtonHideAndShowHelper
import java.util.concurrent.CopyOnWriteArrayList

class BroucherAdapter (
    private val context: Context,
    private var easyPreference : EasyPreference.Builder,
    private var list: ArrayList<Data>,
    private val onDownloadClick: (Data) -> Unit,
    private val viewBroucher: (Data) -> Unit,
    private val deleteFile: (Data) -> Unit,
    private val onFavoriteClick: (Data) -> Unit,
) : MultiChoiceAdapter<BroucherAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater, parent)
    }

    fun add(data: ArrayList<Data>) {
        this.list = data
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position);
        val data = list[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int {
        return list.size
    }


    inner class ViewHolder(private val layoutInflater: LayoutInflater, val parent: ViewGroup) :
        RecyclerView.ViewHolder(layoutInflater.inflate(R.layout.downloaded_broucher_item, parent, false)) {

        private var productName: TextView? = null
        private var updatedDate: TextView? = null
        private var divisionName: TextView? = null
        private var groupName: TextView? = null
        private var btnDownload: LinearLayout? = null
        private var btnView: LinearLayout? = null
        private var btnDelete: AppCompatImageView? = null
        private var btnFavorite: AppCompatImageView? = null
        private var image: ImageView? = null

        init {
            productName = itemView.findViewById(R.id.productName)
            updatedDate = itemView.findViewById(R.id.updatedDate)
            divisionName = itemView.findViewById(R.id.divisionName)
            groupName = itemView.findViewById(R.id.groupName)
            btnDownload = itemView.findViewById(R.id.btnDownload)
            btnFavorite = itemView.findViewById(R.id.btnFavorite)
            btnView = itemView.findViewById(R.id.btnView)
            btnDelete = itemView.findViewById(R.id.btnDelete)
            image = itemView.findViewById(R.id.image)
        }

        fun bind(data: Data) {
            Log.d("CHECKING_NAME", data.productName);
            productName?.text = data.productName
            updatedDate?.text = ""//"Updated date"
            divisionName?.text = data.divisionName
            groupName?.text = data.groupName

            if (data.filesName.substring(data.filesName.lastIndexOf(".") + 1).equals("pdf", true)) {
                image?.setBackgroundResource(R.drawable.img_png)
            }

            if (data.exist) {
                btnDownload?.visibility = View.GONE
                btnView?.visibility = View.VISIBLE
                btnDelete?.visibility = View.VISIBLE
                btnFavorite?.visibility = View.VISIBLE

                if (data.isFavorite) {
                    btnFavorite?.setBackgroundDrawable(context.resources.getDrawable(R.drawable.ic_favorite_filler))
                } else {
                    btnFavorite?.setBackgroundDrawable(context.resources.getDrawable(R.drawable.ic_favorite_gray))
                }
            } else {
                btnDownload?.visibility = View.VISIBLE
                btnView?.visibility = View.GONE
                btnDelete?.visibility = View.GONE
                btnFavorite?.visibility = View.GONE
            }

            btnDownload?.setOnClickListener {
                onDownloadClick(data)
            }

            btnFavorite?.setOnClickListener {
                onFavoriteClick(data)
                data.isFavorite = !data.isFavorite

                /*if(data.isFavorite){
                    list.remove(data)
                    list.add(0,data)
                    notifyDataSetChanged()
                } else {
                    list.remove(data)
                    list.add(data)
                    notifyDataSetChanged()
                }*/

                val newArray = CopyOnWriteArrayList<Data>()
                newArray.addAll(list)

                for (brochure in newArray) {
                    if (brochure.isFavorite) {
                        newArray.remove(brochure)
                        newArray.add(0, brochure)
                    }

                    if(!brochure.exist){
                        newArray.remove(brochure)
                        newArray.add(brochure)
                    }

                    list = ArrayList(newArray)

                }

                notifyDataSetChanged()
            }

            btnDelete?.setOnClickListener {
                deleteFile(data)

            }

            btnView?.setOnClickListener {
                viewBroucher(data)
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

    public fun setMergeFile(multiChoiceToolbar: AppCompatActivity) {
        super.lists = list
        mMultiChoiceToolbarHelper = MergeButtonHideAndShowHelper(multiChoiceToolbar,list)
    }
}