package com.example.bluetoothserver.Utilities;


import com.example.bluetoothserver.Login.LoginModel;
import com.example.bluetoothserver.Utilities.BaseModel;
import com.example.bluetoothserver.file_download.Networking.BroucherResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface ApiMethods {

    @FormUrlEncoded
    @POST("Authentication")
    Call<LoginModel> login(
            @Field("UserName") String UserName,
            @Field("Password") String Password);

    @GET("Authentication/ChangePassword")
    Call<BaseModel> ChangePassword(
            @Header("Authorization") String Token,
            @Query("OldPassword") String UserName,
            @Query("Password") String Password,
            @Query("ConfirmPassword") String ConfirmPassword,
            @Query("_EmployeeId") int EmployeeId
    );

    @GET("Common/GetProductFileList")
    Call<BroucherResponse> getBrochuresRequest(
            @Header("Authorization") String Token
    );

    @Streaming
    @GET
    Call<ResponseBody> downloadFile(@Url String fileUrl);

    @GET("Authentication/UpdateRecoveryEmail")
    Call<BaseModel> UpdateRecoveryEmail(
            @Header("Authorization") String Token,
            @Query("EmpId") String EmpId,
            @Query("EmailAddress") String EmailAddress);

    @GET("Authentication/PasswordSendEmail")
    Call<BaseModel> PasswordSendEmail(
            @Query("EmpId") int EmpId);
}