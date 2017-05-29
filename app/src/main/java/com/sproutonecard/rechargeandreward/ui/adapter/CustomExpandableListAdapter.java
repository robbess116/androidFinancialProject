package com.sproutonecard.rechargeandreward.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sproutonecard.rechargeandreward.AppController;
import com.sproutonecard.rechargeandreward.R;
import com.sproutonecard.rechargeandreward.model.ExpandableListViewPurchaseItemModel;
import com.sproutonecard.rechargeandreward.model.ExpandableListViewTransactionModel;
import com.sproutonecard.rechargeandreward.ui.activity.RequestRefundActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by richman on 11/1/16.
 */

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<ExpandableListViewTransactionModel> transactions;
    LayoutInflater inflater;
    public CustomExpandableListAdapter(Context context,  ArrayList<ExpandableListViewTransactionModel> transactions) {
        this.context = context;
        this.transactions = transactions;
        inflater = (LayoutInflater.from(context));
    }


    @Override
    public int getGroupCount() {

        return this.transactions.size();
    }

    @Override
    public int getChildrenCount(int i) {

        return this.transactions.get(i).getPurchaseItemModels().size();
    }

    @Override
    public Object getGroup(int i) {

        return this.transactions.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {

        return this.transactions.get(i).getPurchaseItemModels().get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        final ExpandableListViewTransactionModel transaction = (ExpandableListViewTransactionModel) getGroup(i);
        if (view == null) {

            view = inflater.inflate(R.layout.transaction_item_layout, null);
        }
        ImageView detailImageView = (ImageView)view.findViewById(R.id.transactionItemDetailImageView);
        if(transaction.isChecked()){
            detailImageView.setImageResource(R.drawable.ic_blackminus);
        }else {
            detailImageView.setImageResource(R.drawable.ic_blackplus);
        }
        TextView dateTextView = (TextView)view.findViewById(R.id.transactionDateTextView);
        dateTextView.setText(transaction.getDate());
        TextView typeTextView = (TextView)view.findViewById(R.id.transactionTypeTextView);
        typeTextView.setText(transaction.getType());
        TextView amoutTextView = (TextView)view.findViewById(R.id.transactionAmount);
        amoutTextView.setText(transaction.getAmount());


        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        final ExpandableListViewTransactionModel transactionModel = this.transactions.get(i);
        final ExpandableListViewPurchaseItemModel purchaseItem = transactionModel.getPurchaseItemModels().get(i1);


        if(purchaseItem.getItemName() == "none"){

                view = inflater.inflate(R.layout.purchase_item_dispute_layout, viewGroup, false);

            Button disputeButton = (Button)view.findViewById(R.id.disputeButton);
            try {
                JSONObject selectedTransaction = AppController.selectedAccountTransactions.getJSONObject(i);
                disputeButton.setOnClickListener(new DisputeButtonListener(transactionModel,selectedTransaction));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else{

                view = inflater.inflate(R.layout.purchase_item_layout, viewGroup, false);


            TextView purchaseItemtextview = (TextView)view.findViewById(R.id.purchaseItemName);
            purchaseItemtextview.setText(purchaseItem.getItemName());

            TextView purchaseAmounttextview = (TextView)view.findViewById(R.id.purchaseItemAmount);
            purchaseAmounttextview.setText(purchaseItem.getItemAmount());
        }



        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
    @Override
    public boolean areAllItemsEnabled(){
        return true;
    }

    /*********************** Dispute Button OnclickListener *********************************/
    private final class DisputeButtonListener implements View.OnClickListener{
        private final  ExpandableListViewTransactionModel selectedTransaction;
        private final JSONObject selectedTransactionJsonObj;

        public DisputeButtonListener(ExpandableListViewTransactionModel transaction, JSONObject selectedTransaction){
            this.selectedTransaction = transaction;


            selectedTransactionJsonObj = selectedTransaction;
        }
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, RequestRefundActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            AppController.selectedRefundedPurchase = this.selectedTransaction;
            AppController.refundTransaction = this.selectedTransactionJsonObj;

            context.startActivity(intent);
            //((Activity)context).finish();


        }
    }
}
