package com.sproutonecard.rechargeandreward.ui.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.sproutonecard.rechargeandreward.R;
import com.sproutonecard.rechargeandreward.api.GetDataTask;
import com.sproutonecard.rechargeandreward.api.OnTaskCompleted;
import org.json.JSONObject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConfirmPhonePinActivity extends BaseActivity implements OnTaskCompleted {

    @BindView(R.id.pinNumberEditText_verityUser)EditText pinEditText;
    @BindView(R.id.phoneNumberEditText_verifyUser)EditText phoneNumberEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_phone_pin);
        ButterKnife.bind(this);
    }
    @OnClick(R.id.submitButton_verifyUser)
    public void onSubmitVerify(){

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
        pinEditText.setError(null);
        phoneNumberEditText.setError(null);
        String pin = pinEditText.getText().toString().trim();
        String phoneNumber = phoneNumberEditText.getText().toString().trim();
        if(pin.isEmpty()){
            pinEditText.setError(getString(R.string.error_field_required));
            focusView = pinEditText;
            focusView.requestFocus();
            return;
        }else if(phoneNumber.isEmpty()){
            phoneNumberEditText.setError(getString(R.string.error_field_required));
            focusView = phoneNumberEditText;
            focusView.requestFocus();
            return;
        }


        JSONObject jsonRequest = new JSONObject();
        utility.setStringToJSONObject(jsonRequest, MOBILE_PIN,              pin);
        utility.setStringToJSONObject(jsonRequest, MOBILE_PHONE,           phoneNumber);
        String url = URL_SERVER + "/user/";

        isLoadingBase = true;
        mProgressDialog.show();

        mGetDataTask = new GetDataTask(url,jsonRequest, this, POST);
        mGetDataTask.execute();



    }
    @OnClick(R.id.cancelButton_verifyUser)
    public void onCancelVerifyUser(){
        navToNextAndFinish(this, RegisterNewUserActivity.class);
    }

    @Override
    public void onTaskSuccess(JSONObject jsonResponse) {
        isLoadingBase = false;
        mProgressDialog.dismiss();
        navToNextAndFinish(this, LoginActivity.class);
    }

    @Override
    public void onTaskError(String msg) {
        isLoadingBase = false;
        mProgressDialog.dismiss();

        utility.showAlertDialog(this, msg);

    }
}
