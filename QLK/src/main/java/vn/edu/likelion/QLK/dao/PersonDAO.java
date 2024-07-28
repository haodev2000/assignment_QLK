package vn.edu.likelion.QLK.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import vn.edu.likelion.QLK.base64Pass.PasswordUtil;
import vn.edu.likelion.QLK.connection.DatabaseConnection;
import vn.edu.likelion.QLK.model.Person;

public class PersonDAO {

	Connection connection;
	public PersonDAO() {
		// TODO Auto-generated constructor stub
	}
	
	
	 public PersonDAO(Connection connection) {
		this.connection = connection;
	}

	 WarehouseDAO warehouseDAO = new WarehouseDAO(connection);
		RoleDAO roleDAO = new RoleDAO(connection);

	public void signUp(Person person) {
		 String sql = "INSERT INTO Person (name, age, username, password, role_id, warehouse_id) VALUES (?, ?, ?, ?, ?, ?)";

	        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

	            pstmt.setString(1, person.getName());
	            pstmt.setInt(2, person.getAge());
	            pstmt.setString(3, person.getUsername());
	            pstmt.setString(4,  PasswordUtil.encodeBase64(person.getPassword()));
	            pstmt.setInt(5, person.getRole());
	            pstmt.setInt(6, person.getWarehouse().getId());
	            pstmt.executeUpdate();
	            System.out.println("Person added successfully");

	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	
	//Login
	 public Person signIn(String username, String password) {
		  String sql = "SELECT * FROM Person WHERE username = ?";
		    Person person = null;

		    try (Connection connection = DatabaseConnection.getConnection();
		         PreparedStatement pstmt = connection.prepareStatement(sql)) {

		        pstmt.setString(1, username);
		        try (ResultSet rs = pstmt.executeQuery()) {
		            if (rs.next()) {
		                // Retrieve the encoded password from the database
		                String encodedPassword = rs.getString("password");

		                // Decode the stored password
		                String storedPassword = PasswordUtil.decodeBase64(encodedPassword);

		                // Compare the provided password with the stored password
		                if (storedPassword.equals(password)) {
		                    person = new Person();
		                    person.setId(rs.getInt("id"));
		                    person.setName(rs.getString("name"));
		                    person.setAge(rs.getInt("age"));
		                    person.setUsername(rs.getString("username"));
		                    person.setPassword(storedPassword); 
		                    person.setRole(rs.getInt("role_id"));
		                    int warehouseId = rs.getInt("warehouse_id");
		                    if (warehouseId > 0) {
		                        person.setWarehouse(warehouseDAO.getWarehouseById(warehouseId));
		                    }
		                }
		            }
		        }
		    } catch (SQLException e) {
		        e.printStackTrace(); // Log the error for debugging purposes
		    }

		    return person;
	    }

	 // Create
    public void addPerson(Person person) {
    	 String sql = "INSERT INTO Person (name, age, username, password, role_id, warehouse_id) VALUES (?, ?, ?, ?, ?, ?)";

    	    try (Connection connection = DatabaseConnection.getConnection();
    	         PreparedStatement pstmt = connection.prepareStatement(sql)) {

    	        pstmt.setString(1, person.getName());
    	        pstmt.setInt(2, person.getAge());
    	        pstmt.setString(3, person.getUsername());
    	        pstmt.setString(4, PasswordUtil.encodeBase64(person.getPassword()));
    	        pstmt.setInt(5, person.getRole());
    	        pstmt.setInt(6, person.getWarehouse().getId());

    	        pstmt.executeUpdate();
    	        System.out.println("Person added successfully");

    	    } catch (SQLException e) {
    	        e.printStackTrace(); // Log the error for debugging purposes
    	    }
    }
    
    // Get by ID
    public Person getPersonById(int id) throws SQLException {
    	Person person = null;
        String sql = "SELECT * FROM Person WHERE id = ?";

        WarehouseDAO warehouseDAO = new WarehouseDAO(connection);

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

               pstmt.setInt(1, id);
               try (ResultSet rs = pstmt.executeQuery()) {
                   if (rs.next()) {
                	   
                       person = new Person();
                      
                       person.setId(rs.getInt("id"));
                       person.setName(rs.getString("name"));
                       person.setAge(rs.getInt("age"));
                       person.setUsername(rs.getString("username"));
                       person.setPassword(rs.getString("password"));
                       person.setRole(rs.getInt("role_id"));
                       person.setWarehouse(warehouseDAO.getWarehouseById(rs.getInt("warehouse_id")));
                      
                   }
               }

           } catch (SQLException e) {
               e.printStackTrace();
           }
        return person;
     }
 
    // Read All
    public List<Person> getAllPersons() {
    	  List<Person> persons = new ArrayList<>();
    	    String sql = "SELECT * FROM Person";

    	    // Ensure roleDAO and warehouseDAO are properly initialized
    	  
    	    WarehouseDAO warehouseDAO = new WarehouseDAO(connection);

    	    try (Statement stmt = connection.createStatement();
    	         ResultSet rs = stmt.executeQuery(sql)) {

    	        while (rs.next()) {
    	            Person person = new Person();
    	            person.setId(rs.getInt("id"));
    	            person.setName(rs.getString("name"));
    	            person.setAge(rs.getInt("age"));
    	            person.setUsername(rs.getString("username"));
    	            person.setPassword(rs.getString("password"));
    	            person.setRole(rs.getInt("role_id"));
    	            person.setWarehouse(warehouseDAO.getWarehouseById(rs.getInt("warehouse_id")));
    	            persons.add(person);
    	            
    	       
    	           
    	        }

    	    } catch (SQLException e) {
    	     
    	    }
    	    return persons;
    }

    // Update
    public void updatePerson(Person person) {
        String sql = "UPDATE Person SET name = ?, age = ?, username = ?, password = ?, role_id = ?, warehouse_id = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, person.getName());
            pstmt.setInt(2, person.getAge());
            pstmt.setString(3, person.getUsername());
            pstmt.setString(4, person.getPassword());
            pstmt.setInt(5, person.getId());
            pstmt.setInt(6, person.getWarehouse().getId());
            pstmt.setInt(7, person.getId());
            pstmt.executeUpdate();
            System.out.println("Person updated successfully");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete
    public void deletePerson(int id) {
    	 String getWarehouseIdSql = "SELECT warehouse_id FROM Person WHERE id = ?";
         String deletePersonSql = "DELETE FROM Person WHERE id = ?";
         String deleteWarehouseSql = "DELETE FROM Warehouse WHERE id = ?";

         try (PreparedStatement getWarehouseIdStmt = connection.prepareStatement(getWarehouseIdSql);
              PreparedStatement deletePersonStmt = connection.prepareStatement(deletePersonSql);
              PreparedStatement deleteWarehouseStmt = connection.prepareStatement(deleteWarehouseSql)) {

             // Retrieve the associated Warehouse ID
             getWarehouseIdStmt.setInt(1, id);
             ResultSet rs = getWarehouseIdStmt.executeQuery();
             Integer warehouseId = null;

             if (rs.next()) {
                 warehouseId = rs.getInt("warehouse_id");
             }

             // Delete the Person record
             deletePersonStmt.setInt(1, id);
             deletePersonStmt.executeUpdate();

             // If a Warehouse ID was found, delete the associated Warehouse
             if (warehouseId != null) {
                 deleteWarehouseStmt.setInt(1, warehouseId);
                 deleteWarehouseStmt.executeUpdate();
             }

         } catch (SQLException e) {
             e.printStackTrace();
         }
     }
    
    
    private void deleteWarehouseByPersonId(int personId, Connection conn) throws SQLException {
        String sql = "DELETE FROM Warehouse WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, personId);
            pstmt.executeUpdate();
        }
    }
    
    public Person login1(String username, String password) throws SQLException {
        String sql = "SELECT p.person_id, p.username, p.password, r.role_id, r.role_name " +
                     "FROM Persons p " +	                     
                     "JOIN Roles r ON p.role_id = r.role_id " +
                     "WHERE p.username = ? AND p.password = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);	          
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {	        
                return new Person(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("age"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getInt("role_id")                   
                );
            }
            return null;
        }
 }
}
