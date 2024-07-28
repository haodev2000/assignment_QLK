package vn.edu.likelion.QLK.model;

import lombok.Data;

@Data
public class Warehouse {
	private int id;
    private String name;
    private String address;
    
    
    public Warehouse() {
		// TODO Auto-generated constructor stub
	}
    
	public Warehouse(int id, String name, String address) {
		super();
		this.id = id;
		this.name = name;
		this.address = address;
	}

	public Warehouse(String name, String address) {
		super();
		this.name = name;
		this.address = address;
	}

	@Override
	public String toString() {
		return "Warehouse [id=" + id + ", name=" + name + ", address=" + address + "]";
	}
    
	
    
    
}
