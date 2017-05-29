package com.sproutonecard.rechargeandreward.model;


import java.util.ArrayList;

public class ExpandableListViewTransactionModel {
    private String date;
    private String type;
    private String amount;
    private boolean checked;

    //    ArrayList to store child objects
    private ArrayList<ExpandableListViewPurchaseItemModel> purchaseItemModels;

    /****************** Set Methods *******************/
    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setType(String type) {
        this.type = type;
    }


    /****************** Get Methods *******************/
    public String getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    public String getType() {
        return type;
    }
    /****************** Checking Methods *******************/

    public boolean isChecked(){
        return checked;
    }
    public void setChecked( boolean checked){
        this.checked = checked;
    }

    /****************** ArrayList to store child objects *******************/

    public ArrayList<ExpandableListViewPurchaseItemModel> getPurchaseItemModels() {
        return this.purchaseItemModels;
    }

    public void setPurchaseItemModels(ArrayList<ExpandableListViewPurchaseItemModel> purchaseItemModels) {
        this.purchaseItemModels = purchaseItemModels;
    }

}


