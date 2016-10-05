package com.balamurugan.marshmallowsdfix;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Balamurugan M on 6/17/2016.
 */
public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.MyViewHolder> {

    private List<ListItem> appsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView appName;
        public ImageView appIcon;
        public CheckBox chkSelected;
        private SwitchCompat swt;

        public MyViewHolder(View view) {
            super(view);
            appIcon = (ImageView) view.findViewById(R.id.imageView);
            swt = (SwitchCompat) view.findViewById(R.id.Switch);
            swt.setOnClickListener(null);
        }
    }


    public MyListAdapter(List<ListItem> appsList2) {
        this.appsList = appsList2;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ListItem listItem = appsList.get(position);
        holder.swt.setText(listItem.getName());
        holder.appIcon.setImageDrawable(listItem.getIcon());
        holder.swt.setChecked(listItem.isSelected());
        holder.swt.setTag(appsList.get(position));
        //holder.chkSelected.setChecked(listItem.isSelected());
        //holder.chkSelected.setTag(appsList.get(position));
    }

    @Override
    public int getItemCount() {
        return appsList.size();
    }
}
