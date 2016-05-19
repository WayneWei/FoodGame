package com.example.waynewei.foodgame.info.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import com.example.waynewei.foodgame.R;
import com.example.waynewei.foodgame.info.model.Question;
import com.example.waynewei.foodgame.info.model.Food;
import com.example.waynewei.foodgame.info.viewholder.FoodListViewHolder;
import com.example.waynewei.foodgame.widget.QuestionDialog;
import java.util.ArrayList;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.GrayscaleTransformation;

/**
 * Created by waynewei on 2015/12/13.
 */
public class FoodListAdapter extends RecyclerView.Adapter<FoodListViewHolder>{
	private final RealmList<Question> questions;
	private final Realm realm;
	private Context context;
	private ArrayList<Food> foods= new ArrayList<>();

	public FoodListAdapter(Context context, ArrayList<Food> foods){
		this.context = context;
		this.foods = foods;
		realm = Realm.getDefaultInstance();
		questions = new RealmList<>();
	}

	@Override
	public FoodListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_list, parent, false);
		return new FoodListViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final FoodListViewHolder holder, int position) {
		final Food food = foods.get(position);
		position+=1;
		final int num = foods.size();
		final String question_number = context.getString(R.string.question).toUpperCase() + " " + position;
		final RealmResults<Question> questionResult = realm.where(Question.class).equalTo("answer", food.getName()).findAll();
		if(questionResult.isEmpty()){
			holder.title.setText(question_number);
			Glide.with(context).load(food.getPhoto())
					.bitmapTransform(new BlurTransformation(context, 25), new GrayscaleTransformation(context))
					.into(holder.photo);
		}
		else {
			Glide.with(context).load(food.getPhoto()).into(holder.photo);
			holder.title.setText(food.getName());
		}

		int size = realm.where(Question.class).findAll().size();
		Log.d("test", size+"");

		holder.item.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				QuestionDialog dialog = new QuestionDialog();
				dialog.setInstance(questions, question_number, num , food, holder);
				dialog.show(((Activity) context).getFragmentManager(), "QuestionDialog");

			}
		});

	}

	@Override
	public int getItemCount() {
		return (null != foods ? foods.size() : 0);
	}

	public void removeItem(int position) {
		foods.remove(position);
		notifyItemRemoved(position);
	}

	public void addItem(int position, Food food) {
		foods.add(position, food);
		notifyItemInserted(position);
	}

	public void updateItems(ArrayList<Food> foods) {
		this.foods.clear();
		notifyItemRangeRemoved(0, this.foods.size());
		this.foods = new ArrayList<>();
		this.foods.addAll(foods);
		notifyItemRangeInserted(0, this.foods.size());
	}

}
