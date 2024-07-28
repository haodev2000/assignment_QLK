package vn.edu.likelion.QLK.model;

import lombok.Data;

@Data
public class Person {
	 	private int id;
	    private String name;
	    private int age;
	    private String username;
	    private String password;
	    private int role;
	    private Warehouse warehouse;
	    
		public Person(int id, String name, int age, String username, String password, int role, Warehouse warehouse) {
			super();
			this.id = id;
			this.name = name;
			this.age = age;
			this.username = username;
			this.password = password;
			this.role = role;
			this.warehouse = warehouse;
		}
		
		public Person(int id, String name, int age, String username, String password, int role) {
			super();
			this.id = id;
			this.name = name;
			this.age = age;
			this.username = username;
			this.password = password;
			this.role = role;
			
		}

	  		
	    public Person() {
			// TODO Auto-generated constructor stub
		}

		@Override
		public String toString() {
			return "Person [id=" + id + ", name=" + name + ", age=" + age + ", username=" + username + ", password="
					+ password + ", role=" + role + ", warehouse=" + warehouse + "]";
		}
		
		
		
		
}
