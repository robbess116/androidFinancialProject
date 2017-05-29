package com.sproutonecard.rechargeandreward.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sproutonecard.rechargeandreward.R;
import com.sproutonecard.rechargeandreward.model.SpinnerModel;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by richman on 10/30/16.
 */

public class CustomSpinnerAdapter extends BaseAdapter {
    private Context context;
    private ArrayList data;
    public Resources res;
    SpinnerModel tempValues = null;
    LayoutInflater inflater;




    /********************* CustomSpinnerAdapter Constructor **************************/
    public CustomSpinnerAdapter(Context applicationContext, ArrayList objects, Resources resLocal){
        this.context = applicationContext;
        this.data = objects;
        this.res = resLocal;
        inflater = (LayoutInflater.from(applicationContext));

    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.oan_item_text_view, null);
        tempValues = null;
        tempValues = (SpinnerModel) data.get(i);
        TextView nameTextView = (TextView)view.findViewById(R.id.oanNameTextView);
        nameTextView.setText(tempValues.getItemName());
        return view;
    }
    @Override
    public  void notifyDataSetChanged(){
//        Refresh List Rows
        super.notifyDataSetChanged();
    }
}
