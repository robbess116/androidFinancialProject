package com.sproutonecard.rechargeandreward.ui.activity;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.sproutonecard.rechargeandreward.AppController;
import com.sproutonecard.rechargeandreward.R;
import com.sproutonecard.rechargeandreward.api.GetDataTask;
import com.sproutonecard.rechargeandreward.api.OnTaskCompleted;
import com.sproutonecard.rechargeandreward.model.ExpandableListViewPurchaseItemModel;
import com.sproutonecard.rechargeandreward.model.SpinnerModel;
import com.sproutonecard.rechargeandreward.ui.adapter.CustomListViewAdapter;
import com.sproutonecard.rechargeandreward.ui.adapter.CustomSpinnerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class RequestRefundActivity extends BaseActivity implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener, OnTaskCompleted {
    /*********************
     * Initialize Variables
     **************************/
    public ArrayList<SpinnerModel> reasonSpinnerModels = new ArrayList<SpinnerModel>();
    private CustomSpinnerAdapter adapter;
    private Spinner reasonSpinner;
    private ListView selectedPurchaseListView;
    private ArrayList<ExpandableListViewPurchaseItemModel> refundedPurchase = new ArrayList<ExpandableListViewPurchaseItemModel>();
    private CustomListViewAdapter listViewAdapter;
    private ImageButton transactionImageButton;
    private TextView transactionType;
    private TextView transactionAmount;
    private Boolean checkedStatusTransaction;
    private EditText otherReasonEditText;
    private String disputeUrl = null;

    private String reasonStr = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_refund);
        initView();
        ButterKnife.bind(this);
        }

    private void initView() {
        try {
            if(AppController.selectedAccountTransactionDetails==null)return;
            disputeUrl = URL_SERVER + AppController.selectedAccountTransactionDetails.getString(ACCOUNT_URL)+"/dispute/";
        } catch (JSONException e) {
            e.printStackTrace();
        }

        reasonSpinner = (Spinner) findViewById(R.id.spinnerReason);
        selectedPurchaseListView = (ListView) findViewById(R.id.refundItemsListView);




        /********************* Set OAN Spinner **************************/
        reasonSpinnerModels = setSpinnerData();
        loadSpinnerData(reasonSpinnerModels);
        /********************* Set Selected Purchase ListView **************************/
        refundedPurchase = setListData();
        loadListData(refundedPurchase);

//        transactionItemInitialized
        transactionImageButton = (ImageButton) findViewById(R.id.transactionSelectedIconImageButton);
        transactionAmount = (TextView) findViewById(R.id.refundedTransactionAmount);
        transactionType = (TextView) findViewById(R.id.refundedTransactionType);
        transactionAmount.setText(AppController.selectedRefundedPurchase.getAmount());
        transactionType.setText(AppController.selectedRefundedPurchase.getType());
        checkedStatusTransaction = false;
        otherReasonEditText = (EditText)findViewById(R.id.editTextOtherReason);
        otherReasonEditText.setEnabled(false);


    }

    /*********************
     * Method to set data in ArrayList
     **************************/
    private ArrayList<SpinnerModel> setSpinnerData() {
        String[] reasonArr = getResources().getStringArray(R.array.refund_reason_array);
        final ArrayList<SpinnerModel> spinnerModels = new ArrayList<>();
        for (int i = 0; i < reasonArr.length; i++) {
            final SpinnerModel sched = new SpinnerModel();

            sched.setItemName(reasonArr[i]);

            spinnerModels.add(sched);
        }
        return spinnerModels;
    }


    private ArrayList<ExpandableListViewPurchaseItemModel> setListData() {
        ArrayList<ExpandableListViewPurchaseItemModel> selectedPurchase;
        if(AppController.selectedRefundedPurchase == null){
            selectedPurchase = new ArrayList<>();
        }else{
            selectedPurchase = AppController.selectedRefundedPurchase.getPurchaseItemModels();
        }

        return selectedPurchase;
    }

    /*********************
     * Method to load data in Adapter
     **************************/
    private void loadSpinnerData(ArrayList<SpinnerModel> spinnerModels) {
        if (spinnerModels == null)
            return;
        if (this.adapter == null) {
            /********************* Set OAN SpinnerAdapter **************************/
            reasonSpinner.setOnItemSelectedListener(this);
            adapter = new CustomSpinnerAdapter(getApplicationContext(), spinnerModels, null);
            reasonSpinner.setAdapter(adapter);

        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private void loadListData(ArrayList<ExpandableListViewPurchaseItemModel> selectedPurchase) {
        if (selectedPurchase == null) {
            return;
        }
        if (this.listViewAdapter == null) {

            this.listViewAdapter = new CustomListViewAdapter(getApplicationContext(), selectedPurchase);
            selectedPurchaseListView.setAdapter(this.listViewAdapter);
            selectedPurchaseListView.setOnItemClickListener(this);
        } else {
            this.listViewAdapter.notifyDataSetChanged();
        }
    }

    /*********************
     * Method to set itemSelected of Spinner
     **************************/
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String selectedItemName = reasonSpinnerModels.get(i).getItemName();
        reasonStr = selectedItemName;
        if(selectedItemName.equals("Other")){
            otherReasonEditText.setEnabled(true);
        }else {
            otherReasonEditText.setEnabled(false);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        refundedPurchase.get(i).setChecked(!refundedPurchase.get(i).getChecked());
        loadListData(refundedPurchase);

        for(int ii = 0; ii< refundedPurchase.size(); ii++){
            if(!refundedPurchase.get(ii).getChecked()){
                checkedStatusTransaction = false;
                transactionImageButton.setImageResource(R.drawable.ic_onetime);
                break;
            }
        }
    }

    @OnClick(R.id.transactionSelectedIconImageButton)
    public void checkedRefundedTransaction() {
        if (checkedStatusTransaction) {
            transactionImageButton.setImageResource(R.drawable.ic_onetime);

        } else {
            transactionImageButton.setImageResource(R.drawable.ic_selectonetime);
        }
        checkedStatusTransaction = !checkedStatusTransaction;
        for(int i=0; i<refundedPurchase.size();i++){
            refundedPurchase.get(i).setChecked(checkedStatusTransaction);
        }
        loadListData(refundedPurchase);


    }

    @OnClick(R.id.cancelButton_requestRefund)
    public void onCancelRefund(){
        AppController.selectedRefundedPurchase = null;
        navToNextAndFinish(this, ManageAccountActivity.class);
    }
    @OnClick(R.id.submitButton_requestRefund)
    public void onDisputedSubmit(){
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
        if(reasonStr == "Other"){
            View focusView = null;
            otherReasonEditText.setError(null);
            reasonStr = otherReasonEditText.getText().toString().trim();
            if(reasonStr.isEmpty()){
                otherReasonEditText.setError(getString(R.string.error_field_required));
                focusView = otherReasonEditText;
                focusView.requestFocus();
                return;
            }

        }


        JSONArray disputedItems = new JSONArray();
        JSONObject jsonRequest = new JSONObject();

        if(checkedStatusTransaction){
            try {
                utility.setStringToJSONObject(jsonRequest, TRANSACTION_ID,     AppController.refundTransaction.getString(TRANSACTION_ID));
                utility.setStringToJSONObject(jsonRequest, REASON,           reasonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else{
            for(int ii = 0; ii< refundedPurchase.size(); ii++){
                if(refundedPurchase.get(ii).getChecked()){
                   utility.addJSONObjectToJSONArray(disputedItems, refundedPurchase.get(ii).getItemID());
                }
            }
            try {
                utility.setStringToJSONObject(jsonRequest, TRANSACTION_ID,     AppController.refundTransaction.getString(TRANSACTION_ID));
                utility.setStringToJSONObject(jsonRequest, REASON,           reasonStr);
                if(disputedItems.length() == 0){
                    utility.showAlertDialog(this, "No selected items!");
                    return;
                }else{
                    utility.setJSONArrayToJSONObject(jsonRequest, ITEMS,           disputedItems);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


        if(disputeUrl == null || jsonRequest == null){
            utility.showAlertDialog(this, "Request Error!");
            return;
        }

        isLoadingBase = true;
        mProgressDialog.show();

        mGetDataTask = new GetDataTask(disputeUrl,jsonRequest, this, POST);
        mGetDataTask.execute();

    }

    @Override
    public void onTaskSuccess(JSONObject jsonResponse) {
        isLoadingBase = false;
        mProgressDialog.dismiss();
        utility.showAlertDialog(this,"Dispute successfully!");

    }

    @Override
    public void onTaskError(String msg) {
        isLoadingBase = false;
        mProgressDialog.dismiss();
        utility.showAlertDialog(this, msg);

    }
}
