package com.sproutonecard.rechargeandreward.ui.activity;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
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

public class AddValueActivity extends BaseActivity implements AdapterView.OnItemSelectedListener, OnTaskCompleted {
    private ArrayList<SpinnerModel> fundingSourceSpinnerModels = new ArrayList<>();
    private ImageButton oneTimeSelectedImageButton;
    private ImageButton recurringSelectedImageButton;
    private EditText oneTimeAmountEditText;
    private ImageButton recurringActionImageButton;
    private EditText recurringAmountEditText;
    private EditText recurringBelowAmountEditText;
    private CustomSpinnerAdapter adapter;
    private Spinner funddingSourceSpinner;
    private String selectedFundingSourceId = null;
    private String selectedAccountUrl = null;
    private Boolean oneTimeFlag =true, recurringFlag=false, recurringActionFlag=true;
    private int onTaskCompletedFlag = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_value);
        ButterKnife.bind(this);
        initView();
    }

    private void initView(){
        funddingSourceSpinner = (Spinner) findViewById(R.id.spinnerOAN_addValue);
        oneTimeAmountEditText = (EditText)findViewById(R.id.oneTimeAmountEditText);
        recurringAmountEditText = (EditText)findViewById(R.id.recurringEditText);
        recurringBelowAmountEditText = (EditText) findViewById(R.id.recurringBelowEditText);
        oneTimeSelectedImageButton = (ImageButton)findViewById(R.id.onTimeSelectedImageButton);
        recurringSelectedImageButton = (ImageButton) findViewById(R.id.recurringSelectedImageButton);
        recurringActionImageButton = (ImageButton)findViewById(R.id.recurringActionImageButton);
        oneTimeAmountEditText.setEnabled(oneTimeFlag);
        oneTimeAmountEditText.requestFocus();
        recurringAmountEditText.setEnabled(recurringFlag);
        recurringBelowAmountEditText.setEnabled(recurringFlag);
        recurringActionImageButton.setClickable(recurringFlag);
        selectedAccountUrl = getIntent().getStringExtra("selectedAccountUrl");
        getUserDetails();

    }
    /********************* Method to get User Details From Server **************************/
    private void getUserDetails(){
//        get user details
        if(isLoadingBase)return;
        try {
            String Url = URL_SERVER + AppController.currentUser.getString(URL);
            isLoadingBase = true;

            mProgressDialog.show();

            mGetDataTask = new GetDataTask(Url,null,this, GET);
            onTaskCompletedFlag = 0;
            mGetDataTask.execute();
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(APP_NAME, e.getLocalizedMessage());
        }

    }
    /*********************
     * Method to set data in ArrayList
     **************************/
    private ArrayList<SpinnerModel> setSpinnerData() {
        final ArrayList<SpinnerModel> spinnerModels = new ArrayList<>();
        if(AppController.funding_sources !=null)
        for (int i = 0; i < AppController.funding_sources.length(); i++) {
            final SpinnerModel sched = new SpinnerModel();
            try {
                String name = AppController.funding_sources.getJSONObject(i).getString(NAME);
                sched.setItemName(name);
            } catch (JSONException e) {
                e.printStackTrace();
            }



            spinnerModels.add(sched);
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
            funddingSourceSpinner.setOnItemSelectedListener(this);
            adapter = new CustomSpinnerAdapter(getApplicationContext(), newSpinners, null);
            funddingSourceSpinner.setAdapter(adapter);

        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
       Log.d(APP_NAME,"Selected =>"+i);
        try {
            selectedFundingSourceId = AppController.funding_sources.getJSONObject(i).getString(FUNDING_SOURCE_ID);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    @OnClick(R.id.onTimeSelectedImageButton)
    public void onOneTimeImageButton(){
        oneTimeFlag = true;
        recurringFlag = false;
        oneTimeSelectedImageButton.setImageResource(R.drawable.ic_selectonetime);
        recurringSelectedImageButton.setImageResource(R.drawable.ic_onetime);
        oneTimeAmountEditText.setEnabled(oneTimeFlag);
        oneTimeAmountEditText.requestFocus();
        recurringAmountEditText.setEnabled(recurringFlag);
        recurringBelowAmountEditText.setEnabled(recurringFlag);
        recurringActionImageButton.setClickable(recurringFlag);
    }

    @OnClick(R.id.recurringSelectedImageButton)
    public void onRecurringImageButton(){
        oneTimeFlag = false;
        recurringFlag = true;
        oneTimeSelectedImageButton.setImageResource(R.drawable.ic_onetime);
        recurringSelectedImageButton.setImageResource(R.drawable.ic_selectonetime);
        oneTimeAmountEditText.setEnabled(oneTimeFlag);
        recurringAmountEditText.setEnabled(recurringFlag);
        recurringBelowAmountEditText.setEnabled(recurringFlag);
        recurringAmountEditText.requestFocus();
        recurringActionImageButton.setClickable(recurringFlag);
    }
    @OnClick(R.id.recurringActionImageButton)
    public void onRecurringActionImageButton(){
        recurringActionFlag = !recurringActionFlag;
        if(recurringActionFlag){
            recurringActionImageButton.setImageResource(R.drawable.ic_addtopoff);
        }else {
            recurringActionImageButton.setImageResource(R.drawable.ic_topoffadd);
        }
    }
    @OnClick(R.id.addFundingSourceImgButton)
    public void onAddFundingSource(){
        navToNextAndFinish(this, AddFundingSourceActivity.class);
    }

    @OnClick(R.id.cancelButton_addValue)
    public void onCancelAddValue(){
        navToNextAndFinish(this, LandingPageActivity.class);
    }

    @OnClick(R.id.submitButton_addValue)
    public void onSubmitAddValue(){
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
        if(selectedFundingSourceId == null)return;
        if(selectedAccountUrl == null){
            utility.showAlertDialog(this, "No Account!");
            return;
        }


        if(oneTimeFlag){
            String oneTimeAmount = oneTimeAmountEditText.getText().toString().trim();
            String fundingUrl = URL_SERVER + selectedAccountUrl + "/load/";

            JSONObject jsonRequest = new JSONObject();
            utility.setStringToJSONObject(jsonRequest, AMOUNT,              oneTimeAmount);
            utility.setStringToJSONObject(jsonRequest, FUNDING_SOURCE_ID,   selectedFundingSourceId);

            isLoadingBase = true;

            mProgressDialog.show();
            onTaskCompletedFlag = 1;
            mGetDataTask = new GetDataTask(fundingUrl,jsonRequest,this, POST);
            mGetDataTask.execute();

        }else if(recurringFlag){
            String recurringAmount = recurringAmountEditText.getText().toString().trim();
            String belowAmount = recurringBelowAmountEditText.getText().toString().trim();
            String action;
            String fundingUrl = URL_SERVER+selectedAccountUrl+ "/recurring-load/";
            if(recurringActionFlag){
                action = "add";

            }else {
                action = "top-off";
            }
            JSONObject jsonRequest = new JSONObject();
            utility.setStringToJSONObject(jsonRequest, AMOUNT,              recurringAmount);
            utility.setStringToJSONObject(jsonRequest, FUNDING_SOURCE_ID,   selectedFundingSourceId);
            utility.setStringToJSONObject(jsonRequest, ACTION,              action);
            utility.setStringToJSONObject(jsonRequest, TRIGGER_BALANCE,   belowAmount);

            isLoadingBase = true;

            mProgressDialog.show();

            mGetDataTask = new GetDataTask(fundingUrl,jsonRequest,this, POST);
            mGetDataTask.execute();


        }

    }

    @Override
    public void onTaskSuccess(JSONObject jsonResponse) {
        isLoadingBase = false;
        mProgressDialog.dismiss();
        if(onTaskCompletedFlag ==0 ){
            AppController.currentUserDetails = jsonResponse;

            try {
                AppController.funding_sources = AppController.currentUserDetails.getJSONArray(FUNDING_SOURCES);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //      set Listener of List and Spinner
            fundingSourceSpinnerModels = setSpinnerData();
            loadSpinner(fundingSourceSpinnerModels);
        }else if(onTaskCompletedFlag == 1){
            utility.showAlertDialog(this, "Funding Successfully!");
        }





    }

    @Override
    public void onTaskError(String msg) {
        isLoadingBase = false;
        mProgressDialog.dismiss();
        utility.showAlertDialog(this, msg);

    }
}
