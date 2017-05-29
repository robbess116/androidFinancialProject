package com.sproutonecard.rechargeandreward;

import android.location.Location;

import com.sproutonecard.rechargeandreward.model.ExpandableListViewPurchaseItemModel;
import com.sproutonecard.rechargeandreward.model.ExpandableListViewTransactionModel;
import com.sproutonecard.rechargeandreward.model.SpinnerModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by richman on 10/31/16.
 */

public class AppController {
    public static JSONObject currentUser = null;
    public static JSONObject currentUserDetails = null;
    public static JSONObject accountDetails = null;
    public static JSONObject selectedAccountTransactionDetails = null;
    public static JSONObject selectedAccountTransactionContents = null;
    public static JSONArray selectedAccountTransactions = null;
    public static JSONArray charities = null;
    public static JSONObject groupList = null;
    public static JSONObject refundTransaction = null;
    public static ExpandableListViewTransactionModel selectedRefundedPurchase = null;
    public static JSONArray accounts = null;
    public static JSONArray funding_sources = null;
    public static String first_name = null;
    public static String last_name = null;
    public static String firstOAN = "virtual card";
    public static ArrayList<SpinnerModel> oanSpinnerModels = null;
    public static boolean isChangedServerData=true;

    public static void initValue(){
        currentUser = null;
        currentUserDetails = null;
        accountDetails = null;
        selectedAccountTransactionDetails = null;
        selectedAccountTransactionContents = null;
        selectedAccountTransactions = null;
        charities = null;
        groupList = null;
        refundTransaction = null;
        selectedRefundedPurchase = null;
        accounts = null;
        funding_sources = null;
        first_name = null;
        last_name = null;
        firstOAN = "virtual card";
        oanSpinnerModels = null;
        isChangedServerData=true;

    }

}
