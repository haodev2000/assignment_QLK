package vn.edu.likelion.QLK.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import vn.edu.likelion.QLK.connection.DatabaseConnection;
import vn.edu.likelion.QLK.model.Warehouse;

public class WarehouseDAO {
	
		Connection connection;
	
	
		public WarehouseDAO(Connection connection) {
		
		this.connection = connection;
	}

	 	public void addWarehouse(Warehouse warehouse) {
	        String sql = "INSERT INTO Warehouse (name, address) VALUES (?, ?)";

	        try (Connection connection = DatabaseConnection.getConnection();
	            PreparedStatement pstmt = connection.prepareStatement(sql)) {

	            pstmt.setString(1, warehouse.getName());
	            pstmt.setString(2, warehouse.getAddress());
	            pstmt.executeUpdate();
	            System.out.println("Warehouse added successfully");

	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    // Read
	    public Warehouse getWarehouseById(int id) throws SQLException {
	    	  Warehouse warehouse = new Warehouse();
	    	    String sql = "SELECT * FROM Warehouse WHERE id = ?";

	    	    try (Connection connection = DatabaseConnection.getConnection();
	    	         PreparedStatement pstmt = connection.prepareStatement(sql)) {

	    	        pstmt.setInt(1, id);
	    	        try (ResultSet rs = pstmt.executeQuery()) {
	    	            if (rs.next()) {
	    	                warehouse.setId(rs.getInt("id"));
	    	                warehouse.setName(rs.getString("name")); // Ensure this column exists
	    	                warehouse.setAddress(rs.getString("address")); // Assuming this is the correct column name for address
	    	            }
	    	        }

	    	    }
	    	    return warehouse;
	    }

	    // Read All
	    public List<Warehouse> getAllWarehouses() {
	        String sql = "SELECT * FROM Warehouse ORDER BY id";
	        List<Warehouse> warehouses = new ArrayList<>();

	        try (Connection connection = DatabaseConnection.getConnection();
	             PreparedStatement pstmt = connection.prepareStatement(sql);
	             ResultSet rs = pstmt.executeQuery()) {

	            while (rs.next()) {
	                Warehouse warehouse = new Warehouse(rs.getInt("id"), rs.getString("name"), rs.getString("address"));
	                warehouses.add(warehouse);
	            }

	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return warehouses;
	    }

	    // Update
	    public void updateWarehouse(Warehouse warehouse) {
	        String sql = "UPDATE Warehouse SET name = ?, address = ? WHERE id = ?";

	        try (Connection connection = DatabaseConnection.getConnection();
	             PreparedStatement pstmt = connection.prepareStatement(sql)) {

	            pstmt.setString(1, warehouse.getName());
	            pstmt.setString(2, warehouse.getAddress());
	            pstmt.setInt(3, warehouse.getId());
	            pstmt.executeUpdate();
	            System.out.println("Warehouse updated successfully");

	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    // Delete
	    public void deleteWarehouse(int id) {
	        String sql = "DELETE FROM Warehouse WHERE id = ?";

	        try (Connection connection = DatabaseConnection.getConnection();
	             PreparedStatement pstmt = connection.prepareStatement(sql)) {

	            pstmt.setInt(1, id);
	            pstmt.executeUpdate();
	            System.out.println("Warehouse deleted successfully");

	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    public boolean transferCategoriesAndDeleteWarehouse(int sourceWarehouseId, int targetWarehouseId) throws SQLException {
	        String updateCategorySql = "UPDATE Category SET warehouse_id = ? WHERE warehouse_id = ?";
	        String deleteWarehouseSql = "DELETE FROM Warehouse WHERE id = ?";
	        String updatePersonSql = "UPDATE Person SET warehouse_id = ? WHERE warehouse_id = ?";
	        
	        Connection connection = null;
	        PreparedStatement updateCategoryStmt = null;
	        PreparedStatement deleteWarehouseStmt = null;
	        PreparedStatement updatePersonStmt = null;

	        try {
	            connection = DatabaseConnection.getConnection();
	            connection.setAutoCommit(false);

	            // Update categories to point to the new warehouse
	            updateCategoryStmt = connection.prepareStatement(updateCategorySql);
	            updateCategoryStmt.setInt(1, targetWarehouseId);
	            updateCategoryStmt.setInt(2, sourceWarehouseId);
	            updateCategoryStmt.executeUpdate();

	            // Update persons to point to the new warehouse
	            updatePersonStmt = connection.prepareStatement(updatePersonSql);
	            updatePersonStmt.setInt(1, targetWarehouseId);
	            updatePersonStmt.setInt(2, sourceWarehouseId);
	            updatePersonStmt.executeUpdate();

	            // Delete the source warehouse
	            deleteWarehouseStmt = connection.prepareStatement(deleteWarehouseSql);
	            deleteWarehouseStmt.setInt(1, sourceWarehouseId);
	            deleteWarehouseStmt.executeUpdate();

	            connection.commit();
	            return true;
	        } catch (SQLException e) {
	            if (connection != null) {
	                try {
	                    connection.rollback();
	                } catch (SQLException rollbackEx) {
	                    rollbackEx.printStackTrace();
	                }
	            }
	            e.printStackTrace();
	            return false;
	        } finally {
	            if (updateCategoryStmt != null) {
	                try {
	                    updateCategoryStmt.close();
	                } catch (SQLException e) {
	                    e.printStackTrace();
	                }
	            }
	            if (updatePersonStmt != null) {
	                try {
	                    updatePersonStmt.close();
	                } catch (SQLException e) {
	                    e.printStackTrace();
	                }
	            }
	            if (deleteWarehouseStmt != null) {
	                try {
	                    deleteWarehouseStmt.close();
	                } catch (SQLException e) {
	                    e.printStackTrace();
	                }
	            }
	            if (connection != null) {
	                try {
	                    connection.close();
	                } catch (SQLException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	    }

}
