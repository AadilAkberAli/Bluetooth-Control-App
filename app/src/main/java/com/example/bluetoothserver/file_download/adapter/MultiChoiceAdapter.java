package com.example.bluetoothserver.file_download.adapter;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bluetoothserver.Broucher.Data;
import com.example.bluetoothserver.file_download.Utilities.MergeButtonHideAndShowHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public abstract class MultiChoiceAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private static final float DESELECTED_ALPHA = 1f;
    static final float SELECTED_ALPHA = 0.25f;
    private static final String EXTRA_ITEM_LIST = "EXTRA_ITEM_LIST";

    boolean mIsInMultiChoiceMode;
    boolean mIsInSingleClickMode;
    MergeButtonHideAndShowHelper mMultiChoiceToolbarHelper;
    private Map<Integer, State> mItemList = new LinkedHashMap<>();
    private Listener mListener = null;
    private RecyclerView mRecyclerView;
//list: ArrayList<Data>
    ArrayList<Data> lists;
    //region Public methods

    /**
     * Override this method to customize the active item
     *
     * @param view  the view to customize
     * @param state true if the state is active/selected
     */
    public void setActive(@NonNull View view, boolean state) {
        if (state) {
            view.setAlpha(SELECTED_ALPHA);
        } else {
            view.setAlpha(DESELECTED_ALPHA);
        }
    }

    /**
     * Provide the default behaviour for the item click with multi choice mode disabled
     *
     * @return the onClick action to perform when multi choice selection is off
     */
    protected View.OnClickListener defaultItemViewClickListener(VH holder, int position) {
        return null;
    }

    protected boolean isSelectableInMultiChoiceMode(int position) {
        return true;
    }

    /**
     * Deselect all the selected items in the adapter
     */
    public void deselectAll() {
        performAll(Action.DESELECT);
    }

    /**
     * Select all the view in the adapter
     */
    public void selectAll() {
        performAll(Action.SELECT);
    }

    /**
     * Select an item from the adapter position
     *
     * @param position adapter position of ther view which will be selected
     * @return True if the view has been selected, False if the view is already selected or is not part of the item list
     */
    public boolean select(int position) {
            if (mItemList.containsKey(position) && mItemList.get(position) == State.INACTIVE) {
            perform(Action.SELECT, position, true, true);
            return true;
        }
        return false;
    }

    /**
     * Deselect an item from the adapter position
     *
     * @param position adapter position of the view which will be deselected
     * @return True if the view has been deselected, False if the view is already deselected or is not part of the item list
     */
    public boolean deselect(int position) {
        if (mItemList.containsKey(position) && mItemList.get(position) == State.ACTIVE) {
            perform(Action.DESELECT, position, true, true);
            return true;
        }
        return false;
    }

    /**
     * Set the selection of the RecyclerView to always single click (instead of first long click and then single click)
     *
     * @param set true if single click sctivated
     */
    public void setSingleClickMode(boolean set) {
        mIsInSingleClickMode = set;
        processNotifyDataSetChanged();
    }


    /**
     * Method to get the number of selected items
     *
     * @return number of selected items
     */
    public int getSelectedItemCount() {
        return getSelectedItemListInternal().size();
    }


    /**
     * Get the list of selected item
     *
     * @return Collection of all the selected position in the adapter
     */
    public List<Integer> getSelectedItemList() {
        return getSelectedItemListInternal();
    }

    public void setMultiChoiceSelectionListener(Listener listener) {
        this.mListener = listener;
    }


    /**
     * @return true if the single click mode is active
     */
    public boolean isInSingleClickMode() {
        return mIsInSingleClickMode;
    }

    public void onSaveInstanceState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            savedInstanceState.putSerializable(EXTRA_ITEM_LIST, (Serializable) mItemList);
        }
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mItemList = (Map<Integer, State>) savedInstanceState.getSerializable(EXTRA_ITEM_LIST);

            int selectedListSize = getSelectedItemListInternal().size();

            updateMultiChoiceMode(selectedListSize);
            processNotifyDataSetChanged();
        }
    }

    //endregion

    //region Private methods
    List<Integer> getSelectedItemListInternal() {
        List<Integer> selectedList = new ArrayList<>();
        for (Map.Entry<Integer, State> item : mItemList.entrySet()) {
            if (item.getValue().equals(State.ACTIVE)) {
                selectedList.add(item.getKey());
            }
        }
        return selectedList;
    }

    private void processSingleClick(int position) {
        if (mIsInMultiChoiceMode || mIsInSingleClickMode) {
            processClick(position);
        }
    }

    private void processLongClick(int position) {
        if (!mIsInMultiChoiceMode && !mIsInSingleClickMode) {
            processClick(position);
        }
    }

    private void processUpdate(View view, int position) {
        if (mItemList.containsKey(position)) {
            if (mItemList.get(position).equals(State.ACTIVE)) {
                setActive(view, true);
            } else {
                setActive(view, false);
            }
        } else {
            mItemList.put(position, State.INACTIVE);
            processUpdate(view, position);
        }
    }

    private void processClick(int position) {
        if (mItemList.containsKey(position)) {
            if (mItemList.get(position).equals(State.ACTIVE)) {
                perform(Action.DESELECT, position, true, true);
            } else {
                perform(Action.SELECT, position, true, true);
            }
        }
    }


    public int getSelectedItemPositionOfList() {
        return positionOfData;
    }
    int positionOfData = 0;
    private void perform(Action action, int position, boolean withCallback, boolean withVibration) {
        positionOfData = position;
        boolean isAddRemove = false;
        if (action == Action.SELECT) {
            isAddRemove = true;
            mItemList.put(position, State.ACTIVE);
        } else {
            isAddRemove = false;
            mItemList.put(position, State.INACTIVE);
        }

        int selectedListSize = getSelectedItemListInternal().size();

        updateMergeFile(selectedListSize,position,isAddRemove);
        //updateMergeFile(selectedListSize);
        updateMultiChoiceMode(selectedListSize);

        processNotifyDataSetChanged();

        if (mListener != null && withCallback) {
            if (action == Action.SELECT) {
                mListener.OnItemSelected(position, selectedListSize, mItemList.size());
            } else {
                mListener.OnItemDeselected(position, selectedListSize, mItemList.size());
            }
        }
    }

    private void processNotifyDataSetChanged() {
        if (mRecyclerView != null) {
            notifyDataSetChanged();
        }
    }



    private void updateMultiChoiceMode(int selectedListSize) {
        boolean somethingSelected = selectedListSize > 0;
        if (mIsInMultiChoiceMode != somethingSelected) {
            mIsInMultiChoiceMode = somethingSelected;
            processNotifyDataSetChanged();
        }
    }

    private void updateMergeFile(int selectedListSize) {
        if ((mIsInMultiChoiceMode || mIsInSingleClickMode || selectedListSize > 0) && mMultiChoiceToolbarHelper != null) {

            mMultiChoiceToolbarHelper.updateToolbar(selectedListSize);

        }
    }

    private void updateMergeFile(int selectedListSize,int position,boolean state) {
        if ((mIsInMultiChoiceMode || mIsInSingleClickMode || selectedListSize > 0) && mMultiChoiceToolbarHelper != null) {


            mMultiChoiceToolbarHelper.updateToolbar(selectedListSize,position,state);

        }
    }

    private void performAll(Action action) {


        int selectedItems;
        State state;
        if (action == Action.SELECT) {
            selectedItems = mItemList.size();
            state = State.ACTIVE;
        } else {
            selectedItems = 0;
            state = State.INACTIVE;
        }

        for (Integer i : mItemList.keySet()) {
            mItemList.put(i, state);
        }



        updateMultiChoiceMode(selectedItems);

        processNotifyDataSetChanged();

        if (mListener != null) {
            if (action == Action.SELECT) {
                mListener.OnSelectAll(getSelectedItemListInternal().size(), mItemList.size());
            } else {
                mListener.OnDeselectAll(getSelectedItemListInternal().size(), mItemList.size());
            }
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;

        for (int i = 0; i < getItemCount(); i++) {
            mItemList.put(i, State.INACTIVE);
        }
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(final VH holder, final int position) {
        View mCurrentView = holder.itemView;

        if ((mIsInMultiChoiceMode || mIsInSingleClickMode) && isSelectableInMultiChoiceMode(position)) {
            mCurrentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(lists.get(holder.getAdapterPosition()).getExist())
                    {
                        processSingleClick(holder.getAdapterPosition());
                    }


                }
            });
        } else if (defaultItemViewClickListener(holder, position) != null) {
            mCurrentView.setOnClickListener(defaultItemViewClickListener(holder, position));
        }

        mCurrentView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {


                if(lists.get(holder.getAdapterPosition()).getExist())
                {
                    processLongClick(holder.getAdapterPosition());
                }
                return true;
            }
        });

        processUpdate(mCurrentView, holder.getAdapterPosition());
    }

    //endregion

    //region Package-Protected methods

    @VisibleForTesting
    void setItemList(LinkedHashMap<Integer, State> itemList) {
        mItemList = itemList;
    }

    //endregion

    private enum Action {
        SELECT,
        DESELECT
    }

    enum State {
        ACTIVE,
        INACTIVE
    }

    public interface Listener {

        void OnItemSelected(int selectedPosition, int itemSelectedCount, int allItemCount);

        void OnItemDeselected(int deselectedPosition, int itemSelectedCount, int allItemCount);

        void OnSelectAll(int itemSelectedCount, int allItemCount);

        void OnDeselectAll(int itemSelectedCount, int allItemCount);
    }
}
