package com.sproutonecard.rechargeandreward.ui.activity;


import android.os.Bundle;
import android.os.Handler;

import com.sproutonecard.rechargeandreward.AppController;
import com.sproutonecard.rechargeandreward.R;

import org.json.JSONObject;


public class SplashActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        onStartSplash();
    }
    private void onStartSplash() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                JSONObject currentUser = utility.getJSONObjectFromSharedPreference(SplashActivity.this, CURRENT_USER);

                if (currentUser != null) {
                    AppController.currentUser = currentUser;
                    navToNextAndFinish(SplashActivity.this, LandingPageActivity.class);
                } else {
                    navToNextAndFinish(SplashActivity.this, LoginActivity.class);
                }


            }
        }, SPLASH_TIME_OUT);
    }

}
