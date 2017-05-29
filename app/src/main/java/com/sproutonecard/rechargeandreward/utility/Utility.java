package com.sproutonecard.rechargeandreward.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.sproutonecard.rechargeandreward.AppConfig;
import com.sproutonecard.rechargeandreward.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sproutonecard.rechargeandreward.R;
import com.sproutonecard.rechargeandreward.api.HttpUrlManager;

/**
 * Created by richman on 10/31/16.
 */

public class Utility implements AppConfig, HttpUrlManager {
    public static Utility utility = null;

    public static Utility getInstance() {
        if (utility == null) {
            utility = new Utility();
        }

        return utility;
    }

    //    Interface Process
    @SuppressWarnings("ConstantConditions")
    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void setupUI(View view, final Activity activity) {
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(activity);
                    return false;
                }
            });
        }
        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView, activity);
            }
        }
    }

    public Boolean isValidEmail(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    //    ******************************* AlertDialog *******************************
    public AlertDialog showAlertDialog(Context context, String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context).setCancelable(false);
        TextView alertMsg = new TextView(context);
        alertMsg.setText(message);
        alertMsg.setTextSize(18);
        alertMsg.setPadding(16, 64, 16, 64);
        alertMsg.setGravity(Gravity.CENTER_HORIZONTAL);
        dialog.setView(alertMsg);

        final AlertDialog alert = dialog.create();
        alert.show();

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (alert.isShowing()) {
                    alert.dismiss();
                }
            }
        };

        handler.postDelayed(runnable, ALERT_TIME);

        return alert;
    }

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    //    Get Device Screen Size
    public Point getDeviceScreenSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    //    Bitmap Process
    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public String compressBitmap(Bitmap bitmap) {
        int width, height, scale;
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        if (width > height) {
            scale = (int) (MAX_WIDTH * 100.0 / width);
        } else {
            scale = (int) (MAX_HEIGHT * 100.0 / height);
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, scale, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static Bitmap base64ToBitmap(String b64) {
        byte[] imageAsBytes = Base64.decode(b64.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }

    public String videoToBase64(Uri uri, Context context) {
        String result = "";
        try {
            InputStream iStream = context.getContentResolver().openInputStream(uri);
            byte[] inputData = getBytes(iStream);
            result = Base64.encodeToString(inputData, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public Bitmap createThumbnailFromVideoUri(Uri uri) {
        return ThumbnailUtils.createVideoThumbnail(uri.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
    }

    public File base64ToVideo(String encodedString) {
        byte[] decodedBytes = Base64.decode(encodedString.getBytes(), Base64.DEFAULT);
        File file = null;
        try {
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "KamiwazaMedia/tempVideo.mp4");
            FileOutputStream out = new FileOutputStream(file);
            out.write(decodedBytes);
            out.close();
        } catch (Exception e) {
            Log.e("Error", e.toString());

        }

        return file;
    }

    //    Find View By Tag
    public View getViewByTag(ViewGroup root, String tag) {
        int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = root.getChildAt(i);
            if (child instanceof ViewGroup) {
                View view = getViewByTag((ViewGroup) child, tag);
                if (view != null) {
                    return view;
                }
            }

            Object object = child.getTag();
            if (object != null) {
                String tagObj = String.valueOf(object);
                if (tagObj.equals(tag)) {
                    return child;
                }
            }
        }

        return null;
    }

    public String md5(String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getMediaUrl(String url) {
        if (url.startsWith("bo_media_user")) {
            url = url.replace("bo_media_user_", "");
            return URL_MEDIA_USERS + url;
        }

        if (url.startsWith("bo")) {
            url = url.replace("bo_", "");
            String[] params = url.split("_");
            if (params[0].startsWith("avatar")) {
                return URL_MEDIA_USERS + params[1];
            } else {
                StringBuilder sb = new StringBuilder();

                int startIndex = 2;

                if (url.contains("thumb"))
                    startIndex = 3;

                for (int i = startIndex; i < params.length; i++) {
                    sb.append(params[i]);
                    if (i != params.length - 1) {
                        sb.append("_");
                    }
                }
                String path = null;
                switch (params[1]) {
                    case "photo":
                        path = URL_MEDIA_PHOTOS;
                        break;
                    case "video":
                        path = URL_MEDIA_VIDEOS;
                        break;
                }

                if (url.contains("thumb"))
                    path = URL_MEDIA_VIDEO_THUMB;

                url = path + sb.toString();
            }
        }
        return url;
    }

    public String getTimeDiff(long time_diff) {
        String result;
        int months, days, hours, minutes, seconds;
        seconds = (int) (time_diff % 60);
        time_diff = (time_diff - seconds) / 60;
        minutes = (int) (time_diff % 60);
        time_diff = (time_diff - minutes) / 60;
        hours = (int) (time_diff % 24);
        time_diff = (time_diff - hours) / 24;
        days = (int) (time_diff % 30);
        time_diff = (time_diff - days) / 30;
        months = (int) time_diff;

        if (months > 0) {
            result = String.valueOf(months) + "month" + (months > 1 ? "s" : "") + " ago";
        } else if (days > 0) {
            result = String.valueOf(days) + "d " + String.valueOf(hours) + "h";
        } else if (hours > 0) {
            result = String.valueOf(hours) + "h " + String.valueOf(minutes) + "m";
        } else if (minutes > 0) {
            result = String.valueOf(minutes) + "m";
        } else {
            result = "1m";
        }

        return result;
    }

    // SharedPreference
    public void setIntToSharedPreference(Context context, String key, int val) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, val);
        editor.apply();
    }

    public int getIntFromSharedPreference(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, 0);
    }

    public void setStringToSharedPreference(Context context, String key, String val) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, val);
        editor.apply();
    }

    public String getStringFromSharedPreference(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }

    public void setLongToSharedPreference(Context context, String key, long val) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, val);
        editor.apply();
    }

    public long getLongFromSharedPreference(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(key, -1);
    }

    public void setBooleanToSharedPreference(Context context, String key, boolean val) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, val);
        editor.apply();
    }

    public boolean getBooleanFromSharedPreference(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, false);
    }

    public void setJSONObjectToSharedPreference(Context context, String key, JSONObject val) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, val == null ? null : val.toString());
        editor.apply();
    }

    public JSONObject getJSONObjectFromSharedPreference(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        String strResult = sharedPreferences.getString(key, null);
        JSONObject result = null;
        if (strResult != null) {
            try {
                result = new JSONObject(strResult);
            } catch (JSONException e) {
                Log.d(TAG, e.getMessage());
            }
        }

        return result;
    }

    public void setJSONArrayToSharedPreference(Context context, String key, JSONArray val) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, val == null ? null : val.toString());
        editor.apply();
    }

    public JSONArray getJSONArrayFromSharedPreference(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        String s = sharedPreferences.getString(key, null);
        JSONArray result = null;
        if (s != null) {
            try {
                result = new JSONArray(s);
            } catch (JSONException e) {
                Log.d(TAG, e.getMessage());
            }
        }

        return result;
    }

    //    ***************************************** JSONArray *****************************************
    public JSONArray reverseJSONArray(JSONArray jsonArray) {
        JSONArray result = new JSONArray();
        for (int i = jsonArray.length() - 1; i >= 0; i--) {
            try {
                result.put(jsonArray.get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public JSONArray addJSONObjectToJSONArray(JSONArray jsonArray, String jsonObject) {
        if (jsonArray == null) {
            jsonArray = new JSONArray();
        }

        jsonArray.put(jsonObject);

        return jsonArray;
    }

    public JSONArray addJSONObjectToJSONArray(JSONArray jsonArray, JSONObject jsonObject, int index) {
        if (jsonArray == null) {
            jsonArray = new JSONArray();
        }

        try {
            jsonArray.put(index, jsonObject);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }

        return jsonArray;
    }

    public JSONArray addJSONArrayToJSONArray(JSONArray jsonArray, JSONArray jArray) {
        if (jsonArray == null) jsonArray = new JSONArray();

        for (int i = 0; i < jArray.length(); i++) {
            jsonArray.put(getJSONObjectFromJSONArray(jArray, i));
        }

        return jsonArray;
    }

    public JSONArray removeJSONObjectFromJSONArray(JSONArray jsonArray, int index) {
        JSONArray result = new JSONArray();
        for (int i = 0; i < jsonArray.length(); i++) {
            if (i != index) {
                try {
                    result.put(jsonArray.getJSONObject(i));
                } catch (JSONException e) {
                    Log.d(TAG, e.getMessage());
                }
            }
        }

        return result;
    }

    public boolean isExistInJSONArray(JSONArray jsonArray, String key, Object val) {
        boolean result = false;

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = getJSONObjectFromJSONArray(jsonArray, i);
            Object object = getObjectFromJSONObject(jsonObject, key);

            if (object.toString().equals(val.toString())) {
                result = true;
                break;
            }
        }

        return result;
    }

    //    ***************************************** JSONObject *****************************************
    public JSONObject getJSONObjectFromJSONObject(JSONObject jsonObject, String key) {
        JSONObject result = null;

        try {
            result = jsonObject.getJSONObject(key);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }

        return result;
    }

    public JSONArray getJSONArrayFromJSONObject(JSONObject jsonObject, String key) {
        JSONArray result = null;

        try {
            result = jsonObject.getJSONArray(key);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }

        return result;
    }

    public String getStringFromJSONObject(JSONObject jsonObject, String key) {
        String result = null;
        try {
            result = jsonObject.getString(key);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }

        return result;
    }

    public void setStringToJSONObject(JSONObject jsonObject, String key, String val) {
        try {
            jsonObject.put(key, val);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public int getIntFromJSONObject(JSONObject jsonObject, String key) {
        int result = -1;
        try {
            result = jsonObject.getInt(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public void setIntToJSONObject(JSONObject jsonObject, String key, int val) {
        try {
            jsonObject.put(key, val);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public float getFloatFromJSONObject(JSONObject jsonObject, String key) {
        float result = -1;
        try {
            result = (float) jsonObject.getDouble(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public double getDoubleFromJSONObject(JSONObject jsonObject, String key) {
        double result = -1;
        try {
            result = jsonObject.getDouble(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public void setDoubleToJSONObject(JSONObject jsonObject, String key, double val) {
        try {
            jsonObject.put(key, val);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public long getLongFromJSONObject(JSONObject jsonObject, String key) {
        long result = -1;
        try {
            result = jsonObject.getLong(key);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }

        return result;
    }

    public void setLongToJSONObject(JSONObject jsonObject, String key, long val) {
        try {
            jsonObject.put(key, val);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public boolean getBooleanFromJSONObject(JSONObject jsonObject, String key) {
        boolean result = false;
        try {
            result = jsonObject.getBoolean(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public Object getObjectFromJSONObject(JSONObject jsonObject, String key) {
        Object result = null;
        try {
            result = jsonObject.get(key);
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }

        return result;
    }

    public JSONObject getJSONObjectFromJSONArray(JSONArray jsonArray, int index) {
        JSONObject result = null;
        try {
            result = jsonArray.getJSONObject(index);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }

        return result;
    }

    public boolean isUserLoggedIn(Context context) {
        JSONObject jsonObject = getJSONObjectFromSharedPreference(context, CURRENT_USER);
        if (jsonObject != null) {
            AppController.currentUser = jsonObject;
            Log.d(TAG, "Current User : " + jsonObject.toString());
            return true;
        } else {
            return false;
        }
    }

    public void logOut(Context context) {
        setJSONObjectToSharedPreference(context, CURRENT_USER, null);
    }

    //    View Color Change (Drawable, Text)
    public void changeDrawableColor(View view, boolean enabled, Context context) {
        if (view instanceof Button) {
            Drawable[] drawables = ((Button) view).getCompoundDrawables();

            for (Drawable drawable : drawables) {
                if (drawable != null) {
                    drawable.setColorFilter(ContextCompat.getColor(context, enabled ? R.color.colorPrimary : R.color.colorGrayText), PorterDuff.Mode.SRC_ATOP);
                }

            }

//            ((Button) view).setTextColor(ContextCompat.getColor(context, enabled ? R.color.colorPrimary : R.color.colorGrayDark));

            return;
        }

//        if (view instanceof TextView) {
//            Drawable[] drawables = ((TextView) view).getCompoundDrawables();
//
//            for (Drawable drawable : drawables) {
//                if (drawable != null)
//                    drawable.setColorFilter(ContextCompat.getColor(context, enabled ? R.color.colorPrimary : R.color.colorGrayDark), PorterDuff.Mode.SRC_ATOP);
//            }
//
//            ((TextView) view).setTextColor(ContextCompat.getColor(context, enabled ? R.color.colorPrimary : R.color.colorGrayDark));
//        }
    }


    /**************************************************************
     * getting from com.google.zxing.client.android.encode.QRCodeEncoder
     * <p>
     * See the sites below
     * http://code.google.com/p/zxing/
     * http://code.google.com/p/zxing/source/browse/trunk/android/src/com/google/zxing/client/android/encode/EncodeActivity.java
     * http://code.google.com/p/zxing/source/browse/trunk/android/src/com/google/zxing/client/android/encode/QRCodeEncoder.java
     */

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    public Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException {
        String contentsToEncode = contents;
        if (contentsToEncode == null) {
            return null;
        }

        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contentsToEncode);
        if (encoding != null) {
            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }

        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contentsToEncode, format, img_width, img_height, null);

        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();

        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);


        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }

    public void setJSONArrayToJSONObject(JSONObject jsonObject, String key, JSONArray val) {
        try {
            jsonObject.put(key, val);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void setJSONObjectToJSONArray(JSONArray jsonArray, JSONObject jsonObject) {
        jsonArray.put(jsonObject);
    }

    public void setJSONObjectToJSONObject(JSONObject jsonObject, String key, JSONObject object) {
        try {
            jsonObject.put(key,object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

