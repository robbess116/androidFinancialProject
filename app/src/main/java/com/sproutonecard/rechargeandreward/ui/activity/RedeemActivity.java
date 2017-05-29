package com.sproutonecard.rechargeandreward.ui.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

public class RedeemActivity extends BaseActivity implements AdapterView.OnItemSelectedListener, OnTaskCompleted {
    private Spinner redeemOanSpinner;
    private EditText promocodeEditText;
    private TextView availablePointsTextView;
    public ArrayList<SpinnerModel> oanSpinnerModels = new ArrayList<SpinnerModel>();
    private CustomSpinnerAdapter adapter;
    private String selectedAccountUrl = null;
    private int selectedAccountNumber = 0;
    private int onTaskCompletedFlag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem);
        ButterKnife.bind(this);
        getAccountDetails();
        initView();
    }

    private void initView(){
        redeemOanSpinner = (Spinner) findViewById(R.id.spinnerOAN_redeem);
        promocodeEditText = (EditText)findViewById(R.id.editTextPromoCode);
        availablePointsTextView = (TextView)findViewById(R.id.availablePoints_redeem);
        //      set Listener of List and Spinner
        if(AppController.oanSpinnerModels == null){
            oanSpinnerModels = setSpinnerData();
            loadSpinner(oanSpinnerModels);
        }else {
            oanSpinnerModels = AppController.oanSpinnerModels;
            loadSpinner(oanSpinnerModels);
        }

    }
    /********************* Method to get Account Details From Server **************************/
    private void getAccountDetails(){
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
        if(AppController.accounts == null)return;
        try {
            String url = URL_SERVER + AppController.accounts.getJSONObject(selectedAccountNumber).getString(URL);
            isLoadingBase = true;

            mProgressDialog.show();

            mGetDataTask = new GetDataTask(url,null,this, GET);
            onTaskCompletedFlag = 0;
            mGetDataTask.execute();

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(APP_NAME, e.getLocalizedMessage());
        }


    }

    /********************* Method to set account data in Screen **************************/

    private void reloadAccount(){
        if(AppController.accountDetails == null)return;
        int availablePoints;

        try {

            availablePoints = AppController.accountDetails.getInt(AVAILABLE_POINTS);

            availablePointsTextView.setText(String.valueOf(availablePoints));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    /********************** Method to set data in ArrayList **************************/
    private ArrayList<SpinnerModel> setSpinnerData() {
        final ArrayList<SpinnerModel> spinnerModels = new ArrayList<>();
        if(AppController.accounts == null)return null;
        JSONArray accounts = AppController.accounts;
        for (int i = 0; i < accounts.length(); i++) {
            final SpinnerModel sched = new SpinnerModel();
            try {
                String account_name = accounts.getJSONObject(i).getString(NAME);
                sched.setItemName(account_name);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            spinnerModels.add(sched);
        }
        return spinnerModels;
    }

    /*********************** Method to load Model data *****************************/
    private void loadSpinner(final ArrayList<SpinnerModel> newSpinners) {
        if (newSpinners == null)
            return;
        if (this.adapter == null) {
            redeemOanSpinner.setOnItemSelectedListener(this);
            adapter = new CustomSpinnerAdapter(getApplicationContext(), newSpinners, null);
            redeemOanSpinner.setAdapter(adapter);

        } else {
            adapter.notifyDataSetChanged();
        }
    }

    /*********************** Method to Redeem with promotion code *****************************/
    @OnClick(R.id.promoRedeemButton)
    public void onPromoRedeem(){
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
        promocodeEditText.setError(null);
        String promocode = promocodeEditText.getText().toString().trim();

        if(promocode.isEmpty()){
            promocodeEditText.setError(getString(R.string.error_field_required));
            focusView = promocodeEditText;
            focusView.requestFocus();
            return;
        }


        JSONObject jsonRequest = new JSONObject();
        utility.setStringToJSONObject(jsonRequest, PROMOTION_CODE,              promocode);
        if(selectedAccountUrl == null){
            utility.showAlertDialog(this, "Please choose account!");
            return;
        }
       String promoUrl = URL_SERVER + selectedAccountUrl + "/instant-promotion/";
        onTaskCompletedFlag = 1;
       isLoadingBase = true;
       mProgressDialog.show();

        mGetDataTask = new GetDataTask(promoUrl,jsonRequest, this, POST);
        mGetDataTask.execute();
    }

    @OnClick(R.id.redeem1Button)
    public void onRedeem1Button(){

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
        JSONObject jsonRequest = new JSONObject();
        utility.setStringToJSONObject(jsonRequest, POINTS,              "500");
        if(selectedAccountUrl == null){
            utility.showAlertDialog(this, "Please choose account!");
            return;
        }
        String redemptionUrl = URL_SERVER + selectedAccountUrl + "/redemption/";
        onTaskCompletedFlag = 1;
        isLoadingBase = true;
        mProgressDialog.show();

        mGetDataTask = new GetDataTask(redemptionUrl,jsonRequest, this, POST);
        mGetDataTask.execute();

    }

    @OnClick(R.id.redeem3Button)
    public void onRedeem3Button(){

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
        JSONObject jsonRequest = new JSONObject();
        utility.setStringToJSONObject(jsonRequest, POINTS,              "1000");
        if(selectedAccountUrl == null){
            utility.showAlertDialog(this, "Please choose account!");
            return;
        }
        String redemptionUrl = URL_SERVER + selectedAccountUrl + "/redemption/";
        onTaskCompletedFlag = 1;
        isLoadingBase = true;
        mProgressDialog.show();

        mGetDataTask = new GetDataTask(redemptionUrl,jsonRequest, this, POST);
        mGetDataTask.execute();

    }

    @OnClick(R.id.redeem10Button)
    public void onRedeem10Button(){

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
        JSONObject jsonRequest = new JSONObject();
        utility.setStringToJSONObject(jsonRequest, POINTS,              "2500");
        if(selectedAccountUrl == null){
            utility.showAlertDialog(this, "Please choose account!");
            return;
        }
        String redemptionUrl = URL_SERVER + selectedAccountUrl + "/redemption/";
        onTaskCompletedFlag = 1;
        isLoadingBase = true;
        mProgressDialog.show();

        mGetDataTask = new GetDataTask(redemptionUrl,jsonRequest, this, POST);
        mGetDataTask.execute();

    }

    @OnClick(R.id.redeem25Button)
    public void onRedeem25Button(){

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
        JSONObject jsonRequest = new JSONObject();
        utility.setStringToJSONObject(jsonRequest, POINTS,              "5000");
        if(selectedAccountUrl == null){
            utility.showAlertDialog(this, "Please choose account!");
            return;
        }
        String redemptionUrl = URL_SERVER + selectedAccountUrl + "/redemption/";
        onTaskCompletedFlag = 1;
        isLoadingBase = true;
        mProgressDialog.show();

        mGetDataTask = new GetDataTask(redemptionUrl,jsonRequest, this, POST);
        mGetDataTask.execute();

    }
    @OnClick(R.id.cancelButton_redeem)
    public void onCancelRedeem(){
        navToNextAndFinish(this, LandingPageActivity.class);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String selectedItemName = oanSpinnerModels.get(i).getItemName();
        selectedAccountNumber = i;
        try {
            selectedAccountUrl = AppController.accounts.getJSONObject(i).getString(URL);
            getAccountDetails();
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
        if(onTaskCompletedFlag == 0){
            AppController.accountDetails = jsonResponse;

            reloadAccount();

        }else if(onTaskCompletedFlag == 1){ try {
            int newPointsBalance = jsonResponse.getInt(NEW_POINTS_BALANCE);
            availablePointsTextView.setText(newPointsBalance);
            String message = "$" + jsonResponse.getDouble(NET_TRANSACTION_AMOUNT)+" has been added to your account.";
            utility.showAlertDialog(this, message);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        }


    }

    @Override
    public void onTaskError(String msg) {
        isLoadingBase = false;
        mProgressDialog.dismiss();
        utility.showAlertDialog(this, msg);

    }
}
