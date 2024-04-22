package com.example.bluetoothserver.file_download.Controller;


import static com.example.bluetoothserver.Bluetoothchat.BluetoothChat.DEVICE_NAME;
import static com.example.bluetoothserver.Bluetoothchat.BluetoothChat.MESSAGE_DEVICE_NAME;
import static com.example.bluetoothserver.Bluetoothchat.BluetoothChat.MESSAGE_READ;
import static com.example.bluetoothserver.Bluetoothchat.BluetoothChat.MESSAGE_STATE_CHANGE;
import static com.example.bluetoothserver.Bluetoothchat.BluetoothChat.MESSAGE_TOAST;
import static com.example.bluetoothserver.Bluetoothchat.BluetoothChat.MESSAGE_WRITE;
import static com.example.bluetoothserver.Bluetoothchat.BluetoothChat.TOAST;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.bluetoothserver.Bluetoothchat.BluetoothChatService;
import com.example.bluetoothserver.file_download.ViewModel.BluetoothViewModel;




public class BluetoothController {
    private Context context;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothChatService mChatService;
    private BluetoothViewModel viewModel;
    private static BluetoothController controller;
    public static BluetoothController getInstance(Context context) {
        if(controller==null){
            controller=new BluetoothController(context);
        }
        return controller;
    }

    private BluetoothController(Context context) {
        this.context= context;
        this.viewModel= BluetoothViewModel.getInstance();

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(context, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            return;
        }
        start();
    }
    public void start(){
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
            // Otherwise, setup the chat session
        }
//        mChatService = new BluetoothChatService(context, viewModel);
    }

    public BluetoothViewModel getViewModel() {
        return viewModel;
    }

    public void stop(){
        if(mChatService!=null){
            mChatService.stop();
        }
    }


    public void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(context, "Not connected", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write

            mChatService.write(message.getBytes());

        }
    }


    public void resume(){
        if (mChatService != null) {// Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }
}

