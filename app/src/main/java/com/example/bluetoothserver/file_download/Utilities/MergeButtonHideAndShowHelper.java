package com.example.bluetoothserver.file_download.Utilities;


import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bluetoothserver.Broucher.Data;
import com.example.bluetoothserver.R;
import com.example.bluetoothserver.file_download.DownloadBroucherActivity;

import java.util.ArrayList;

public class MergeButtonHideAndShowHelper {

    private static final String TOOLBAR_ERROR_MESSAGE = "Toolbar not implemented via getSupportActionBar method";
    private static final String SELECTED_TOOLBAR_TITLE_FORMAT = "%d %s";
    private static final int ZERO = 0;


    private AppCompatActivity mAppCompatActivity;
    ArrayList<Data> list;

    public MergeButtonHideAndShowHelper(AppCompatActivity mAppCompatActivitys, ArrayList<Data> list) {

        mAppCompatActivity = mAppCompatActivitys;
        this.list =list;

    }

    public void updateToolbar(int itemNumber) throws IllegalStateException {
        if (itemNumber == ZERO) {
            showDefaultToolbar();
        } else if (itemNumber > ZERO) {
            showMultiChoiceToolbar();
        }
        updateToolbarTitle(itemNumber);
    }

    public void updateToolbar(int itemNumber,int position,boolean isAddOrRemove) throws IllegalStateException {
        if (itemNumber == ZERO) {
            showDefaultToolbar();
        } else if (itemNumber > ZERO) {
            showMultiChoiceToolbar();
        }
        updateToolbarTitle(itemNumber,position, isAddOrRemove);
    }

    private void showMultiChoiceToolbar() {

        Button btn = mAppCompatActivity.findViewById(R.id.btnDevices);
        btn.setVisibility(View.VISIBLE);
        /*mSupportActionBar.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        mSupportActionBar.setDisplayHomeAsUpEnabled(true);

        int multi_primaryColor = mMultiChoiceToolbar.getMultiPrimaryColor();
        if (multi_primaryColor != 0) {
            mSupportActionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(mAppCompatActivity, multi_primaryColor)));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = mAppCompatActivity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            int multiPrimaryColorDark = mMultiChoiceToolbar.getMultiPrimaryColorDark();
            if (multiPrimaryColorDark != 0) {
                window.setStatusBarColor(ContextCompat.getColor(mAppCompatActivity, multiPrimaryColorDark));
            }
        }

        mMultiChoiceToolbar.getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MultiChoiceToolbar.Listener listener = mMultiChoiceToolbar.getToolbarListener();
                if (listener != null) {
                    listener.onClearButtonPressed();
                }
            }
        });*/
    }

    private void showDefaultToolbar() {

        Button btn = mAppCompatActivity.findViewById(R.id.btnDevices);
        btn.setVisibility(View.GONE);
       /* showDefaultIcon();
        mSupportActionBar.setBackgroundDrawable(new ColorDrawable(mMultiChoiceToolbar.getDefaultPrimaryColor()));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = mAppCompatActivity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(mMultiChoiceToolbar.getDefaultPrimaryColorDark());
        }*/
    }


    private void updateToolbarTitle(int itemNumber) {

    }

    private void updateToolbarTitle(int itemNumber,int position,boolean isAddOrRemove) {
        DownloadBroucherActivity.Companion.addOrRemoveFiles(list.get(position),isAddOrRemove);
    }
}