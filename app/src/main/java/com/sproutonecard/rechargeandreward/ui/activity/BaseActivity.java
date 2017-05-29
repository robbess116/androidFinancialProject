package com.sproutonecard.rechargeandreward.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.sproutonecard.rechargeandreward.AppConfig;
import com.sproutonecard.rechargeandreward.R;
import com.sproutonecard.rechargeandreward.api.GetDataTask;
import com.sproutonecard.rechargeandreward.api.HttpUrlManager;
import com.sproutonecard.rechargeandreward.utility.Utility;

public class BaseActivity extends AppCompatActivity  implements AppConfig ,HttpUrlManager{
    public Utility utility = Utility.getInstance();
    public GetDataTask mGetDataTask = null;
    public boolean isLoadingBase;

    public ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        super.onCreate(savedInstanceState);

        mProgressDialog = new ProgressDialog(this, R.style.ProgressDialogTheme);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
    }

    public void navToNextAndFinish(Activity mActivity, Intent intent) {
        startActivity(intent);
        mActivity.finish();

    }

    public void navToNextAndFinish(Activity mActivity, Class c) {
        Intent intent = new Intent(mActivity, c);
        startActivity(intent);
        mActivity.finish();
    }
}
