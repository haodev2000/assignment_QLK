package vn.edu.likelion.QLK.model;

import java.util.List;

import lombok.Data;

@Data
public class Product {
	 private int id;
	 private String name;
	 private int quantity;
	 private double price;
	 private Category category;
	 private List<Attribute> listAttributes;
	 
	public Product(int id, String name, int quantity, double price, Category category) {
		super();
		this.id = id;
		this.name = name;
		this.quantity = quantity;
		this.price = price;
		this.category = category;
	}
	public Product(int id, String name, int quantity, double price) {
		super();
		this.id = id;
		this.name = name;
		this.quantity = quantity;
		this.price = price;
	}
	 
	 public Product() {
		// TODO Auto-generated constructor stub
	}

}
