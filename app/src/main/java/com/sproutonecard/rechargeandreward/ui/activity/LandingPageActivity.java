package com.sproutonecard.rechargeandreward.ui.activity;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
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
import butterknife.OnClick;
import butterknife.ButterKnife;




public class LandingPageActivity extends BaseActivity implements AdapterView.OnItemSelectedListener, OnTaskCompleted {

    /********************* Initialize Variables **************************/
    private TextView welcomeTextView, availableBalanceTextView, currentBalanceTextView, availablePointsTextView;
    public ArrayList<SpinnerModel> accountSpinnerModels = new ArrayList<SpinnerModel>();
    public ArrayList<SpinnerModel> groupSpinnerModels = new ArrayList<>();
    private CustomSpinnerAdapter adapter;
    private Spinner oanSpinner;
    private Dialog barcodeDialog;
    private Dialog groupListDialog;
    private int onTaskCompletedFlag = 0;
    private int startFlag = 0;
    private String selectedAccountUrl = null;
    private String selectedItemName = null;
    private int selectedAccountNumber = 0;
    private Bitmap bitmap = null;
    private ImageView barcodeImageView;
    private TextView barcodeStringTextView;
    private Spinner groupSpinner;
    private Button addGroupButton;
    private JSONObject selectedGroup= null;
    private TextView noCardTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);


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
            onTaskCompletedFlag = 0;
            mGetDataTask.execute();
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(APP_NAME, e.getLocalizedMessage());
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
            onTaskCompletedFlag = 1;
            mGetDataTask.execute();

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(APP_NAME, e.getLocalizedMessage());
        }


    }

    private void initView(){

        welcomeTextView = (TextView)findViewById(R.id.textViewHeader);
        availableBalanceTextView = (TextView)findViewById(R.id.availableBalanceTextView);
        currentBalanceTextView = (TextView)findViewById(R.id.currentBalanceTextView);
        availablePointsTextView = (TextView)findViewById(R.id.availablePointsTextView);
        oanSpinner = (Spinner)findViewById(R.id.spinnerOAN);
        //        Dialog initialize
        barcodeDialog = new Dialog(this);
        barcodeDialog.setContentView(R.layout.dialog_barcode);
        Window dialogWindow = barcodeDialog.getWindow();
        dialogWindow.setLayout(900, 800);
        dialogWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageButton closeDialogButton = (ImageButton)barcodeDialog.findViewById(R.id.closeImageButton);
        closeDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                barcodeDialog.dismiss();
            }
        });
        barcodeImageView = (ImageView)barcodeDialog.findViewById(R.id.barcodeImageView);
        noCardTextView = (TextView)barcodeDialog.findViewById(R.id.noCardTextView);
        barcodeStringTextView = (TextView)barcodeDialog.findViewById(R.id.barcodeTextView);
//      Initialize Variable;
        selectedAccountUrl = null;

        getUserDetails();


    }

    /********************* Method to set data in ArrayList **************************/
    private ArrayList<SpinnerModel> setSpinnerData(JSONArray accounts) {
        final ArrayList<SpinnerModel> spinnerModels = new ArrayList<>();
        if(accounts == null)return null;
//        JSONArray accounts = AppController.accounts;
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

    /********************* Method to set data in ArrayList **************************/
    private ArrayList<SpinnerModel> setSpinnerDataGroup(JSONArray groups) {
        final ArrayList<SpinnerModel> spinnerModels = new ArrayList<>();
        if(groups == null)return null;
//        JSONArray accounts = AppController.accounts;
        for (int i = 0; i < groups.length(); i++) {
            final SpinnerModel sched = new SpinnerModel();
            try {
                String groupCode = groups.getJSONObject(i).getString(CODE);
                sched.setItemName(groupCode);
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
            /********************* Set OAN SpinnerAdapter **************************/
            oanSpinner.setOnItemSelectedListener(this);
            adapter = new CustomSpinnerAdapter(getApplicationContext(), newSpinners, null);
            oanSpinner.setAdapter(adapter);

        } else {
            adapter.notifyDataSetChanged();
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
            welcomeTextView.setText("Welcome, " + firstName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(AppController.currentUserDetails.has(ACCOUNTS)){
            JSONArray accountArray = null;
            try {
                accountArray = AppController.currentUserDetails.getJSONArray(ACCOUNTS);
                AppController.accounts = accountArray;
                //      set Listener of List and Spinner
                accountSpinnerModels = setSpinnerData(AppController.accounts);
                loadSpinner(accountSpinnerModels);
                getAccountDetails();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else{
            getGroupList();
        }
        if(AppController.currentUserDetails.has(FUNDING_SOURCES)){
            JSONArray funding_sources = null;
            try {
                funding_sources = AppController.currentUserDetails.getJSONArray(FUNDING_SOURCES);
                AppController.funding_sources = funding_sources;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    /********************* Method to set account data in Screen **************************/

    private void reloadAccount(){
        if(AppController.accountDetails == null)return;
        double availableBalance;
        double currentBalance;
        int availablePoints;

        try {
            availableBalance = AppController.accountDetails.getDouble(AVAILABLE_BALANCE);
            currentBalance = AppController.accountDetails.getDouble(CURRENT_BALANCE);
            availablePoints = AppController.accountDetails.getInt(AVAILABLE_POINTS);
            if(availableBalance < 0.0){
                availableBalanceTextView.setText("-$"+ Math.abs(availableBalance));
            }else{
                availableBalanceTextView.setText("$"+availableBalance);
            }
            if(currentBalance < 0.0){
                currentBalanceTextView.setText("-$"+ Math.abs(availableBalance));
            }else{
                currentBalanceTextView.setText("$"+availableBalance);
            }
            availablePointsTextView.setText(String.valueOf(availablePoints));
            selectedItemName = AppController.accountDetails.getString(NAME);
            try {
                selectedAccountUrl = AppController.accountDetails.getString(URL);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    /********************* Method to Get Group List **************************/
    private void getGroupList(){
        if(isLoadingBase)return;
        try {
            if(!AppController.currentUserDetails.has(GROUP)){
                String groupUrl = URL_SERVER + AppController.currentUserDetails.getString(URL)+"/group?alias="+ AppController.firstOAN;

                isLoadingBase = true;
                mProgressDialog.show();

                mGetDataTask = new GetDataTask(groupUrl,null,this, GET);
                onTaskCompletedFlag = 2;
                mGetDataTask.execute();
            }else{
                getAccountDetails();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /********************* Method to show Group List **************************/
    private void showGroupList(){
        if(!AppController.currentUserDetails.has(GROUP)) {
            //        Dialog initialize
            groupListDialog = new Dialog(this);
            groupListDialog.setContentView(R.layout.dialog_add_group);
            Window dialogWindow = groupListDialog.getWindow();
            dialogWindow.setLayout(900, 800);
            dialogWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            ImageButton closeDialogButton = (ImageButton) barcodeDialog.findViewById(R.id.closeImageButton);
            closeDialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    groupListDialog.dismiss();
                }
            });

            groupSpinner = (Spinner) groupListDialog.findViewById(R.id.spinnerGroup);
            try {
                groupSpinnerModels = setSpinnerDataGroup(AppController.groupList.getJSONArray(GROUPS));

                if (this.adapter == null) {
                    /********************* Set OAN SpinnerAdapter **************************/
                    groupSpinner.setOnItemSelectedListener(this);
                    adapter = new CustomSpinnerAdapter(getApplicationContext(), groupSpinnerModels, null);
                    groupSpinner.setAdapter(adapter);

                } else {
                    adapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            addGroupButton = (Button) groupListDialog.findViewById(R.id.submitButton_group);
            addGroupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isLoadingBase) return;
                    ConnectivityManager connMgr = (ConnectivityManager)
                            getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        // fetch data
                    } else {
                        // display error
                        utility.showAlertDialog(LandingPageActivity.this, "Network Connection Error!");
                        return;
                    }
                     try {
                        String url = URL_SERVER + "/account/";
                        isLoadingBase = true;
                        mProgressDialog.show();

                        JSONObject jsonRequest = new JSONObject();
                        if (AppController.firstOAN.equals("virtual card")) {
                            utility.setStringToJSONObject(jsonRequest, USER_ID, AppController.currentUser.getString(USER_ID));
                            if (selectedGroup == null) {
                                selectedGroup = AppController.groupList.getJSONArray(GROUPS).getJSONObject(0);
                            }
                            utility.setStringToJSONObject(jsonRequest, GROUP, selectedGroup.getString(CODE));


                        } else {
                            utility.setStringToJSONObject(jsonRequest, USER_ID, AppController.currentUser.getString(USER_ID));
                            if (selectedGroup == null) {
                                selectedGroup = AppController.groupList.getJSONArray(GROUPS).getJSONObject(0);
                            }
                            utility.setStringToJSONObject(jsonRequest, GROUP, selectedGroup.getString(CODE));
                            utility.setStringToJSONObject(jsonRequest, ALIAS, AppController.firstOAN);
                        }

                        mGetDataTask = new GetDataTask(url, jsonRequest, LandingPageActivity.this, POST);
                        onTaskCompletedFlag = 3;
                        mGetDataTask.execute();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d(APP_NAME, e.getLocalizedMessage());
                    }

                }
            });
            groupListDialog.show();
        }

    }

    /********************* Method to set itemSelected of Spinner **************************/
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(view.equals(oanSpinner)){
           selectedItemName = accountSpinnerModels.get(i).getItemName();
            try {
                selectedAccountUrl = AppController.accounts.getJSONObject(i).getString(URL);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            selectedAccountNumber = i;
            startFlag = 1;
            getAccountDetails();

        }else if(view.equals(groupSpinner)){
            try {
                selectedGroup = AppController.groupList.getJSONArray(GROUPS).getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }



    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /********************* Method for Logout **************************/
    @OnClick(R.id.logoutButton)
    public void onLogout(){
        barcodeDialog.dismiss();
        if(groupListDialog!= null)
        groupListDialog.dismiss();
        AppController.initValue();
        utility.setJSONObjectToSharedPreference(this, CURRENT_USER, null);
        navToNextAndFinish(LandingPageActivity.this, LoginActivity.class);


    }

    /********************* Method for Manage **************************/
    @OnClick(R.id.manageButton)
    public void onManage(){
        barcodeDialog.dismiss();
        if(groupListDialog!= null)
        groupListDialog.dismiss();
        //utility.setJSONObjectToSharedPreference(this, CURRENT_USER, null);
        navToNextAndFinish(LandingPageActivity.this, ManageAccountActivity.class);


    }
    /********************** Method for Redeem **************************/
    @OnClick(R.id.reddemButton)
    public void onMoveRedeem(){
        barcodeDialog.dismiss();
        if(groupListDialog!= null)
        groupListDialog.dismiss();
        AppController.oanSpinnerModels = accountSpinnerModels;
        navToNextAndFinish(this,RedeemActivity.class);
    }

    /********************** Method for AddValue  **************************/
    @OnClick(R.id.addValueButton)
    public void onMoveAddValue(){
        barcodeDialog.dismiss();
        if(groupListDialog!= null)
        groupListDialog.dismiss();
        Intent intent = new Intent(this, AddValueActivity.class);
        intent.putExtra("selectedAccountUrl", selectedAccountUrl);
        navToNextAndFinish(this,intent);
    }
    /********************** Method for Show Barcode  **************************/
    @OnClick(R.id.imageViewCard)
    public void showBarcode(){
        String barcodeContentStr = selectedItemName;

        try {
            if(barcodeContentStr == null){
                noCardTextView.setVisibility(View.VISIBLE);

            }else {
                noCardTextView.setVisibility(View.GONE);
                bitmap = utility.encodeAsBitmap(barcodeContentStr, BarcodeFormat.CODE_128, 600, 300);
                barcodeImageView.setImageBitmap(bitmap);
                barcodeStringTextView.setText(barcodeContentStr);
            }



        } catch (WriterException e) {
            e.printStackTrace();
        }


        barcodeDialog.show();
    }

    @Override
    public void onTaskSuccess(JSONObject jsonResponse) {
        isLoadingBase = false;
        mProgressDialog.dismiss();
        if(onTaskCompletedFlag == 0){
            AppController.currentUserDetails = jsonResponse;
            reloadScreen();
        }else if(onTaskCompletedFlag == 1){
            AppController.accountDetails = jsonResponse;
            reloadAccount();

        }else if(onTaskCompletedFlag == 2){
            AppController.groupList = jsonResponse;
            showGroupList();

        }else if(onTaskCompletedFlag == 3){
            groupListDialog.dismiss();
        }


    }

    @Override
    public void onTaskError(String msg) {
        isLoadingBase = false;
        mProgressDialog.dismiss();
        utility.showAlertDialog(this, msg);

    }
}
