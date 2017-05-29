package com.sproutonecard.rechargeandreward.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.sproutonecard.rechargeandreward.R;
import com.sproutonecard.rechargeandreward.model.ExpandableListViewPurchaseItemModel;
import com.sproutonecard.rechargeandreward.model.SpinnerModel;

import java.util.ArrayList;

/**
 * Created by richman on 11/2/16.
 */

public class CustomListViewAdapter extends BaseAdapter {
    private ArrayList<ExpandableListViewPurchaseItemModel> selectedTransaction;
    private ExpandableListViewPurchaseItemModel tempValues;
    private Context context;
    private LayoutInflater mInflater;

    public  CustomListViewAdapter(Context context, ArrayList<ExpandableListViewPurchaseItemModel> selectedTransaction) {
        this.context = context;
        this.selectedTransaction = selectedTransaction;
        this.mInflater = (LayoutInflater.from(context));
    }
    @Override
    public int getCount() {
        return this.selectedTransaction.size();
    }

    @Override
    public Object getItem(int i) {
        return this.selectedTransaction.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = mInflater.inflate(R.layout.refund_transaction_item, viewGroup, false);
        tempValues = null;
        tempValues = (ExpandableListViewPurchaseItemModel) getItem(i);
        if(tempValues.getItemName() == "none"){
            view.setVisibility(View.GONE);

        }else{

            TextView nameTextView = (TextView)view.findViewById(R.id.textViewRefundedPuchaseItemName);
            nameTextView.setText(tempValues.getItemName());
            TextView amountTextView = (TextView) view.findViewById(R.id.textViewRefundedPurchaseItemAmount);
            amountTextView.setText(tempValues.getItemAmount());
            ImageView selectedImageView =  (ImageView) view.findViewById(R.id.imageButtonRefundedItemSelected);
            if(tempValues.getChecked()){
                selectedImageView.setImageResource(R.drawable.ic_selectonetime);

            }else{
                selectedImageView.setImageResource(R.drawable.ic_onetime);
            }

        }


        return view;
    }

    @Override
    public  void notifyDataSetChanged(){
//        Refresh List Rows
        super.notifyDataSetChanged();
    }
}
