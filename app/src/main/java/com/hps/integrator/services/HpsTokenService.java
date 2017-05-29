package com.hps.integrator.services;


import com.hps.integrator.entities.credit.HpsCreditCard;
import java.io.IOException;


public class HpsTokenService {
    private String mPublicKey;
    private String mUrl;

    public HpsTokenService(String publicKey) {
        mPublicKey = publicKey;

        if (publicKey == null) {
            throw new IllegalArgumentException("publicKey can not be null");
        }

        String[] components = mPublicKey.split("_");

        if (components.length < 3) {
            throw new IllegalArgumentException("publicKey format invalid");
        }

        String env = components[1].toLowerCase();

        if (env.equals("prod")) {
            mUrl = "https://api2.heartlandportico.com/SecureSubmit.v1/api/token";
        } else {
            mUrl = "https://cert.api2.heartlandportico.com/Hps.Exchange.PosGateway.Hpf.v1/api/token";
        }
    }
    public String getmUrl(){
        return this.mUrl;
    }

    public String getmPublicKey(){
        return this.mPublicKey;
    }


    public void getToken(HpsCreditCard card) throws IOException {
//        HttpsURLConnection conn = (HttpsURLConnection) new URL(mUrl).openConnection();
//        JSONObject result;
//
//        byte[] creds = String.format("%s:", mPublicKey).getBytes();
//        String auth = String.format("Basic %s", Base64.encode(creds, Base64.URL_SAFE));
//
//        JSONObject cardJson = new JSONObject();
//        utility.setStringToJSONObject(cardJson, "number", card.getNumber());
//        utility.setStringToJSONObject(cardJson, "cvc", card.getCvv());
//        utility.setIntToJSONObject(cardJson, "exp_month", card.getExpMonth());
//        utility.setIntToJSONObject(cardJson, "exp_year", card.getExpYear());
//        JSONObject jsonObject = new JSONObject();
//        utility.setStringToJSONObject(jsonObject, "object", "token");
//        utility.setStringToJSONObject(jsonObject, "token_type", "supt");
//        utility.setJSONObjectToJSONObject(jsonObject, "card", cardJson);
//
//        String payload = jsonObject.toString();
//
//        byte[] bytes = payload.getBytes();



//        conn.setDoOutput(true);
//        conn.setDoInput(true);
//        conn.setRequestMethod("POST");
//        conn.addRequestProperty("Authorization", auth);
//        conn.addRequestProperty("Content-Type", "application/json");
//        conn.addRequestProperty("Content-Length", String.format("%s", bytes.length));
//
////        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
////        wr.write(payload);
////        wr.flush();
////        wr.close();
//        DataOutputStream requestStream = new DataOutputStream(conn.getOutputStream());
//        requestStream.write(bytes);
//        requestStream.flush();
//        requestStream.close();
//
//        try {
//            InputStream responseStream = conn.getInputStream();
//            result = convertInputStreamToJSON(responseStream);
//            responseStream.close();
//        } catch (IOException e) {
//            if (conn.getResponseCode() == 400) {
//                InputStream errorStream =conn.getErrorStream();
//                result = convertInputStreamToJSON(errorStream);
//                errorStream.close();
//            } else {
//                throw new IOException(e);
//            }
//        }
//
//        return result;
//    }
//
//    private JSONObject convertInputStreamToJSON(InputStream inputStream) throws IOException {
//        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
//        String line;
//        String result = "";
//        while((line = bufferedReader.readLine()) != null)
//            result += line;
//
//        inputStream.close();
//
//        JSONObject jObject= null;
//
//        try {
//            jObject = new JSONObject(result);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return jObject;
//    }
    }
}
