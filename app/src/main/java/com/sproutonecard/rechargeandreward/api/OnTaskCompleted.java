package com.sproutonecard.rechargeandreward.api;

import org.json.JSONObject;

public interface OnTaskCompleted {
    void onTaskSuccess(JSONObject jsonResponse);
    void onTaskError  (String msg);
}
