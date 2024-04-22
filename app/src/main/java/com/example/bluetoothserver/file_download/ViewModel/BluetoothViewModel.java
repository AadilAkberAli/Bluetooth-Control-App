package com.example.bluetoothserver.file_download.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BluetoothViewModel extends ViewModel {

    private static BluetoothViewModel viewModel;
    private static MutableLiveData<Integer> state;
    private static MutableLiveData<String> message;
    public static BluetoothViewModel getInstance() {
        if(viewModel==null){
            viewModel=new BluetoothViewModel();
            state=new MutableLiveData<Integer>(-1);
            message=new MutableLiveData<String>("");
        }
        return viewModel;
    }
    private BluetoothViewModel() {
    }

    public LiveData<Integer> getState(){
        return state;
    }

    public void setState(int state){
        this.state.postValue(state);
    }

    public LiveData<String> getMessage(){
        return message;
    }

    public void setMessage(String message){
        this.message.postValue(message);
    }
}
