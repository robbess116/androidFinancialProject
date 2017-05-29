package com.sproutonecard.rechargeandreward.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import com.google.gson.Gson;
import com.hps.integrator.entities.HpsToken;
import com.hps.integrator.entities.credit.HpsCreditCard;
import com.hps.integrator.services.HpsTokenService;
import com.sproutonecard.rechargeandreward.AppController;
import com.sproutonecard.rechargeandreward.R;
import com.sproutonecard.rechargeandreward.api.GetDataTask;
import com.sproutonecard.rechargeandreward.api.GetHpsTokenTask;
import com.sproutonecard.rechargeandreward.api.OnTaskCompleted;
import com.sproutonecard.rechargeandreward.model.SpinnerModel;
import com.sproutonecard.rechargeandreward.ui.adapter.CustomSpinnerAdapter;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddFundingSourceActivity extends BaseActivity implements AdapterView.OnItemSelectedListener, OnTaskCompleted {

    private int MY_SCAN_REQUEST_CODE = 100; // arbitrary int
    private EditText cardNumberEditText, cardExpiryEditText, cvvEditText, zipEditText, nicknameEditText;
    private String cardNumber,cvv,zip,expiry,lastFour,cardType,nickname,tokenValue = null;

    private Spinner cardTypeSpinner;
    ArrayList<SpinnerModel> cardTypeModels = new ArrayList<>();
    CustomSpinnerAdapter adapter;

    private int onTaskCompletedFlag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_funding_source);
        ButterKnife.bind(this);
        initView();
    }
    private void initView(){
        cardNumberEditText = (EditText) findViewById(R.id.cardNumberEditText);
        cardExpiryEditText = (EditText) findViewById(R.id.cardExpiryEditText);
        cvvEditText = (EditText) findViewById(R.id.cvvEditText);
        zipEditText = (EditText) findViewById(R.id.zipEditText);
        cardTypeSpinner = (Spinner)findViewById(R.id.cardTypeSpinner);
        nicknameEditText = (EditText)findViewById(R.id.nickNameEditText);

        /********************* Set OAN Spinner **************************/
        cardTypeModels = setSpinnerData();
        loadSpinner(cardTypeModels);
    }


    /*********************
     * Method to set data in ArrayList
     **************************/
    private ArrayList<SpinnerModel> setSpinnerData() {
        String[] cardname = getResources().getStringArray(R.array.card_name_array);
        String[] cardtype = getResources().getStringArray(R.array.card_type_array);
        final ArrayList<SpinnerModel> spinnerModels = new ArrayList<>();
        for (int i = 0; i < cardname.length; i++) {
            final SpinnerModel sched = new SpinnerModel();

            sched.setItemName(cardname[i]);
            sched.setItemType(cardtype[i]);

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
            cardTypeSpinner.setOnItemSelectedListener(this);
            adapter = new CustomSpinnerAdapter(getApplicationContext(), newSpinners, null);
            cardTypeSpinner.setAdapter(adapter);

        } else {
            adapter.notifyDataSetChanged();
        }
    }
    @OnClick(R.id.cancelButton_addFundingSource)
    public void onCancelAddFundingSource(){
        navToNextAndFinish(this, AddValueActivity.class);

    }
    @OnClick(R.id.submitButton_addFundingSource)
    public void onSubmitAddFundingSource(){
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
        cardNumberEditText.setError(null);
        cardExpiryEditText.setError(null);
        cvvEditText.setError(null);
        zipEditText.setError(null);
        nicknameEditText.setError(null);
        cardNumber = cardNumberEditText.getText().toString().trim();
        expiry = cardExpiryEditText.getText().toString().trim();
        cvv = cvvEditText.getText().toString().trim();
        zip = zipEditText.getText().toString().trim();
        nickname = nicknameEditText.getText().toString().trim();
        lastFour =  cardNumber.substring(11,15);
        String []expiryArr = expiry.split("/");
        String expiry_month = expiryArr[0];
        String expiry_year  = expiryArr[1];

        if(cardNumber.isEmpty()){
            cardExpiryEditText.setError(getString(R.string.error_field_required));
            focusView = cardExpiryEditText;
            focusView.requestFocus();
            return;
        }else if(expiry.isEmpty()){
            cardExpiryEditText.setError(getString(R.string.error_field_required));
            focusView = cardExpiryEditText;
            focusView.requestFocus();
            return;
        }else if(cvv.isEmpty()){
            cvvEditText.setError(getString(R.string.error_field_required));
            focusView = cvvEditText;
            focusView.requestFocus();
            return;
        }else if(zip.isEmpty()){
            zipEditText.setError(getString(R.string.error_field_required));
            focusView = zipEditText;
            focusView.requestFocus();
            return;
        }else if(cardType.isEmpty()){
            utility.showAlertDialog(this,"Please choose card type!");
            return;
        }else if(nickname.isEmpty()){
            nicknameEditText.setError(getString(R.string.error_field_required));
            focusView = nicknameEditText;
            focusView.requestFocus();
            return;
        }else if(nickname.length()>20){
            utility.showAlertDialog(this,"Nickname should be 20 at max");
            focusView = nicknameEditText;
            focusView.requestFocus();
            return;
        }
        HpsCreditCard card = new HpsCreditCard();
        card.setCvv(cvv);
        card.setNumber(cardNumber);
        card.setExpMonth(Integer.valueOf(expiry_month));
        card.setExpYear(2000+Integer.valueOf(expiry_year));
        getHeartlandToken(card);


    }
    private void getHeartlandToken(HpsCreditCard card){
        HpsTokenService hpsTokenService = new HpsTokenService(HPSTOKENSERVIC_EPUBLICKEY);
        String mPublicKey = hpsTokenService.getmPublicKey();
        String mUrl = hpsTokenService.getmUrl();

        byte[] creds = String.format("%s:", mPublicKey).getBytes();
        //String auth = String.format("Basic %s", Base64.encodeBase64URLSafe(creds));
        String encodedString = new String(Base64.encodeBase64(creds));
        String auth = String.format("Basic %s",encodedString.replace('+','-').replace('/','_'));

         //String payload = jsonObject.toString();
        Gson gson = new Gson();
        String payload = gson.toJson(new HpsToken(card));
        byte[] bytes = payload.getBytes();

        isLoadingBase = true;
        mProgressDialog.show();
        onTaskCompletedFlag = 1;

        GetHpsTokenTask mGetHpsTokenTask = new GetHpsTokenTask(mUrl ,auth,bytes, this, POST);
        mGetHpsTokenTask.execute();
    }

    private void addFundingSource(){
       if(isLoadingBase)return;

        String url = null;
        try {
            url = URL_SERVER + AppController.currentUserDetails.getString(URL)+ "/funding-source/";
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JSONObject jsonRequest = new JSONObject();
        utility.setStringToJSONObject(jsonRequest, NAME,              nickname);
        utility.setStringToJSONObject(jsonRequest, TOKEN,             tokenValue);
        utility.setStringToJSONObject(jsonRequest, CARD_TYPE,         cardType);
        utility.setStringToJSONObject(jsonRequest, EXPIRATION,        expiry);
        utility.setStringToJSONObject(jsonRequest, ZIP_CODE,          zip);
        utility.setStringToJSONObject(jsonRequest, LAST_FOUR,         lastFour);
        isLoadingBase = true;
        mProgressDialog.show();
        onTaskCompletedFlag = 0;

        mGetDataTask = new GetDataTask(url ,jsonRequest, this, POST);
        mGetDataTask.execute();


    }

    @OnClick(R.id.scanImageButton)
    public void onCardScanPress(){
        Intent scanIntent = new Intent(this, CardIOActivity.class);

        // customize these values to suit your needs.
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_RESTRICT_POSTAL_CODE_TO_NUMERIC_ONLY, false); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CARDHOLDER_NAME, false); // default: false

        // hides the manual entry button
        // if set, developers should provide their own manual entry mechanism in the app
        scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, false); // default: false

        // matches the theme of your application
        scanIntent.putExtra(CardIOActivity.EXTRA_KEEP_APPLICATION_THEME, false); // default: false

        // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
        startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String resultStr;
        if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
            CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

            // Never log a raw card number. Avoid displaying it, but if necessary use getFormattedCardNumber()
            resultStr = "Card Number: " + scanResult.getRedactedCardNumber() + "\n";
            cardNumberEditText.setText(scanResult.cardNumber);
            cardExpiryEditText.setText(scanResult.expiryMonth + "/" + (scanResult.expiryYear-2000));
            cvvEditText.setText(scanResult.cvv);
            zipEditText.setText(scanResult.postalCode);


            // Do something with the raw number, e.g.:
            // myService.setCardNumber( scanResult.cardNumber );

            if (scanResult.isExpiryValid()) {
                resultStr += "Expiration Date: " + scanResult.expiryMonth + "/" + scanResult.expiryYear + "\n";
            }

            if (scanResult.cvv != null) {
                // Never log or display a CVV
                resultStr += "CVV has " + scanResult.cvv.length() + " digits.\n";
            }

            if (scanResult.postalCode != null) {
                resultStr += "Postal Code: " + scanResult.postalCode + "\n";
            }

            if (scanResult.cardholderName != null) {
                resultStr += "Cardholder Name : " + scanResult.cardholderName + "\n";
            }
        } else {
            resultStr = "Scan was canceled.";
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        cardType = cardTypeModels.get(i).getItemType();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onTaskSuccess(JSONObject jsonResponse) {
        isLoadingBase = false;
        mProgressDialog.dismiss();
        if(onTaskCompletedFlag == 0){
            utility.showAlertDialog(this,"Added funding source successfully!");
        }
        if(onTaskCompletedFlag == 1){
            Log.d(APP_NAME, jsonResponse.toString());
            try {
                tokenValue = jsonResponse.getString("token_value");
                addFundingSource();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onTaskError(String msg) {

        isLoadingBase = false;
        mProgressDialog.dismiss();
        utility.showAlertDialog(this,msg);

    }
}
