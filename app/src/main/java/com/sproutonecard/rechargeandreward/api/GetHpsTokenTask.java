package com.sproutonecard.rechargeandreward.api;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import static com.sproutonecard.rechargeandreward.AppConfig.MESSAGE;

/**
 * Created by richman on 11/10/16.
 */

public class GetHpsTokenTask extends AsyncTask<Void, Void, JSONObject> {
    private OnTaskCompleted listener;
    private String url;
    private String auth;
    private String requestMethod;
    private String msg;
    private byte[] bytes;

    public GetHpsTokenTask(String url,String auth, byte[] bytes, OnTaskCompleted listener,String requestMethod) {
        this.listener = listener;
        this.url = url;
        this.bytes = bytes;
        this.requestMethod = requestMethod;
        this.auth = auth;
    }
    @Override
    protected JSONObject doInBackground(Void... voids) {
        JSONObject jsonResponse, result = null;

        jsonResponse = Connect.getInstance().getJSONObjectHpsToken(url, auth,  bytes,  requestMethod);
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
