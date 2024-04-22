package com.example.bluetoothserver.Utilities;

import static com.example.bluetoothserver.Bluetoothchat.BluetoothChat.DEVICE_NAME;
import static com.example.bluetoothserver.Bluetoothchat.BluetoothChat.MESSAGE_DEVICE_NAME;
import static com.example.bluetoothserver.Bluetoothchat.BluetoothChat.MESSAGE_READ;
import static com.example.bluetoothserver.Bluetoothchat.BluetoothChat.MESSAGE_STATE_CHANGE;
import static com.example.bluetoothserver.Bluetoothchat.BluetoothChat.MESSAGE_TOAST;
import static com.example.bluetoothserver.Bluetoothchat.BluetoothChat.MESSAGE_WRITE;
import static com.example.bluetoothserver.Bluetoothchat.BluetoothChat.TOAST;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.bluetoothserver.Bluetoothchat.BluetoothChatService;
import com.example.bluetoothserver.Login.Login;
import com.example.bluetoothserver.R;
import com.example.bluetoothserver.file_download.ViewModel.BluetoothViewModel;

public class BluetoothService extends Service {
    public static final String CHANNEL_ID = "BluetoothServiceChannel";
    private final String TAG = this.getClass().getSimpleName();
    private BluetoothViewModel viewModel;
    private String mConnectedDeviceName = null;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothChatService mChatService = null;

    @Override
    public void onCreate() {
        super.onCreate();
        this.viewModel= BluetoothViewModel.getInstance();
        initBluetooth();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, Login.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Service")
                .setSmallIcon(R.drawable.ic_baseline_bluetooth_24)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private void initBluetooth(){
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
            // Otherwise, setup the chat session
        }

        // Initialize the BluetoothChatService to perform bluetooth connections
//        mChatService = new BluetoothChatService(this, mha);
    }
}
