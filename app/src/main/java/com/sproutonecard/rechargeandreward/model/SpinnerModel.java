package com.sproutonecard.rechargeandreward.model;

/**
 * Created by richman on 10/30/16.
 */

public class SpinnerModel {
    private String ItemName="";
    private String ItemType="";
    private String Url="";

    /***************** Set Methods **********************/
    public void setItemName(String ItemName){
        this.ItemName = ItemName;
    }

    public void setItemType(String ItemType){
        this.ItemType = ItemType;
    }

    public void setUrl(String Url){
        this.Url = Url;
    }
    /****************** Get Methods**********************/
    public String getItemName() {
        return this.ItemName;
    }

    public String getItemType() {
        return this.ItemType;
    }

    public String getUrl() {
        return this.Url;
    }
}
