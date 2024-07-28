package vn.edu.likelion.QLK.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import vn.edu.likelion.QLK.connection.DatabaseConnection;
import vn.edu.likelion.QLK.model.Category;
import vn.edu.likelion.QLK.model.Product;
import vn.edu.likelion.QLK.model.Warehouse;

public class ProductDAO {
	
	  private Connection connection;
	  
	  
	public ProductDAO(Connection connection) {
		this.connection = connection;
	}

	CategoryDAO categoryDAO = new CategoryDAO();
	WarehouseDAO warehouseDAO = new WarehouseDAO(connection);

	    // Create
	    public void addProduct(Product product) {
	        String sql = "INSERT INTO Product (name, quantity, price, category_id) VALUES (?, ?, ?, ?)";

	        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

	            pstmt.setString(1, product.getName());
	            pstmt.setInt(2, product.getQuantity());
	            pstmt.setDouble(3, product.getPrice());
	            pstmt.setInt(4, product.getCategory().getId());
	            pstmt.executeUpdate();
	            System.out.println("Product added successfully");

	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    // Read
	    public Product getProductById(int id) throws SQLException {
	        Product product = null;
	        String sql = "SELECT * FROM Product WHERE id = ?";

	        try (Connection connection = DatabaseConnection.getConnection();
	             PreparedStatement pstmt = connection.prepareStatement(sql)) {

	            pstmt.setInt(1, id);
	            try (ResultSet rs = pstmt.executeQuery()) {
	                if (rs.next()) {
	                    product = new Product();
	                    product.setId(rs.getInt("id"));
	                    product.setName(rs.getString("name"));
	                    product.setQuantity(rs.getInt("quantity"));
	                    product.setPrice(rs.getDouble("price"));
	                    product.setCategory(categoryDAO.getCategoryById(rs.getInt("category_id"), connection));
	                }
	            }

	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return product;
	    }

	    // Read All
	    public List<Product> getAllProducts() {
	        List<Product> products = new ArrayList<>();
	        String sql = "SELECT * FROM Product";

	        try (Connection connection = DatabaseConnection.getConnection();
	             PreparedStatement pstmt = connection.prepareStatement(sql);
	             ResultSet rs = pstmt.executeQuery()) {

	            while (rs.next()) {
	                Product product = new Product();
	                product.setId(rs.getInt("id"));
	                product.setName(rs.getString("name"));
	                product.setQuantity(rs.getInt("quantity"));
	                product.setPrice(rs.getDouble("price"));
	                product.setCategory(categoryDAO.getCategoryById(rs.getInt("category_id"), connection));
	                products.add(product);
	            }

	        } catch (SQLException e) {
	          e.printStackTrace();
	        }
	        return products;
	    }
	    
	    // Update
	    public void updateProduct(Product product) {
	        String sql = "UPDATE Product SET name = ?, quantity = ?, price = ?, category_id = ? WHERE id = ?";

	        try (Connection connection = DatabaseConnection.getConnection();
	             PreparedStatement pstmt = connection.prepareStatement(sql)) {

	            pstmt.setString(1, product.getName());
	            pstmt.setInt(2, product.getQuantity());
	            pstmt.setDouble(3, product.getPrice());
	            pstmt.setInt(4, product.getCategory().getId());
	            pstmt.setInt(5, product.getId());
	            pstmt.executeUpdate();
	            System.out.println("Product updated successfully");

	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    // Delete
	    public void deleteProduct(int id) {
	        String sql = "DELETE FROM Product WHERE id = ?";

	        try (Connection connection = DatabaseConnection.getConnection();
	             PreparedStatement pstmt = connection.prepareStatement(sql)) {

	            pstmt.setInt(1, id);
	            pstmt.executeUpdate();
	            System.out.println("Product deleted successfully");

	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    public List<Product> getProductsByCategoryAndWarehouse(int categoryId, int warehouseId) {
	        List<Product> products = new ArrayList<>();
	        String sql = "SELECT p.id, p.name, p.quantity, p.price, c.id AS category_id, c.name AS category_name, w.id AS warehouse_id, w.name AS warehouse_name, w.address AS warehouse_address " +
	                     "FROM Product p " +
	                     "JOIN Category c ON p.category_id = c.id " +
	                     "JOIN Warehouse w ON c.warehouse_id = w.id " +
	                     "WHERE c.id = ? AND w.id = ?";

	        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	            pstmt.setInt(1, categoryId);
	            pstmt.setInt(2, warehouseId);
	            try (ResultSet rs = pstmt.executeQuery()) {
	                while (rs.next()) {
	                    Product product = new Product();
	                    product.setId(rs.getInt("id"));
	                    product.setName(rs.getString("name"));
	                    product.setQuantity(rs.getInt("quantity"));
	                    product.setPrice(rs.getDouble("price"));

	                    Category category = new Category();
	                    category.setId(rs.getInt("category_id"));
	                    category.setName(rs.getString("category_name"));

	                    Warehouse warehouse = new Warehouse();
	                    warehouse.setId(rs.getInt("warehouse_id"));
	                    warehouse.setName(rs.getString("warehouse_name"));
	                    warehouse.setAddress(rs.getString("warehouse_address"));

	                    category.setWarehouse(warehouse);
	                    product.setCategory(category);

	                    products.add(product);
	                }
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return products;
	    }

	    public List<Product> getProductsByWarehouseId(int warehouseId) {
	        List<Product> products = new ArrayList<>();
	        String sql = "SELECT p.id AS product_id, p.name AS product_name, p.quantity, p.price, " +
	                     "c.id AS category_id, c.name AS category_name, " +
	                     "w.id AS warehouse_id, w.name AS warehouse_name, w.address AS warehouse_address " +
	                     "FROM Product p " +
	                     "INNER JOIN Category c ON p.category_id = c.id " +
	                     "INNER JOIN Warehouse w ON c.warehouse_id = w.id " +
	                     "WHERE w.id = ?";

	        try (Connection connection = DatabaseConnection.getConnection();
	             PreparedStatement pstmt = connection.prepareStatement(sql)) {
	            
	            pstmt.setInt(1, warehouseId);
	            try (ResultSet rs = pstmt.executeQuery()) {
	                while (rs.next()) {
	                    Product product = new Product();
	                    product.setId(rs.getInt("product_id"));
	                    product.setName(rs.getString("product_name"));
	                    product.setQuantity(rs.getInt("quantity"));
	                    product.setPrice(rs.getDouble("price"));

	                    Category category = new Category();
	                    category.setId(rs.getInt("category_id"));
	                    category.setName(rs.getString("category_name"));

	                    Warehouse warehouse = new Warehouse();
	                    warehouse.setId(rs.getInt("warehouse_id"));
	                    warehouse.setName(rs.getString("warehouse_name"));
	                    warehouse.setAddress(rs.getString("warehouse_address"));

	                    category.setWarehouse(warehouse);
	                    product.setCategory(category);

	                    products.add(product);
	                }
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return products;
	    }
}
