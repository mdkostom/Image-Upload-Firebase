package com.example.imageupload;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

//Responsible for binding the data from the DataClass to the views in the grid layout of the MainActivity.

public class MyAdapter extends BaseAdapter {

    private ArrayList<DataClass> dataList;
    private Context context;
    LayoutInflater layoutInflater;

    public MyAdapter(ArrayList<DataClass> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return dataList.size();
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
        if (layoutInflater == null){
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (view == null){
            view = layoutInflater.inflate(R.layout.grid_item, null);
        }
        ImageView gridImage = view.findViewById(R.id.gridImage);
        TextView gridCaption = view.findViewById(R.id.gridCaption);

        Glide.with(context).load(dataList.get(i).getImageURL()).into(gridImage);
        gridCaption.setText(dataList.get(i).getCaption());

        // Add click listener to each grid item
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch the enlarged image activity
                Intent intent = new Intent(context, EnlargedImageViewActivity.class);
                intent.putExtra("imageUrl", dataList.get(i).getImageURL());
                context.startActivity(intent);
            }
        });

        return view;
    }
}

