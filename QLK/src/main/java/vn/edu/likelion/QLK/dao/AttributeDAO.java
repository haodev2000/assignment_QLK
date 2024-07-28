package vn.edu.likelion.QLK.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import vn.edu.likelion.QLK.connection.DatabaseConnection;
import vn.edu.likelion.QLK.model.Attribute;
import vn.edu.likelion.QLK.model.Product;

public class AttributeDAO {
	
	Connection connection;
	
	
	 public AttributeDAO(Connection connection) {
		this.connection = connection;
	}

	private ProductDAO productDAO = new ProductDAO(connection);
	private CategoryDAO categoryDAO = new CategoryDAO(connection);

	    // Create
	 public void createAttribute(Attribute attribute) {
	        String sql = "INSERT INTO Attribute (description, product_id) VALUES (?, ?)";

	        try (Connection conn = DatabaseConnection.getConnection();
	             PreparedStatement pstmt = conn.prepareStatement(sql)) {
	            pstmt.setString(1, attribute.getDescription());
	            pstmt.setInt(2, attribute.getProduct().getId());
	            pstmt.executeUpdate();
	            System.out.println("Attribute added successfully");
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	 public List<Attribute> getAttributesByProductId(int productId) {
	        List<Attribute> attributes = new ArrayList<>();
	        String sql = "SELECT a.id, a.description, p.id as product_id, p.name as product_name, p.quantity, p.price, p.category_id " +
	                     "FROM Attribute a " +
	                     "INNER JOIN Product p ON a.product_id = p.id " +
	                     "WHERE p.id = ?";

	        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	            
	            pstmt.setInt(1, productId);
	            try (ResultSet rs = pstmt.executeQuery()) {
	                while (rs.next()) {
	                    Attribute attribute = new Attribute();
	                    attribute.setId(rs.getInt("id"));
	                    attribute.setDescription(rs.getString("description"));
	                    
	                    Product product = new Product();
	                    product.setId(rs.getInt("product_id"));
	                    product.setName(rs.getString("product_name"));
	                    product.setQuantity(rs.getInt("quantity"));
	                    product.setPrice(rs.getDouble("price"));
	                    product.setCategory(categoryDAO.getCategoryById(rs.getInt("category_id"), connection)); // Ensure categoryDAO is properly initialized

	                    attribute.setProduct(product);
	                    attributes.add(attribute);
	                }
	            }

	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return attributes;
	    }

}
