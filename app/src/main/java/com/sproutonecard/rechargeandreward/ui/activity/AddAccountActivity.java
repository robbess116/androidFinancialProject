package com.sproutonecard.rechargeandreward.ui.activity;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.sproutonecard.rechargeandreward.AppController;
import com.sproutonecard.rechargeandreward.R;
import com.sproutonecard.rechargeandreward.api.GetDataTask;
import com.sproutonecard.rechargeandreward.api.OnTaskCompleted;
import com.sproutonecard.rechargeandreward.model.SpinnerModel;
import com.sproutonecard.rechargeandreward.ui.adapter.CustomSpinnerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddAccountActivity extends BaseActivity implements AdapterView.OnItemSelectedListener, OnTaskCompleted {
    private EditText nicknameEditText;
    private EditText oanEditText;
    private Spinner groupSpinner;
    private int groupIndex=0;
    private JSONObject selecetedGroup=null;
    ArrayList<SpinnerModel> groupSpinnerModels = new ArrayList<>();
    CustomSpinnerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);
        ButterKnife.bind(this);
        initView();
    }

    private void initView(){
        nicknameEditText =(EditText)findViewById(R.id.nickNameEditText_addAccount);
        oanEditText =(EditText) findViewById(R.id.oanNumberEditText_addAccount);
        groupSpinner = (Spinner) findViewById(R.id.spinnerGroup);
        try {
            if(!AppController.currentUserDetails.has(GROUP)){
                groupSpinnerModels = setSpinnerData(AppController.groupList.getJSONArray(GROUPS));
                loadSpinner(groupSpinnerModels);
            }else{
                JSONArray groups = new JSONArray();
                utility.setJSONObjectToJSONArray(groups,AppController.currentUserDetails.getJSONObject(GROUP));
                groupSpinnerModels = setSpinnerData(groups);
                loadSpinner(groupSpinnerModels);
            }

        } catch (JSONException e) {
            e.printStackTrace();

        }



    }
    /*********************
     * Method to set data in ArrayList
     **************************/
    private ArrayList<SpinnerModel> setSpinnerData(JSONArray groups) {
        final ArrayList<SpinnerModel> spinnerModels = new ArrayList<>();
        if(groups !=null)
        for (int i = 0; i < groups.length(); i++) {
            final SpinnerModel sched = new SpinnerModel();

            try {
                sched.setItemName(groups.getJSONObject(i).getString(CODE));
                spinnerModels.add(sched);
                if(groups.getJSONObject(i).getString(CODE).equals(AppController.currentUserDetails.getJSONObject(GROUP).getString(CODE))){
                    groupIndex = i;

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        return spinnerModels;
    }

    /*********************** Method to load Model data *****************************/
    private void loadSpinner(final ArrayList<SpinnerModel> newSpinners) {
        if (newSpinners == null)
            return;
        if (this.adapter == null) {
            groupSpinner.setOnItemSelectedListener(this);
            adapter = new CustomSpinnerAdapter(getApplicationContext(), newSpinners, null);
            groupSpinner.setAdapter(adapter);
            groupSpinner.setSelection(groupIndex);
            if(AppController.currentUserDetails.has(GROUP)){
                groupSpinner.setEnabled(false);
            }else{
                groupSpinner.setEnabled(true);
            }

        } else {
            adapter.notifyDataSetChanged();
        }
    }
    @OnClick(R.id.cancelButton_addAccount)
    public void onCancelAddAccount(){
        navToNextAndFinish(this, ManageAccountActivity.class);
    }
    @OnClick(R.id.submitButton_addAccount)
    public void onSubmitAddAccount(){
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
        nicknameEditText.setError(null);
        oanEditText.setError(null);
        String nickname = nicknameEditText.getText().toString().trim();
        String oan = oanEditText.getText().toString().trim();
        if(nickname.isEmpty()){
            nicknameEditText.setError(getString(R.string.error_field_required));
            focusView = nicknameEditText;
            focusView.requestFocus();
            return;
        }else if(oan.isEmpty()){
            oanEditText.setError(getString(R.string.error_field_required));
            focusView = oanEditText;
            focusView.requestFocus();
            return;
        }else if(oan.substring(0,1).equals("1")||oan.substring(0,1).equals("2")){

        }else{
            utility.showAlertDialog(this,"First digit should be 1 or 2!");
            oanEditText.setError(getString(R.string.error_field_required));
            focusView = oanEditText;
            focusView.requestFocus();
            return;
        }
        String url = URL_SERVER + "/account/";

        JSONObject jsonRequest = new JSONObject();
        if(groupSpinner.isEnabled()){
            utility.setStringToJSONObject(jsonRequest, NICKNAME,              nickname);
            utility.setStringToJSONObject(jsonRequest, ALIAS,                 oan);
            try {
                utility.setStringToJSONObject(jsonRequest, USER_ID,          AppController.currentUserDetails.getString(USER_ID));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                utility.setStringToJSONObject(jsonRequest, CODE,             selecetedGroup.getString(CODE));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            utility.setStringToJSONObject(jsonRequest, NICKNAME,              nickname);
            utility.setStringToJSONObject(jsonRequest, ALIAS,                 oan);
            try {
                utility.setStringToJSONObject(jsonRequest, USER_ID,          AppController.currentUserDetails.getString(USER_ID));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        isLoadingBase = true;
        mProgressDialog.show();

        mGetDataTask = new GetDataTask(url,jsonRequest, this, POST);
        mGetDataTask.execute();


    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        try {
            if(AppController.groupList!=null)
            selecetedGroup = AppController.groupList.getJSONArray(GROUP).getJSONObject(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onTaskSuccess(JSONObject jsonResponse) {
        isLoadingBase = false;
        mProgressDialog.dismiss();
        navToNextAndFinish(this,ManageAccountActivity.class);

    }

    @Override
    public void onTaskError(String msg) {
        isLoadingBase = false;
        mProgressDialog.dismiss();
        utility.showAlertDialog(this,msg);

    }
}
