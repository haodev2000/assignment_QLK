package vn.edu.likelion.QLK.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import vn.edu.likelion.QLK.connection.DatabaseConnection;
import vn.edu.likelion.QLK.model.Role;

public class RoleDAO {
	Connection connection;
	
	
	 public RoleDAO(Connection connection) {
		this.connection = connection;
	}

	public void addRole(Role role) throws SQLException {
	        String sql = "INSERT INTO Role (name) VALUES (?)";
	        try (Connection conn = DatabaseConnection.getConnection();
	             PreparedStatement pstmt = conn.prepareStatement(sql)) {
	            pstmt.setString(1, role.getName());
	            pstmt.executeUpdate();
	        }
	    }

	 public List<Role> getAllRoles() throws SQLException {
	        String sql = "SELECT * FROM Role";
	        try (Connection conn =  DatabaseConnection.getConnection();
	             Statement stmt = conn.createStatement();
	             ResultSet rs = stmt.executeQuery(sql)) {
	            List<Role> roles = new ArrayList<>();
	            while (rs.next()) {
	                roles.add(new Role(rs.getInt("id"), rs.getString("name")));
	            }
	            return roles;
	        }
	    }
	 
	 public Role getRoleID(int id) throws SQLException {
		 Role role = null;
		    String sql = "SELECT * FROM Role WHERE id = ?";

		    try (Connection connection = DatabaseConnection.getConnection();
		         PreparedStatement pstmt = connection.prepareStatement(sql)) {

		        pstmt.setInt(1, id);
		        try (ResultSet rs = pstmt.executeQuery()) {
		            if (rs.next()) {
		                role = new Role();
		                role.setId(rs.getInt("id"));
		                role.setName(rs.getString("name"));
		                
//		                System.out.println(rs.getInt("id"));
//		                  System.out.println(rs.getString("name"));
		            }
		        }

		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		    return role;
	    }
}
