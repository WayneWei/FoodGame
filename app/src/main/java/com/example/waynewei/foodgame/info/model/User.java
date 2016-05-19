package com.example.waynewei.foodgame.info.model;

import com.example.waynewei.foodgame.info.model.Friend;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by waynewei on 2015/10/25.
 */
public class User extends RealmObject {

	private String name, email, photo, cover, language, id, deviceId;
	private RealmList<Friend> friends;

	public User(){

	}

	public void setId(String id){
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setEmail(String email){
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getPhoto() {
		return photo;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public String getCover() {
		return cover;
	}

	public void setFriends(RealmList<Friend> friends) { this.friends = friends; }

	public RealmList<Friend> getFriends(){
		return friends;
	}

}
