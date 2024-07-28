package vn.edu.likelion.QLK.model;

import lombok.Data;

@Data
public class Role {
	 private int id;
	 private String name;
	 
	 public Role() {
		// TODO Auto-generated constructor stub
	}

	public Role(String name) {
		super();
		this.name = name;
	}

	public Role(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	 
		 
}
