package com.sproutonecard.rechargeandreward.api;

import android.os.AsyncTask;

import com.sproutonecard.rechargeandreward.ui.activity.AddFundingSourceActivity;

import org.json.JSONException;
import org.json.JSONObject;

import static com.sproutonecard.rechargeandreward.AppConfig.MESSAGE;

public class GetDataTask extends AsyncTask<Void, Void, JSONObject> {
    private OnTaskCompleted listener;
    private String url;
    private JSONObject jsonRequest;
    private String requestMethod;
    private String msg;
    private byte[] bytes;

    public GetDataTask(String url, JSONObject jsonRequest, OnTaskCompleted listener,String requestMethod) {
        this.listener = listener;
        this.url = url;
        this.jsonRequest = jsonRequest;
        this.requestMethod = requestMethod;
    }



    @Override
    protected JSONObject doInBackground(Void... params) {
        JSONObject jsonResponse, result = null;

        jsonResponse = Connect.getInstance().getJSONObject(url, jsonRequest, requestMethod);




        if (jsonResponse != null) {

               result = jsonResponse;

        } else {
            msg = "Please check your internet connection";
        }

        return result;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        if (result != null) {


                if(result.has(MESSAGE)){
                    try {
                        listener.onTaskError(result.getString(MESSAGE));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        listener.onTaskError("Loading Error!");
                    }
                }else{
                    listener.onTaskSuccess(result);
            }




        }
        else {
            listener.onTaskError(msg);
        }
    }
}
