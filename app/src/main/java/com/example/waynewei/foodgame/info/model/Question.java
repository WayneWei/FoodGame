package com.example.waynewei.foodgame.info.model;

import io.realm.RealmObject;

/**
 * Created by waynewei on 2015/10/25.
 */
public class Question extends RealmObject {

	private String answer;

	public Question() {

	}

	public void setAnswer(String answer){
		this.answer = answer;
	}

	public String getAnswer() {
		return answer;
	}

}
