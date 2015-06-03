package com.rhapsodyman.learndroid;

public class ListItem {
	private String title;
	private String image;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.title;
	}

}
