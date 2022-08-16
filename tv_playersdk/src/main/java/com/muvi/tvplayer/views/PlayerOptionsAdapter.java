package com.muvi.tvplayer.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.muvi.tvplayer.R;

import java.util.ArrayList;

public class PlayerOptionsAdapter extends BaseAdapter {

    private Context mContext;
    int currentSelection;

    public PlayerOptionsAdapter(Context mContext, ArrayList<String> options, int currentSelection) {
        this.mContext = mContext;
        this.options = options;
        this.currentSelection = currentSelection;
    }

    private ArrayList<String> options;

    @Override
    public int getCount() {
        return options.size();
    }

    @Override
    public Object getItem(int position) {
        return options.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_player_options_layout, parent, false);
        }

        TextView textView = view.findViewById(R.id.textView);
        ImageView imageView = view.findViewById(R.id.imageView);

        textView.setText(options.get(position));

        if (position == currentSelection) {
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.GONE);
        }

        return view;
    }
}
