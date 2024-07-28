package vn.edu.likelion.QLK.model;

import lombok.Data;

@Data
public class Attribute {

	 private int id;
	 private String description;
	 private Product product;
	public Attribute(String description) {
		super();
		this.description = description;
		
	}
	 
	 public Attribute() {
		// TODO Auto-generated constructor stub
	}
	 
}
