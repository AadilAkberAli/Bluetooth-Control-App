package com.example.bluetoothserver.Utilities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BaseModel implements Serializable {

    @SerializedName("success")
    private Boolean success;
    @SerializedName("data")
    private Object data;
    @SerializedName("message")
    private String message;
    @SerializedName("detailsMessage")
    private Object detailsMessage;


    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getDetailsMessage() {
        return detailsMessage;
    }

    public void setDetailsMessage(Object detailsMessage) {
        this.detailsMessage = detailsMessage;
    }
}
