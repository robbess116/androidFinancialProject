package com.sproutonecard.rechargeandreward.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;

import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.sproutonecard.rechargeandreward.AppController;
import com.sproutonecard.rechargeandreward.R;
import com.sproutonecard.rechargeandreward.api.GetDataTask;
import com.sproutonecard.rechargeandreward.api.OnTaskCompleted;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class LoginActivity extends BaseActivity implements OnTaskCompleted {
    @BindView(R.id.editTextEmail) EditText emailEditText;

    @BindView(R.id.editTextPassword) EditText passwordEditText;
    private Button forgotPasswordBtn;
    private Dialog forgotPasswordDialog;
    private int onTaskCompletedFlag = 0;
    private String email, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        ButterKnife.bind(this);
    }
    private void initView(){
        forgotPasswordBtn = (Button)findViewById(R.id.forgotPasswordButton);
        forgotPasswordBtn.setPaintFlags(forgotPasswordBtn.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
//        Dialog Initialize
        forgotPasswordDialog = new Dialog(this);
        forgotPasswordDialog.setContentView(R.layout.dialog_forgot_password);
        Window dialogWindow = forgotPasswordDialog.getWindow();
        dialogWindow.setLayout(900, 800);
        dialogWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageButton closeDialogButton = (ImageButton)forgotPasswordDialog.findViewById(R.id.closeImageButton);
        closeDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgotPasswordDialog.hide();
            }
        });

        Button submitForgotPassword = (Button)forgotPasswordDialog.findViewById(R.id.submitButton_forgotPassword);

        submitForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isLoadingBase)return;
                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    // fetch data
                } else {
                    // display error
                    utility.showAlertDialog(LoginActivity.this, "Network Connection Error!");
                    return;
                }
                View focusView = null;
                EditText emailEditTextForgot = (EditText)forgotPasswordDialog.findViewById(R.id.emailEditText_forgot);
               emailEditTextForgot.setError(null);

                String email = emailEditTextForgot.getText().toString().trim();

                if(email.isEmpty()){
                    emailEditText.setError(getString(R.string.error_field_required));
                    focusView = emailEditText;
                    focusView.requestFocus();
                    return;
                }else if(!utility.isValidEmail(email)) {
                    emailEditText.setError(getString(R.string.correct_mail_required));
                    focusView = emailEditText;
                    focusView.requestFocus();
                    return;

                }
                String url = URL_SERVER + "/reset-password/";


                JSONObject jsonRequest = new JSONObject();
                utility.setStringToJSONObject(jsonRequest, EMAIL,              email);
                isLoadingBase = true;
                mProgressDialog.show();
                onTaskCompletedFlag = 1;

                mGetDataTask = new GetDataTask(url ,jsonRequest, LoginActivity.this, POST);
                mGetDataTask.execute();

            }
        });



    }

    @OnClick(R.id.submitButton)
    public void onClickSubmitButton(){
        if(isLoadingBase)return;
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
        } else {
            // display error
            utility.showAlertDialog(this, "Network Connection Error!");
            return;
        }
        View focusView = null;
        emailEditText.setError(null);
        passwordEditText.setError(null);
        email = emailEditText.getText().toString().trim();
        password = passwordEditText.getText().toString().trim();
        if(email.isEmpty()){
            emailEditText.setError(getString(R.string.error_field_required));
            focusView = emailEditText;
            focusView.requestFocus();
            return;
        }else if(!utility.isValidEmail(email)) {
            emailEditText.setError(getString(R.string.correct_mail_required));
            focusView = emailEditText;
            focusView.requestFocus();
            return;

        }else if(password.isEmpty()){
            passwordEditText.setError(getString(R.string.error_field_required));
            focusView = passwordEditText;
            focusView.requestFocus();
            return;
        }


        JSONObject jsonRequest = new JSONObject();
        utility.setStringToJSONObject(jsonRequest, USER_EMAIL,              email);
        utility.setStringToJSONObject(jsonRequest, USER_PASSWORD,           password);

        isLoadingBase = true;
        mProgressDialog.setMessage("Login...");
        mProgressDialog.show();
        onTaskCompletedFlag = 0;
        mGetDataTask = new GetDataTask(URL_MANUAL_LOGIN,jsonRequest, this, POST);
        mGetDataTask.execute();

    }

    @OnClick(R.id.newUserButton)
    public void onNewUser(){
        navToNextAndFinish(this, RegisterNewUserActivity.class);
    }

    @OnClick(R.id.forgotPasswordButton)
    public void onForgotPassword(){
        forgotPasswordDialog.show();

    }

    @Override
    public void onTaskSuccess(JSONObject jsonResponse) {

        isLoadingBase = false;
        mProgressDialog.dismiss();

        //JSONObject currentUser = utility.getJSONObjectFromJSONObject(jsonResponse, CURRENT_USER);
        //utility.setJSONObjectToSharedPreference(this, CURRENT_USER, currentUser);
        if(onTaskCompletedFlag == 0){
            utility.setJSONObjectToSharedPreference(this, CURRENT_USER, jsonResponse);
            AppController.currentUser = jsonResponse;
            forgotPasswordDialog.dismiss();
            navToNextAndFinish(LoginActivity.this, LandingPageActivity.class);

        }else if(onTaskCompletedFlag == 1){
            utility.showAlertDialog(this, "Please check your email!");
        }


    }

    @Override
    public void onTaskError(String msg) {
        isLoadingBase = false;
        mProgressDialog.dismiss();
        utility.showAlertDialog(this, msg);

    }



}
