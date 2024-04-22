package com.example.bluetoothserver.Utilities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.example.bluetoothserver.R;


public class GH {

    private static final GH ourInstance = new GH();
    ProgressDialog progressDialog;


    public static GH getInstance() {
        return ourInstance;
    }

    public GH() {
    }




    public enum KEYS {

        AUTHORIZATION,
        PASSWORD,
        LOGIN_MODEL,
        LOGOUTCHECKINGDATE,
        USERID,
        RECOVERYEMAIL,
        SELECTEDPAGES,

    }



    public void startActivityWithString(Context context, Class activity, String data) {

        Intent intent = new Intent(context, activity);
        intent.putExtra("data", data);
        context.startActivity(intent);
    }


    public String getAuthorization(Context context) {
        return EasyPreference.with(context).getString(KEYS.AUTHORIZATION.name(), null);
    }

    public void startActivity(Context context, Class activity) {

        Intent intent = new Intent(context, activity);
        context.startActivity(intent);
    }


    public ProgressDialog ShowProgressDialog(Context activity) {

        if (activity == null) {

        }

        if(((Activity) activity).isFinishing() && ((Activity) activity).isDestroyed()){
            return null;
        }

        if (progressDialog != null && progressDialog.isShowing()) {
        } else {

            try{
                progressDialog = new ProgressDialog(activity);
                progressDialog.setMessage("Please Wait.....");
                progressDialog.setCancelable(false);
                progressDialog.show();
            } catch (Exception e){
                e.printStackTrace();
            }


        }

        return progressDialog;
    }

    public void HideProgressDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (final Exception e) {
            // Handle or log or ignore
            e.printStackTrace();
        } finally {
            this.progressDialog = null;
        }

    }

    public void setError(Context context, View view) {
        view.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.background_error_edt));
    }

    public void setEnable(Context context, View view) {
        view.setClickable(true);
        view.setEnabled(true);
        view.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.background_edt));
    }










}
