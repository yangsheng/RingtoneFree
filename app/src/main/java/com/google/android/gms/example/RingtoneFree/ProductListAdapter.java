package com.google.android.gms.example.RingtoneFree;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static android.content.ContentValues.TAG;

public class ProductListAdapter extends BaseAdapter {

    private Context mContext;
    private List<Product> mProductList;
    private int pos;
    ViewHolder holder;

    //Constructor


    public ProductListAdapter(Context mContext, List<Product> mProductList) {
        this.mContext = mContext;
        this.mProductList = mProductList;
    }

    @Override
    public int getCount() {
        return mProductList.size();
    }

    @Override
    public Object getItem(int position) {
        return mProductList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(mContext, R.layout.item_product_list,null);

        pos = position;
        holder = new ViewHolder();

        holder.img1 = (ImageView) v.findViewById(R.id.img_receiver);
        holder.img2 = (ImageView) v.findViewById(R.id.img_dot);
        holder.img3 = (ImageView) v.findViewById(R.id.img_setting);
        holder.textView = (TextView) v.findViewById(R.id.tv_name);



        holder.img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String str = holder.textView.getText().toString();
                Intent intent = new Intent(mContext,SettingsActivity.class);
                Log.d("TextView Value",holder.textView.getText().toString());
                intent.putExtra("File_Id", pos);
                mContext.startActivity(intent);
            }
        });
        holder.img3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String str = holder.textView.getText().toString();
                Intent intent = new Intent(mContext,SettingsActivity.class);
                Log.d("TextView Value",holder.textView.getText().toString());
                intent.putExtra("File_Id", pos);
                mContext.startActivity(intent);
            }
        });

        ImageView img_receiver = (ImageView)v.findViewById(R.id.img_receiver);
        TextView tv_name = (TextView)v.findViewById(R.id.tv_name);

        //Set text as "Ringtone1,2,3"
        tv_name.setText("Ringtone  "+ (mProductList.get(position).getText_str()));
        v.setTag(holder);
        return v;

    }

      static class ViewHolder{
        ImageView img1;
        TextView textView;
        ImageView img2;
        ImageView img3;
    }
}
