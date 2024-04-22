package com.example.bluetoothserver.file_download.Utilities;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;

public class AppConstants {


    public static class HTTP {

        // Development Url

        // Live Url
//        public static final String BASE_URL_DEVELOPMENT ="http://10.0.0.155:9095/api/";
//        public static final String BASE_URL_LIVE ="http://10.0.1.27:7475/api/";

        public static final String BASE_API = "api/User/";
        public static final String GRANT_TYPE = "password";

    }

    public static final String APPROVE = "Approved";
    public static final String FORWARD = "Forward";
    public static final String REJECTED = "Rejected";
    public static final String PENDING = "Pending";



    public static class Keys{

        public static String UserID = "";
        public static final String UserName = "";

    }

    public static void setupUI(Activity activity, View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(activity);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(activity, innerView);
            }
        }
    }


    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);

        View focusedView = activity.getCurrentFocus();

        if (focusedView != null) {
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);
        }

    }

    public static void showToast(Context context, String s){
        Toast.makeText(context,s,Toast.LENGTH_SHORT).show();
    }

    public static boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isConnected() throws InterruptedException, IOException {
        String command = "ping -c 1 google.com";
        return Runtime.getRuntime().exec(command).waitFor() == 0;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    static int count=0;
    private static Handler handler;
    private static Runnable myRunnable;

    public static void signalShowing(Activity activity)
    {
        if(handler == null)
        {
            handler =  new Handler();
            if(myRunnable == null)
            {
                myRunnable = new Runnable() {
                    public void run() {
                        // Things to be done
                        /*Toast toast = Toast.makeText(activity, "Toast"+count++, Toast.LENGTH_SHORT);
                        toast.show();*/
                        handler.postDelayed(myRunnable, 10000);
                    }
                };
            }

            handler.postDelayed(myRunnable, 10000);
        }
    }


}
