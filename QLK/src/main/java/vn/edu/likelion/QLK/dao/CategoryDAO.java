package vn.edu.likelion.QLK.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import vn.edu.likelion.QLK.connection.DatabaseConnection;
import vn.edu.likelion.QLK.model.Category;
import vn.edu.likelion.QLK.model.Warehouse;

public class CategoryDAO {
	
	  private Connection connection;

	    public CategoryDAO(Connection connection) {
	        this.connection = connection;
	    }
	 public CategoryDAO() {
		// TODO Auto-generated constructor stub
	}
	 
	 WarehouseDAO warehouseDAO = new WarehouseDAO(connection);
	
	public void addCategory(Category category) {
        String sql = "INSERT INTO Category (name, warehouse_id) VALUES (?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, category.getName());
            pstmt.setInt(2, category.getWarehouse().getId());
            pstmt.executeUpdate();
            System.out.println("Category added successfully");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Read
	public Category getCategoryById(int id, Connection connection) throws SQLException {
	    Category category = null;
	    String sql = "SELECT * FROM Category WHERE id = ?";

	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setInt(1, id);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                category = new Category();
	                category.setId(rs.getInt("id"));
	                category.setName(rs.getString("name"));
	                // If you need to set the warehouse, ensure you have a valid warehouseDAO and connection
	                // category.setWarehouse(warehouseDAO.getWarehouseById(rs.getInt("warehouse_id"), connection));
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        throw e; // Re-throw the exception to handle it in the calling method
	    }
	    return category;
	}
	
	 public void addCategoryInWarehouse(Category category, int warehouseId) throws SQLException {
	        String sql = "INSERT INTO Category (name, warehouse_id) VALUES (?, ?)";

	        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	            pstmt.setString(1, category.getName());
	            pstmt.setInt(2, warehouseId);
	            pstmt.executeUpdate();
	            System.out.println("Category added successfully to warehouse ID: " + warehouseId);
	        } catch (SQLException e) {
	            e.printStackTrace();
	            throw e;
	        }
	    }

    // Read All
	 public List<Category> getAllCategories() {
		    List<Category> categories = new ArrayList<>();
		    String sql = "SELECT * FROM Category";
		    
		    // Obtain the connection outside of try-with-resources to handle it explicitly
		    Connection connection = null;
		    
		    try {
		        connection = DatabaseConnection.getConnection();
		        // Ensure the connection is open before proceeding
		        if (connection == null || connection.isClosed()) {
		            throw new SQLException("Failed to establish a database connection.");
		        }

		        try (PreparedStatement pstmt = connection.prepareStatement(sql);
		             ResultSet rs = pstmt.executeQuery()) {
		             
		            WarehouseDAO warehouseDAO = new WarehouseDAO(connection);

		            while (rs.next()) {
		                Category category = new Category();
		                category.setId(rs.getInt("id"));
		                category.setName(rs.getString("name"));
		                category.setWarehouse(warehouseDAO.getWarehouseById(rs.getInt("warehouse_id")));
		                categories.add(category);
		            }
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		    } finally {
		        // Ensure the connection is closed after the operation is completed
		        if (connection != null) {
		            try {
		                connection.close();
		            } catch (SQLException e) {
		                e.printStackTrace();
		            }
		        }
		    }
		    return categories;
		}







    // Update
    public void updateCategory(Category category) {
        String sql = "UPDATE Category SET name = ?, warehouse_id = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, category.getName());
            pstmt.setInt(2, category.getWarehouse().getId());
            pstmt.setInt(3, category.getId());
            pstmt.executeUpdate();
            System.out.println("Category updated successfully");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete
    public void deleteCategory(int id) {
        String sql = "DELETE FROM Category WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Category deleted successfully");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void addWarehouseToCategory(int categoryId, int warehouseId) throws SQLException {
        String sql = "UPDATE Category SET warehouse_id = ? WHERE id = ?";

        Connection connection = null;
        try {
            // Obtain the connection
            connection = DatabaseConnection.getConnection();

            // Ensure the connection is open before proceeding
            if (connection == null || connection.isClosed()) {
                throw new SQLException("Failed to establish a database connection.");
            }

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, warehouseId);
                pstmt.setInt(2, categoryId);
                pstmt.executeUpdate();
                System.out.println("Warehouse ID: " + warehouseId + " added to Category ID: " + categoryId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            // Ensure the connection is closed after the operation is completed
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public List<Category> getCategoriesByWarehouseId(int warehouseId) {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT c.id AS category_id, c.name AS category_name, " +
                     "w.id AS warehouse_id, w.name AS warehouse_name, w.address AS warehouse_address " +
                     "FROM Category c " +
                     "INNER JOIN Warehouse w ON c.warehouse_id = w.id " +
                     "WHERE w.id = ?";

        try {
            // Ensure the connection is open before preparing the statement
            if (connection == null || connection.isClosed()) {
                connection = DatabaseConnection.getConnection(); // Reopen the connection if closed
            }

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, warehouseId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Category category = new Category();
                        category.setId(rs.getInt("category_id"));
                        category.setName(rs.getString("category_name"));

                        Warehouse warehouse = new Warehouse();
                        warehouse.setId(rs.getInt("warehouse_id"));
                        warehouse.setName(rs.getString("warehouse_name"));
                        warehouse.setAddress(rs.getString("warehouse_address"));

                        category.setWarehouse(warehouse);
                        categories.add(category);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }
}
