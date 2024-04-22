package com.example.bluetoothserver.file_download.Utilities;

public interface CallbackInterface<T> {
    void onSuccess(T response);
    void onError(Throwable throwable);
}
