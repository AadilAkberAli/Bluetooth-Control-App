package com.example.bluetoothserver.Login;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.bluetoothserver.Utilities.EasyPreference;
import com.example.bluetoothserver.Utilities.GH;
import com.example.bluetoothserver.Utilities.NetworkController;
import com.example.bluetoothserver.R;
import com.example.bluetoothserver.Utilities.BluetoothService;
import com.example.bluetoothserver.databinding.ActivityLoginBinding;
import com.example.bluetoothserver.file_download.DownloadBroucherActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Login extends AppCompatActivity implements View.OnClickListener {

    Context context;
    ActivityLoginBinding bi;
    protected EasyPreference.Builder easyPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_login);
        context = Login.this;
        easyPreference = EasyPreference.with(this);
        initView();
    }

    private void initView() {

        // bi.appVersion.setText(BuildConfig.FLAVOR+" (TESTING) V " + BuildConfig.VERSION_NAME);
        bi.btnLogin.setOnClickListener(this);
        bi.btnForgotPassword.setOnClickListener(this);
    }

    private boolean validateAndLogin() {

        String userID = bi.edtUser.getText().toString();
        String password = bi.edtPass.getText().toString();

        if (userID.equals("") || password.equals("")) {
            bi.edtUser.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.background_error_edt));
            bi.edtPass.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.background_error_edt));
            Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.length() < 7) {
            showToastMessage("Invalid Password. Password length should be greater than 6");
        } else if (userID.length() < 6) {
            showToastMessage("Invalid User ID");
        } else {
            if (isInternetAvailable(context)) {
                callLogin(userID, password);
            } else {
                callOfflineLogin(userID, password);
            }

        }

        return true;
    }

    public boolean isInternetAvailable(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            //should check null because in airplane mode it will be null
            return (netInfo != null && netInfo.isConnected());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void callLogin(String userID, final String password) {

        GH.getInstance().ShowProgressDialog(Login.this);

        Call<LoginModel> call = NetworkController.getInstance().getInstance().getApiMethods(this).login(userID, password);
        call.enqueue(
                new Callback<LoginModel>() {
                    @Override
                    public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                        LoginModel loginModel = response.body();
                        GH.getInstance().HideProgressDialog();
                        if (loginModel.getSuccess()) {

                            if (password.equals("sami123")) {
//                                GH.getInstance().startActivityWithString(context, ChangePasswordActivity.class, password);
//                                Intent intent = new Intent(context,ChangePasswordActivity.class);
//                                easyPreference.addString(GH.KEYS.AUTHORIZATION.name(), "Bearer" + " " + loginModel.getData().getToken()).save();
//                                intent.putExtra("LOGIN_DATA",loginModel);
//                                intent.putExtra("data",password);
//
//                                startActivity(intent);

                            } else {

                                loginModel.getData().setPassword(password);
                                easyPreference.addObject(GH.KEYS.LOGIN_MODEL.name(), loginModel).save();
                                easyPreference.addString(GH.KEYS.AUTHORIZATION.name(), "Bearer" + " " + loginModel.getData().getToken()).save();
                                easyPreference.addString(GH.KEYS.PASSWORD.name(), password).save();

                                easyPreference.addString(GH.KEYS.LOGOUTCHECKINGDATE.name(), settingNumberOfDaysInCurrentDateTime(2)).save();
                                easyPreference.addInt(GH.KEYS.USERID.name(), loginModel.getData().getUserId()).save();
                                easyPreference.addString(GH.KEYS.RECOVERYEMAIL.name(), loginModel.getData().getEmail()).save();


                                //GH.getInstance().startActivity(context, DashboardActivity.class);

                                Intent intent = new Intent(context, DownloadBroucherActivity.class);
                                context.startActivity(intent);
                            }


                            finishAffinity();


                        } else {
                            GH.getInstance().HideProgressDialog();
                            Toast.makeText(context, loginModel.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginModel> call, Throwable t) {
                        GH.getInstance().HideProgressDialog();
                        Log.d("Error", t.getMessage() + "");
                        showToastMessage(t.getLocalizedMessage());
                    }
                }
        );
    }
    public String settingNumberOfDaysInCurrentDateTime(Integer numberOfDays){
        SimpleDateFormat sdfo = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        calendar.add(Calendar.DATE, numberOfDays);
        //calendar.add(Calendar.HOUR, numberOfDays);
        //calendar.add(Calendar.MINUTE, numberOfDays);
        return sdfo.format(calendar.getTime());
    }

    private void callOfflineLogin(String userID, final String password) {
        LoginModel userProfile = easyPreference.getObject(GH.KEYS.LOGIN_MODEL.name(), LoginModel.class);
        if (userProfile != null) {
            if (userID.equals(userProfile.getData().getUserName()) && userProfile.getData().getPassword().equals(password)) {
                //switchActivity(DashboardActivity.class);
                Intent intent = new Intent(this, DownloadBroucherActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(context, "Invalid Credentials", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void openForgotPasswordActivity() {
//        GH.getInstance().startActivity(LoginActivity.this, ForgotPasswordActivity.class);
    }
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnLogin:
                validateAndLogin();
                break;
            case R.id.btnForgotPassword:
                openForgotPasswordActivity();
                break;
        }
    }


}