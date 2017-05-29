package com.sproutonecard.rechargeandreward.ui.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sproutonecard.rechargeandreward.AppController;
import com.sproutonecard.rechargeandreward.R;
import com.sproutonecard.rechargeandreward.api.GetDataTask;
import com.sproutonecard.rechargeandreward.api.OnTaskCompleted;
import com.sproutonecard.rechargeandreward.model.ExpandableListViewPurchaseItemModel;
import com.sproutonecard.rechargeandreward.model.ExpandableListViewTransactionModel;
import com.sproutonecard.rechargeandreward.model.SpinnerModel;
import com.sproutonecard.rechargeandreward.ui.adapter.CustomExpandableListAdapter;
import com.sproutonecard.rechargeandreward.ui.adapter.CustomSpinnerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ManageAccountActivity extends BaseActivity implements AdapterView.OnItemSelectedListener, ExpandableListView.OnGroupExpandListener, ExpandableListView.OnGroupCollapseListener, ExpandableListView.OnGroupClickListener, OnTaskCompleted {

    /*********************
     * Initialize Variables
     **************************/
    public ArrayList<SpinnerModel> CustomListViewValueArr = new ArrayList<SpinnerModel>();
    private CustomSpinnerAdapter adapterSpinner;
    private Spinner oanSpinner;
    private ExpandableListView listViewTransaction;
    private CustomExpandableListAdapter adapterList;
    private String selectedAccountUrl = null;
    private int selectedAccountNumber = 0;
    private int selectedTransactionNumber;
    private int onTaskCompletedFlag = 0;
    private TextView userFullNameTextView;
    private TextView noHistoryTextView;


    public ArrayList<ExpandableListViewTransactionModel> transactionModels = new ArrayList<ExpandableListViewTransactionModel>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_account);
        initView();
        ButterKnife.bind(this);


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
            onTaskCompletedFlag = 2;
            mGetDataTask.execute();
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(APP_NAME, e.getLocalizedMessage());
        }

    }

    /*********************
     * Method to get Account Details From Server
     **************************/
    private void getSelectedAccountTransactions() {
        if (isLoadingBase) return;
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
        if (AppController.accounts == null) return;
        try {
            String url = URL_SERVER + AppController.accounts.getJSONObject(selectedAccountNumber).getString(URL)+"/transactions";
            isLoadingBase = true;

            mProgressDialog.show();

            mGetDataTask = new GetDataTask(url, null, this, GET);
            onTaskCompletedFlag = 0;
            mGetDataTask.execute();

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(APP_NAME, e.getLocalizedMessage());
        }


    }

    /********************* Method to set data in Screen **************************/
    private void reloadScreen(){
        if(AppController.currentUserDetails == null)return;

            // Store data to AppController
        String firstName = null;
        try {
            firstName = AppController.currentUserDetails.getString(USER_FIRST_NAME);
            AppController.first_name = firstName;
            String lastName = AppController.currentUserDetails.getString(USER_LAST_NAME);
            AppController.last_name = lastName;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray accountArray = null;
        if(AppController.currentUserDetails.has(ACCOUNTS)){
            try {
                accountArray = AppController.currentUserDetails.getJSONArray(ACCOUNTS);
                AppController.accounts = accountArray;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        if(AppController.currentUserDetails.has(FUNDING_SOURCES)){
            JSONArray funding_sources = null;
            try {
                funding_sources = AppController.currentUserDetails.getJSONArray(FUNDING_SOURCES);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            AppController.funding_sources = funding_sources;

        }

            userFullNameTextView.setText(AppController.first_name + " "+ AppController.last_name);
            //      set Listener of List and Spinner
            CustomListViewValueArr = setSpinnerData();
            loadSpinner(CustomListViewValueArr);

            getSelectedAccountTransactions();


    }
    /********************* Method to set account data in Screen **************************/

    private void reloadTransactionHistory(){
        if(AppController.selectedAccountTransactionDetails == null)return;
        JSONArray transactions = null;

        try {
            if(AppController.selectedAccountTransactionDetails.has(TRANSACTIONS)){
                transactions = AppController.selectedAccountTransactionDetails.getJSONArray(TRANSACTIONS);
                AppController.selectedAccountTransactions = transactions;
                if(transactions.length() == 0){
                    noHistoryTextView.setVisibility(View.VISIBLE);

                }else {
                    noHistoryTextView.setVisibility(View.GONE);
                }
                transactionModels = setListData();
                loadHosts(transactionModels);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    /************************
     * Initialize View
     *****************************/
    private void initView() {
//      Initialize View
        userFullNameTextView = (TextView) findViewById((R.id.userNameTextView));
        userFullNameTextView.setText(AppController.first_name + " "+ AppController.last_name);
        listViewTransaction = (ExpandableListView) findViewById(R.id.transactionHistoryListView);
        oanSpinner = (Spinner) findViewById(R.id.spinnerOAN_manageAccount);
        noHistoryTextView = (TextView)findViewById(R.id.noHistoryTextView);
//      set data

        getUserDetails();
    }

    /***********************
     * Method to set Model data
     *****************************/
    private ArrayList<ExpandableListViewTransactionModel> setListData() {
        final ArrayList<ExpandableListViewTransactionModel> transactionModels = new ArrayList<ExpandableListViewTransactionModel>();
        if(AppController.selectedAccountTransactions ==null )return null;
        for (int i = 0; i < AppController.selectedAccountTransactions.length(); i++) {
            final ExpandableListViewTransactionModel sched = new ExpandableListViewTransactionModel();
            try {
                String createdDate = AppController.selectedAccountTransactions.getJSONObject(i).getString(CREATED);
                String[] separated = createdDate.split(" ");
                sched.setDate(separated[0]);
                double transactionAmount = AppController.selectedAccountTransactions.getJSONObject(i).getDouble(TRANSACTION_AMOUNT);
                if(transactionAmount < 0.0){
                    sched.setAmount("-$" +Math.abs(transactionAmount) );

                }else{
                    sched.setAmount("$" +Math.abs(transactionAmount) );

                }

                sched.setType(AppController.selectedAccountTransactions.getJSONObject(i).getString(TRANSACTION_TYPE));
                sched.setChecked(false);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            sched.setPurchaseItemModels(new ArrayList<ExpandableListViewPurchaseItemModel>());
            transactionModels.add(sched);
        }

        return transactionModels;
    }
    private void setPurchaseData(){
        final ArrayList<ExpandableListViewPurchaseItemModel> sched = transactionModels.get(selectedTransactionNumber).getPurchaseItemModels();
        try {
            if(AppController.selectedAccountTransactionContents.has(ITEMS)){
                JSONArray purchaseItems = AppController.selectedAccountTransactionContents.getJSONArray(ITEMS);
                if(purchaseItems == null){

                }else{
                    for (int j = 0; j < purchaseItems.length(); j++) {
                        final ExpandableListViewPurchaseItemModel purchaseItemModel = new ExpandableListViewPurchaseItemModel();
                        double transactionAmount = purchaseItems.getJSONObject(j).getDouble(PRICE);
                        if(transactionAmount < 0.0){

                            purchaseItemModel.setItemAmount("-$" +Math.abs(transactionAmount) );

                        }else{
                            purchaseItemModel.setItemAmount("$" +Math.abs(transactionAmount) );

                        }
                        purchaseItemModel.setItemName(purchaseItems.getJSONObject(j).getString(DESCRIPTION));
                        purchaseItemModel.setChecked(false);
                        sched.add(purchaseItemModel);

                    }
                    final ExpandableListViewPurchaseItemModel disputeModel = new ExpandableListViewPurchaseItemModel();
                    disputeModel.setItemName("none");
                    disputeModel.setItemAmount("none");
                    disputeModel.setChecked(false);
                    sched.add(disputeModel);
                }


            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        loadHosts(transactionModels);
    }

    /*********************** Method to load Model data *****************************/
    private void loadHosts(final ArrayList<ExpandableListViewTransactionModel> newTransactionModels) {
        if (newTransactionModels == null)
            return;
        if (this.adapterList == null) {

            adapterList = new CustomExpandableListAdapter(getApplicationContext(), newTransactionModels);
            listViewTransaction.setAdapter(adapterList);
            listViewTransaction.setOnGroupClickListener(this);
            listViewTransaction.setOnGroupCollapseListener(this);
            listViewTransaction.setOnGroupExpandListener(this);

        } else {
            adapterList.notifyDataSetChanged();
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

    /************************ Method to load Model data *****************************/
    private void loadSpinner(final ArrayList<SpinnerModel> newSpinners) {
        if (newSpinners == null)
            return;
        if (this.adapterSpinner == null) {
            oanSpinner.setOnItemSelectedListener(this);
            adapterSpinner = new CustomSpinnerAdapter(getApplicationContext(), newSpinners, null);
            oanSpinner.setAdapter(adapterSpinner);

        } else {
            adapterSpinner.notifyDataSetChanged();
        }
    }


    /********************** Method to set itemSelected of Spinner **************************/
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        selectedAccountNumber = i;
        try {
            selectedAccountUrl = AppController.accounts.getJSONObject(i).getString(URL);
            getSelectedAccountTransactions();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    /********************** Method for Logout **************************/
    @OnClick(R.id.cancelButton_manageAccount)
    public void onCancel() {
        //utility.setJSONObjectToSharedPreference(this, CURRENT_USER, null);
        navToNextAndFinish(ManageAccountActivity.this, LandingPageActivity.class);


    }

    @OnClick(R.id.addAccountImgButton)
    public void onAddAccount() {
        navToNextAndFinish(this, AddAccountActivity.class);

    }


    @Override
    public void onGroupExpand(int i) {
        transactionModels.get(i).setChecked(true);
        try {
            if(AppController.selectedAccountTransactions == null)return ;
            String transactionContentsUrl = URL_SERVER + AppController.selectedAccountTransactions.getJSONObject(i).getString(URL);
            selectedTransactionNumber = i;
            isLoadingBase = true;
            mProgressDialog.show();
            mGetDataTask = new GetDataTask(transactionContentsUrl, null, this, GET);
            onTaskCompletedFlag = 3;
            mGetDataTask.execute();

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onGroupCollapse(int i) {
        transactionModels.get(i).setChecked(false);
        loadHosts(transactionModels);

    }

    @Override
    public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {

        return false;
    }

    @OnClick(R.id.editProfileButton)
    public void onEditProfile() {
        navToNextAndFinish(this, ManageProfileActivity.class);
    }


    @Override
    public void onTaskSuccess(JSONObject jsonResponse) {
        isLoadingBase = false;
        mProgressDialog.dismiss();
        if(onTaskCompletedFlag == 2){
            AppController.currentUserDetails = jsonResponse;
            reloadScreen();


        }else if (onTaskCompletedFlag == 0) {
            AppController.selectedAccountTransactionDetails = jsonResponse;

            reloadTransactionHistory();

        } else if (onTaskCompletedFlag == 3) {
            AppController.selectedAccountTransactionContents = jsonResponse;
            setPurchaseData();
        }


    }

    @Override
    public void onTaskError(String msg) {
        isLoadingBase = false;
        mProgressDialog.dismiss();
        utility.showAlertDialog(this, msg);

    }
}