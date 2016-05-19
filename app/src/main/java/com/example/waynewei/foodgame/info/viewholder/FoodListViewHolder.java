package com.example.waynewei.foodgame.info.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.waynewei.foodgame.R;

/**
 * Created by waynewei on 2015/12/13.
 */
public class FoodListViewHolder extends RecyclerView.ViewHolder{

	public final RelativeLayout item;
	public final TextView title;
	public final ImageView photo;

	public FoodListViewHolder(View itemView) {
		super(itemView);
		item = (RelativeLayout) itemView.findViewById(R.id.list_item);
		title = (TextView) itemView.findViewById(R.id.title);
		photo = (ImageView) itemView.findViewById(R.id.photo);
	}
}
