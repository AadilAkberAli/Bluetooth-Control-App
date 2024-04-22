package com.example.bluetoothserver.Splashscreen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.bluetoothserver.Login.Login;
import com.example.bluetoothserver.Login.LoginModel;
import com.example.bluetoothserver.R;
import com.example.bluetoothserver.Utilities.BluetoothService;
import com.example.bluetoothserver.Utilities.EasyPreference;
import com.example.bluetoothserver.Utilities.GH;
import com.example.bluetoothserver.file_download.DownloadBroucherActivity;

public class SplashActivity extends Activity {

    LoginModel userProfile;
    int time = 2 * 1000;
    protected EasyPreference.Builder easyPreference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        easyPreference = EasyPreference.with(this);
        setContentView(R.layout.activity_splash);
        userProfile = easyPreference.getObject(GH.KEYS.LOGIN_MODEL.name(), LoginModel.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, BluetoothService.class));
        }else{
            startService(new Intent(this,BluetoothService.class));
        }
        new Handler().postDelayed(() -> {

            if (userProfile == null) {
                Intent intent = new Intent(this, Login.class);
                startActivity(intent);
            } else {


                Intent intent = new Intent(this, DownloadBroucherActivity.class);
                startActivity(intent);

            }
            finishAffinity();

        }, time);

    }

}