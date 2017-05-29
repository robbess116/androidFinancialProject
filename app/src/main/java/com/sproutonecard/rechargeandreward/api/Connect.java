package com.sproutonecard.rechargeandreward.api;

import android.util.Log;
import android.webkit.CookieManager;


import com.sproutonecard.rechargeandreward.AppConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static com.sproutonecard.rechargeandreward.utility.Utility.utility;

public class Connect implements HttpUrlManager, AppConfig {
    private static Connect mSharedInstance = null;

    public static Connect getInstance() {

        if (mSharedInstance == null) {
            mSharedInstance = new Connect();
        }
        return mSharedInstance;
    }

    public JSONObject getJSONObject(String strUrl, JSONObject jsonParam, String requestMethod) {
        JSONObject result = null;
        HttpURLConnection connection = null;

        Log.d(APP_NAME, "URL : " + strUrl);



        try {
            URL url = new URL(strUrl);
            connection = (HttpURLConnection)url.openConnection();
            CookieManager cookieManager = CookieManager.getInstance();
            String cookie = cookieManager.getCookie(connection.getURL().toString());
            if (cookie != null) {
                connection.setRequestProperty("Cookie", cookie);
            }
            if(jsonParam != null){
                Log.d(APP_NAME, "Request : " + jsonParam.toString());
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setUseCaches(false);
                connection.setRequestMethod(requestMethod);
                connection.setRequestProperty("Content-Type", CONTENT_TYPE);
                connection.setRequestProperty("Accept", ACCEPT);
                connection.connect();

                OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                wr.write(jsonParam.toString());
                wr.flush();
                wr.close();
            }else{

                connection.setRequestMethod(requestMethod);
                connection.setRequestProperty("Content-Type", CONTENT_TYPE);
                connection.setRequestProperty("Accept", ACCEPT);
                connection.connect();

            }

            // Get cookies from responses and save into the cookie manager
            List cookieList = connection.getHeaderFields().get("Set-Cookie");
            if (cookieList != null) {
                for (Object cookieTemp : cookieList) {
                    cookieManager.setCookie(connection.getURL().toString(), cookieTemp.toString());
                }
            }

            InputStream ins;
            int statusCode = connection.getResponseCode();
            switch (statusCode){
                case 200:
                    ins = connection.getInputStream();
                    result = convertInputStreamToJSON(ins);
                    ins.close();
                    break;
                case 201:
                    ins = connection.getInputStream();
                    result = convertInputStreamToJSON(ins);
                    ins.close();
                    break;
                case 400:
                    result = new JSONObject();
                    utility.setStringToJSONObject(result, MESSAGE,              "400(Bad Request)\n Response Error!");
                    break;
                case 401:
                    result = new JSONObject();
                    utility.setStringToJSONObject(result, MESSAGE,              "401(Unauthorized)\n Invalid Email or Password!");
                    break;
                case 404:
                    result = new JSONObject();
                    utility.setStringToJSONObject(result, MESSAGE,              "404(Not Found)\n Response Error!");
                    break;
                case 406:
                    result = new JSONObject();
                    utility.setStringToJSONObject(result, MESSAGE,              "406(Not Acceptable)\n Response Error!");
                    break;
                case 411:

                    utility.setStringToJSONObject(result, MESSAGE,              "411(Length Required)\n Response Error!");
                    break;
                case 412:
                    result = new JSONObject();
                    utility.setStringToJSONObject(result, MESSAGE,              "412(Precondition)\n Response Error!");
                    break;
                case 413:
                    result = new JSONObject();
                    utility.setStringToJSONObject(result, MESSAGE,              "413(Request Entity Too Large)\n Response Error!");
                    break;
                case 415:
                    result = new JSONObject();
                    utility.setStringToJSONObject(result, MESSAGE,              "415(Unsupported Media Type)\n Response Error!");
                    break;
                case 422:
                    ins = connection.getErrorStream();
                    result = convertInputStreamToJSON(ins);
                    ins.close();
                    break;
                default:
                    ins = connection.getErrorStream();
                    result = convertInputStreamToJSON(ins);
                    ins.close();
                    break;

            }




            connection.disconnect();
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }finally {
            connection.disconnect();
        }

        Log.d(APP_NAME, result == null ? "Response : NULL": "Response : " + result.toString());

        return result;
    }



    public JSONObject getJSONObjectHpsToken(String strUrl, String auth, byte[] bytes, String requestMethod) {
        JSONObject result = null;
        HttpURLConnection connection = null;

        Log.d(APP_NAME, "URL : " + strUrl);



        try {
            URL url = new URL(strUrl);
            connection = (HttpURLConnection)url.openConnection();

            if(bytes != null){
                Log.d(APP_NAME, "Request : " + bytes.toString());
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestMethod("POST");
                connection.addRequestProperty("Authorization", auth);
                connection.addRequestProperty("Content-Type", "application/json");
                connection.addRequestProperty("Content-Length", String.format("%s", bytes.length));

//        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
//        wr.write(payload);
//        wr.flush();
//        wr.close();
                DataOutputStream requestStream = new DataOutputStream(connection.getOutputStream());
                requestStream.write(bytes);
                requestStream.flush();
                requestStream.close();
            }

            try {
                InputStreamReader responseStream = new InputStreamReader(connection.getInputStream());
                //result = gson.fromJson(responseStream, HpsToken.class);
                BufferedReader bufferedReader = new BufferedReader(responseStream);
                String line;
                String resultToken = "";
                while((line = bufferedReader.readLine()) != null)
                    resultToken += line;

                try {
                    result = new JSONObject(resultToken);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                responseStream.close();
            } catch (IOException e) {
                if (connection.getResponseCode() == 400) {
                    InputStreamReader errorStream = new InputStreamReader(connection.getErrorStream());
                    //result = gson.fromJson(errorStream, HpsToken.class);
                    BufferedReader bufferedReader = new BufferedReader(errorStream);
                    String line;
                    String resultToken = "";
                    while((line = bufferedReader.readLine()) != null)
                        resultToken += line;

                    try {
                        result = new JSONObject(resultToken);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    errorStream.close();
                } else {
                    throw new IOException(e);
                }
            }


        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }finally {
            connection.disconnect();
        }

        Log.d(APP_NAME, result == null ? "Response : NULL": "Response : " + result.toString());

        return result;
    }

    private static JSONObject convertInputStreamToJSON(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line;
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();

        JSONObject jObject= null;

        try {
            jObject = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jObject;
    }
}
