package com.example.bluetoothserver.file_download.adapter


import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bluetoothserver.R
import com.example.bluetoothserver.file_download.PageSelected
import com.example.bluetoothserver.file_download.PdfViewActivity
import java.io.File
import java.util.*


class PdfImagesAdapter(var context:Context, var imagesFiles: LinkedList<PageSelected>, var isselectedcheckbox: CheckBox, var pdfFile: File, var checksharepref: Boolean) : RecyclerView.Adapter<PdfImagesAdapter.ViewHolder>() {

    var pageSelectedData:ArrayList<Int> = ArrayList()
    var ischecked = false
    inner class ViewHolder(inflater: LayoutInflater, private val parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.item_view_selection, parent, false)) {

        var imageView: ImageView? = null
        var checkBox: CheckBox? = null
        init {
            imageView = itemView.findViewById(R.id.image_view)
            checkBox = itemView.findViewById(R.id.selection)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(data: PageSelected, position: Int) {
            //holder.imageView.setImageBitmap(imagesFiless.get(position));
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
                val decoder: Base64.Decoder = Base64.getDecoder()
                val decoded = decoder.decode(data.image.toByteArray())
                imageView!!.setImageBitmap(BitmapFactory.decodeByteArray(decoded, 0, decoded.size))
            }
            else
            {
                val decoded = android.util.Base64.decode(data.image.toByteArray(), android.util.Base64.DEFAULT);
                imageView!!.setImageBitmap(BitmapFactory.decodeByteArray(decoded, 0, decoded.size))
            }
            checkBox!!.setOnCheckedChangeListener(null)
            checkBox!!.setChecked(data.isSelected)
            checkBox!!.setOnCheckedChangeListener{ buttonView, isChecked ->
                imagesFiles.get(position).isSelected = isChecked
                Log.e("checkimagefileoncheck", getList().toString())



//            if (isChecked) {
//
//                imagesFiles.get(position).positionOfPage = position
//                //pageSelectedData.add(position)
//
//            } else {
//               // imagesFiles.get(position).isSelected = isChecked
//                imagesFiles.get(position).positionOfPage = 0
//               // pageSelectedData.remove(position)
//            }

                isselectedcheckbox.isChecked = getcheckchange()
        }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater, parent)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data: PageSelected = imagesFiles[holder.adapterPosition]
               holder.bind(data,holder.adapterPosition)

    }

    override fun getItemCount(): Int = imagesFiles.size

    fun getList() : LinkedList<PageSelected>{
        return imagesFiles
    }


    fun getcheckchange() : Boolean
    {
        ischecked = getSelectedPages().size == itemCount
        return  ischecked
    }

    fun getSelectedPages() : IntArray  {
        pageSelectedData = ArrayList()
        for(item in imagesFiles)
        {
            Log.e("chekingoverall", item.positionOfPage.toString())
            if(item.isSelected)
            {
                pageSelectedData.add(item.positionOfPage)
                Log.e("Checkingselected", item.positionOfPage.toString())
            }
        }
        return pageSelectedData.toIntArray()
    }


    fun swap(firstPosition: Int, secondPosition: Int) {
//        var temp : Bitmap = imagesFiles.get(firstPosition).image
//        imagesFiles.get(firstPosition).image = imagesFiles.get(secondPosition).image
//        imagesFiles.get(secondPosition).image = temp
//
//        var tempcheck : Boolean = imagesFiles.get(firstPosition).isSelected
//        imagesFiles.get(firstPosition).isSelected = imagesFiles.get(secondPosition).isSelected
//        imagesFiles.get(secondPosition).isSelected = tempcheck

        Collections.swap(imagesFiles, firstPosition, secondPosition)
//        notifyDataSetChanged()
        notifyItemMoved(firstPosition, secondPosition)
        notifyItemChanged(firstPosition)
        notifyItemChanged(secondPosition)
        (context as PdfViewActivity).displayFromUri(pdfFile.toUri())
        Log.e("checkimagefile", getList().toString())
    }


    fun selectAll() {
        for ((item,index) in imagesFiles.withIndex()) {

            index.isSelected = true
            index.positionOfPage = item
        }
        Log.e("checkimagefile", getList().toString())
        notifyDataSetChanged()
    }

    fun selectAllindexs() {
        for ((item,index) in imagesFiles.withIndex()) {

            index.isSelected = true
        }
        Log.e("checkimagefile", getList().toString())
        notifyDataSetChanged()
    }




    fun unSelectAll() {
        for ((item,index) in imagesFiles.withIndex()) {

            index.isSelected = false
//            index.positionOfPage = item
        }
        Log.e("checkimagefile", getList().toString())
        notifyDataSetChanged()
    }

}