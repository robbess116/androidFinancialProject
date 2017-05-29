package com.sproutonecard.rechargeandreward.model;

import org.json.JSONObject;

/**
 * Created by richman on 11/1/16.
 */

public class ExpandableListViewPurchaseItemModel {
    private String itemName;
    private String itemAmount;
    private Boolean checked;
    private String itemId;
    /****************** Set Methods *********************/

    public void setItemAmount(String itemAmount) {
        this.itemAmount = itemAmount;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }
    public void setItemId(String itemId){
        this.itemId = itemId;
    }

    /****************** Get Methods *********************/

    public String getItemAmount() {
        return this.itemAmount;
    }

    public String getItemName() {
        return this.itemName;
    }

    public Boolean getChecked() {
        return checked;
    }


    public String getItemID() {
        return this.itemId;
    }
}