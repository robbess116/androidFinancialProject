package com.sproutonecard.rechargeandreward.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sproutonecard.rechargeandreward.AppController;
import com.sproutonecard.rechargeandreward.R;
import com.sproutonecard.rechargeandreward.api.GetDataTask;
import com.sproutonecard.rechargeandreward.api.OnTaskCompleted;
import com.sproutonecard.rechargeandreward.model.SpinnerModel;
import com.sproutonecard.rechargeandreward.ui.adapter.CustomSpinnerAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ManageProfileActivity extends BaseActivity implements AdapterView.OnItemSelectedListener, OnTaskCompleted {
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private EditText mobileNumberEditText;
    private TextView userFullName;
    private TextView groupTextView;
    private Spinner charitySpinner;
    private Dialog changePasswordDiallog;
    private int onTaskCompletedFlag=0;
    private int charityIndex=-1;

    private ArrayList<SpinnerModel> charitySpinnerModels = new ArrayList<>();
    private CustomSpinnerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_profile);
        ButterKnife.bind(this);
        getCharities();
        initView();
    }
    private void getCharities(){
        String url = URL_SERVER + "/charity/";
        if(isLoadingBase)return;
            isLoadingBase = true;
            mProgressDialog.show();
            mGetDataTask = new GetDataTask(url,null,this, GET);
            onTaskCompletedFlag = 0;
            mGetDataTask.execute();



    }

    private void initView(){
        firstNameEditText = (EditText)findViewById(R.id.firstNameEditText);
        firstNameEditText.setText(AppController.first_name);
        lastNameEditText = (EditText)findViewById(R.id.lastNameEditText);
        lastNameEditText.setText(AppController.last_name);
        mobileNumberEditText = (EditText)findViewById(R.id.mobileEditText);
        emailEditText = (EditText)findViewById(R.id.emailEditText_manageProfile);
        groupTextView = (TextView)findViewById(R.id.groupNameTextView);
        charitySpinner = (Spinner)findViewById(R.id.charitySpinner);
        try {
            emailEditText.setText(AppController.currentUserDetails.getString(EMAIL));
            if(AppController.currentUserDetails.has(MOBILE_PHONE))
            mobileNumberEditText.setText(AppController.currentUserDetails.getString(MOBILE_PHONE));
            groupTextView.setText(AppController.currentUserDetails.getJSONObject(GROUP).getString(NAME));
        } catch (JSONException e) {
            e.printStackTrace();
        }


//       Dialog Initialize
        changePasswordDiallog = new Dialog(this);
        changePasswordDiallog.setContentView(R.layout.dialog_change_password);
        Window dialogWindow = changePasswordDiallog.getWindow();
        dialogWindow.setLayout(1000, 1000);
        dialogWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageButton closeDialogButton = (ImageButton)changePasswordDiallog.findViewById(R.id.closeImageButton);
        closeDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePasswordDiallog.dismiss();
            }
        });

        Button submitChangePassword = (Button)changePasswordDiallog.findViewById(R.id.submitButton_changePassword);
        submitChangePassword.setOnClickListener(new View.OnClickListener() {
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
                    utility.showAlertDialog(ManageProfileActivity.this, "Network Connection Error!");
                    return;
                }
                View focusView = null;
                EditText oldPasswordEditText = (EditText)changePasswordDiallog.findViewById(R.id.oldPasswordEditText);
                EditText newPasswordEditText = (EditText)changePasswordDiallog.findViewById(R.id.newPasswordEditText);
                EditText confirmNewPasswordEditText = (EditText)changePasswordDiallog.findViewById(R.id.confirmNewPasswordEditText);
                oldPasswordEditText.setError(null);
                newPasswordEditText.setError(null);
                confirmNewPasswordEditText.setError(null);
                String oldPassword = oldPasswordEditText.getText().toString().trim();
                String newPassword = newPasswordEditText.getText().toString().trim();
                String confirmNewPassword = confirmNewPasswordEditText.getText().toString().trim();
                if(oldPassword.isEmpty()){
                    oldPasswordEditText.setError(getString(R.string.error_field_required));
                    focusView = oldPasswordEditText;
                    focusView.requestFocus();
                    return;
                }else if(newPassword.isEmpty()){
                    newPasswordEditText.setError(getString(R.string.error_field_required));
                    focusView = newPasswordEditText;
                    focusView.requestFocus();
                    return;
                }else if(confirmNewPassword.isEmpty()){
                    confirmNewPasswordEditText.setError(getString(R.string.error_field_required));
                    focusView = confirmNewPasswordEditText;
                    focusView.requestFocus();
                    return;
                }else if(!newPassword.equals(confirmNewPassword)){
                    utility.showAlertDialog(ManageProfileActivity.this, "Password Invalid!");
                    confirmNewPasswordEditText.setError(getString(R.string.error_field_required));
                    focusView = confirmNewPasswordEditText;
                    focusView.requestFocus();
                    return;

                }
                JSONObject jsonRequest = new JSONObject();
                utility.setStringToJSONObject(jsonRequest, USER_OLD_PASSWORD, oldPassword);
                utility.setStringToJSONObject(jsonRequest, USER_NEW_PASSWORD, newPassword);
                String url = null;
                try {
                    url = URL_SERVER + AppController.currentUserDetails.getString(URL)+"/change-password/";
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                if(url == null) {
                    utility.showAlertDialog(ManageProfileActivity.this, "Server Error!");
                }else {
                    isLoadingBase = true;
                    mProgressDialog.show();
                    onTaskCompletedFlag =2;
                    mGetDataTask = new GetDataTask(url,jsonRequest, ManageProfileActivity.this, POST);
                    mGetDataTask.execute();
                }


           }
        });

    }

    /*********************
     * Method to set data in ArrayList
     **************************/
    private ArrayList<SpinnerModel> setSpinnerData() {
        final ArrayList<SpinnerModel> spinnerModels = new ArrayList<>();
        if(AppController.charities !=null)
        for (int i = 0; i < AppController.charities.length(); i++) {
            final SpinnerModel sched = new SpinnerModel();

            try {
                sched.setItemName(AppController.charities.getJSONObject(i).getString(NAME));
                if(AppController.currentUserDetails.has(CHARITY))
                if(AppController.charities.getJSONObject(i).getString(NAME).equals(AppController.currentUserDetails.getJSONObject(CHARITY).getString(NAME))){
                    charityIndex = i;

                }
                spinnerModels.add(sched);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        return spinnerModels;
    }

    /***********************
     * Method to load Model data
     *****************************/
    private void loadSpinner(final ArrayList<SpinnerModel> newSpinners) {
        if (newSpinners == null)
            return;
        if (this.adapter == null) {
            charitySpinner.setOnItemSelectedListener(this);
            adapter = new CustomSpinnerAdapter(getApplicationContext(), newSpinners, null);
            charitySpinner.setAdapter(adapter);
            charitySpinner.setSelection(charityIndex);
            try {
                if(!AppController.currentUserDetails.getBoolean(EDIT_CHARITY)){
                    charitySpinner.setClickable(false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @OnClick(R.id.cancelButton_manageProfile)
    public void onCancelManageProfile(){
        changePasswordDiallog.dismiss();
        navToNextAndFinish(this, ManageAccountActivity.class);
    }

    @OnClick(R.id.submitButton_manageProfile)
    public void onSubmitManageProfile(){

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
        mobileNumberEditText.setError(null);
        String email = emailEditText.getText().toString().trim();
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String mobilePhoneNumber = mobileNumberEditText.getText().toString().trim();
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

        }else if(firstName.isEmpty()){
            firstNameEditText.setError(getString(R.string.error_field_required));
            focusView = firstNameEditText;
            focusView.requestFocus();
            return;
        }else if(lastName.isEmpty()){
            lastNameEditText.setError(getString(R.string.error_field_required));
            focusView = lastNameEditText;
            focusView.requestFocus();
            return;
        }else if(mobilePhoneNumber.isEmpty()){
            mobileNumberEditText.setError(getString(R.string.error_field_required));
            focusView = mobileNumberEditText;
            focusView.requestFocus();
            return;
        }


        JSONObject jsonRequest = new JSONObject();
        if(!firstName.equals(AppController.first_name)){
            utility.setStringToJSONObject(jsonRequest, USER_FIRST_NAME,           firstName);
        }else if(!lastName.equals(AppController.last_name)){
            utility.setStringToJSONObject(jsonRequest, USER_LAST_NAME,             lastName);
        }
        try {
            if(!email.equals(AppController.currentUserDetails.getString(EMAIL))){
                utility.setStringToJSONObject(jsonRequest, USER_EMAIL,              email);
            }

        }catch (Exception e){
            utility.setStringToJSONObject(jsonRequest, USER_EMAIL,              email);
        }



        try{
            if(!mobilePhoneNumber.equals(AppController.currentUserDetails.getString(MOBILE_PHONE))){

                utility.setStringToJSONObject(jsonRequest, MOBILE_PHONE,              mobilePhoneNumber);
            }
        }catch (Exception e){
            utility.setStringToJSONObject(jsonRequest, MOBILE_PHONE,              mobilePhoneNumber);
        }


        try {
            String url = URL_SERVER + AppController.currentUserDetails.getString(URL);
            if(jsonRequest == null){
                utility.showAlertDialog(this,"No changed user data!");
                return;
            }
            isLoadingBase = true;
            mProgressDialog.show();
            onTaskCompletedFlag =1;
            mGetDataTask = new GetDataTask(url,jsonRequest, this, PATCH);
            mGetDataTask.execute();
        }catch (Exception e){
            return;
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    @OnClick(R.id.changePasswordButton)
    public void showChangePasswordDialog(){
        changePasswordDiallog.show();
    }

    @Override
    public void onTaskSuccess(JSONObject jsonResponse) {
        isLoadingBase = false;
        mProgressDialog.dismiss();
        if(onTaskCompletedFlag == 0){
            try {
                AppController.charities = jsonResponse.getJSONArray(CHARITIES);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if (onTaskCompletedFlag == 1){
            utility.showAlertDialog(this,"Profile has been modified successfully!");
        }else if(onTaskCompletedFlag == 2){
            utility.showAlertDialog(this,"Password has been changed successfully!");
        }
        charitySpinnerModels = setSpinnerData();
        loadSpinner(charitySpinnerModels);

    }

    @Override
    public void onTaskError(String msg) {
        isLoadingBase = false;
        mProgressDialog.dismiss();
        utility.showAlertDialog(this, msg);

    }
}
