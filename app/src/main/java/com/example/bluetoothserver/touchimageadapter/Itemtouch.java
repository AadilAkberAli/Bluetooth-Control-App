package com.example.bluetoothserver.touchimageadapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bluetoothserver.file_download.adapter.PdfImagesAdapter;

public class Itemtouch extends ItemTouchHelper.SimpleCallback{
    PdfImagesAdapter recycleAdapter;

    public Itemtouch(PdfImagesAdapter recycleAdapter) {
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
        this.recycleAdapter = recycleAdapter;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        recycleAdapter.swap(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

}
