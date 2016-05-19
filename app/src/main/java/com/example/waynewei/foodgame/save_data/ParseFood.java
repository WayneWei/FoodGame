package com.example.waynewei.foodgame.save_data;

import com.example.waynewei.foodgame.info.model.Food;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * Created by waynewei on 2015/12/13.
 */
public class ParseFood {

	private ArrayList<Food> foods;

	public ParseFood(String result){
		try {
			foods = new ArrayList<>();
			foods.clear();
			JSONArray jsonArray = new JSONArray(result);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject object = jsonArray.getJSONObject(i);
				Food food = new Food();

				if(object.has("id"))
					food.setId(object.getInt("id"));
				if(object.has("name"))
					food.setName(object.getString("name"));
				if(object.has("photo"))
					food.setPhoto(object.getString("photo"));
				if(object.has("type"))
					food.setType(object.getString("type"));
				if(object.has("question"))
					food.setQuestion(object.getString("question"));

				foods.add(food);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<Food> getFoodList(){
		return foods;
	}
}
