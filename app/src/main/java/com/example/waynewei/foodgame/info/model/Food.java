package com.example.waynewei.foodgame.info.model;

import java.io.Serializable;

/**
 * Created by waynewei on 2016/5/16.
 */
public class Food implements Serializable {

	private int id;
	private String name;
	private String photo;
	private String type;
	private String question;

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setPhoto(String photo){
		this.photo = photo;
	}

	public String getPhoto(){
		return photo;
	}

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return type;
	}

	public void setQuestion(String question){
		this.question = question;
	}

	public String getQuestion(){
		return question;
	}
}
