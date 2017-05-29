package com.sproutonecard.rechargeandreward.ui.activity;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.sproutonecard.rechargeandreward.AppController;
import com.sproutonecard.rechargeandreward.R;
import com.sproutonecard.rechargeandreward.api.GetDataTask;
import com.sproutonecard.rechargeandreward.api.OnTaskCompleted;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterNewUserActivity extends BaseActivity implements OnTaskCompleted {
    @BindView(R.id.cardflagImageButton)ImageButton checkedImageButton;
    @BindView(R.id.oanNumberEditText)EditText oanEditText;
    @BindView(R.id.firstNameEditText_register)EditText firstNameEditText;
    @BindView(R.id.lastNameEditText_register)EditText lastNameEditText;
    @BindView(R.id.emailEditText_register)EditText emailEditText;
    @BindView(R.id.passwordEditText_register)EditText passwordEditText;
    @BindView(R.id.confirmPasswordEditText_register)EditText confirmPassEditText;
    @BindView(R.id.phoneNumberEditText_register)EditText phoneNumberEditText;
    private boolean cardFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new_user);
        ButterKnife.bind(this);
    }
    @OnClick(R.id.cardflagImageButton)
    public void checkedCard(){
        cardFlag = !cardFlag;
        if(cardFlag){
            checkedImageButton.setImageResource(R.drawable.ic_cardcheckedflag);
            oanEditText.setEnabled(false);
        }else {
            checkedImageButton.setImageResource(R.drawable.ic_cardflag);
            oanEditText.setEnabled(true);
        }
    }
    @OnClick(R.id.submitButton_register)
    public void onRegisterSubmit(){

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
        firstNameEditText.setError(null);
        lastNameEditText.setError(null);
        emailEditText.setError(null);
        passwordEditText.setError(null);
        confirmPassEditText.setError(null);
        phoneNumberEditText.setError(null);

        String firstname = firstNameEditText.getText().toString().trim();
        String lastname = lastNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPass = confirmPassEditText.getText().toString().trim();
        String phonenumber = phoneNumberEditText.getText().toString().trim();
        String oan = oanEditText.getText().toString().trim();
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
        }else if(confirmPass.isEmpty()){
            confirmPassEditText.setError(getString(R.string.error_field_required));
            focusView = confirmPassEditText;
            focusView.requestFocus();
            return;
        }else if(firstname.isEmpty()){
            firstNameEditText.setError(getString(R.string.error_field_required));
            focusView = firstNameEditText;
            focusView.requestFocus();
            return;
        }else if(lastname.isEmpty()){
            lastNameEditText.setError(getString(R.string.error_field_required));
            focusView = lastNameEditText;
            focusView.requestFocus();
            return;
        }else if(phonenumber.isEmpty()){
            phoneNumberEditText.setError(getString(R.string.error_field_required));
            focusView = phoneNumberEditText;
            focusView.requestFocus();
            return;
        }else if(!cardFlag){
            oanEditText.setError(null);
            if(oan.isEmpty()){
                oanEditText.setError(getString(R.string.error_field_required));
                focusView = oanEditText;
                focusView.requestFocus();
                return;
            }

        }else if(oan.length() !=14) {
            utility.showAlertDialog(this,"OAN lenth should be 14 digits!");
            oanEditText.setError(getString(R.string.error_field_required));
            focusView = oanEditText;
            focusView.requestFocus();
            return;

        }else if(oan.substring(0,1).equals("1")||oan.substring(0,1).equals("2")){
            AppController.firstOAN = oan;

            }else{
                utility.showAlertDialog(this,"First digit should be 1 or 2!");
                oanEditText.setError(getString(R.string.error_field_required));
                focusView = oanEditText;
                focusView.requestFocus();
                return;
            }


        String url = URL_SERVER + "/registration/";
        JSONObject jsonRequest = new JSONObject();
        utility.setStringToJSONObject(jsonRequest, USER_EMAIL,              email);
        utility.setStringToJSONObject(jsonRequest, USER_PASSWORD,           password);
        utility.setStringToJSONObject(jsonRequest, USER_FIRST_NAME,              firstname);
        utility.setStringToJSONObject(jsonRequest, USER_LAST_NAME,           lastname);
        utility.setStringToJSONObject(jsonRequest, MOBILE_PHONE,              phonenumber);

        isLoadingBase = true;
        mProgressDialog.setMessage("Sign Up...");
        mProgressDialog.show();

        mGetDataTask = new GetDataTask(url,jsonRequest, this, POST);
        mGetDataTask.execute();




    }
    @OnClick(R.id.cancelButton_register)
    public void onCancelRegister(){
        navToNextAndFinish(this,LoginActivity.class);
    }

    @Override
    public void onTaskSuccess(JSONObject jsonResponse) {
        isLoadingBase = false;
        mProgressDialog.dismiss();
        navToNextAndFinish(this,ConfirmPhonePinActivity.class);

    }

    @Override
    public void onTaskError(String msg) {
        isLoadingBase = false;
        mProgressDialog.dismiss();
        utility.showAlertDialog(this,msg);

    }
}
