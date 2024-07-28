package vn.edu.likelion.QLK;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import vn.edu.likelion.QLK.base64Pass.PasswordUtil;
import vn.edu.likelion.QLK.connection.DatabaseConnection;
import vn.edu.likelion.QLK.dao.AttributeDAO;
import vn.edu.likelion.QLK.dao.CategoryDAO;
import vn.edu.likelion.QLK.dao.PersonDAO;
import vn.edu.likelion.QLK.dao.ProductDAO;
import vn.edu.likelion.QLK.dao.RoleDAO;
import vn.edu.likelion.QLK.dao.WarehouseDAO;
import vn.edu.likelion.QLK.model.Attribute;
import vn.edu.likelion.QLK.model.Category;
import vn.edu.likelion.QLK.model.Person;
import vn.edu.likelion.QLK.model.Product;
import vn.edu.likelion.QLK.model.Role;
import vn.edu.likelion.QLK.model.Warehouse;

@SpringBootApplication
public class QlkApplication {
	
	private static final Connection connection;

    static {
        try {
            connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            throw new ExceptionInInitializerError("Failed to establish database connection");
        }
    }
	
	private static final Scanner scanner = new Scanner(System.in);
    private static final PersonDAO personDAO = new PersonDAO(connection);
    private static final WarehouseDAO warehouseDAO = new WarehouseDAO(connection);
    private static final RoleDAO roleDAO = new RoleDAO(connection);
    private static final CategoryDAO categoryDAO = new CategoryDAO(connection);
    private static final ProductDAO productDAO = new ProductDAO(connection);
    private static final AttributeDAO attributeDAO = new AttributeDAO(connection);
    
    
    public static void main(String[] args) throws SQLException {
        while (true) {
            Person loggedInUser = null;

            // Login/Register Loop
            while (loggedInUser == null) {
                System.out.println("-----------LOGIN/REGISTER-----------");
                System.out.println("1. Sign In");
                System.out.println("2. Sign Up");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume the newline

                switch (choice) {
                    case 1:
                        loggedInUser = handleSignIn();
                        break;
                    case 2:
                        handleSignUp();
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
            }

            // Role-based Menu Loop
            boolean exit = false;
            while (!exit) {
                if (loggedInUser.getRole() == 1) { // Check for admin role
                    exit = handleAdminMenu(loggedInUser);
                } else if (loggedInUser.getRole() == 2) { // Check for user role
                    exit = handleUserMenu(loggedInUser);
                }
            }
        }
    }

    private static Person handleSignIn() {
        System.out.print("Enter username: ");
        String loginUsername = scanner.nextLine();
        System.out.print("Enter password: ");
        String loginPassword = scanner.nextLine();

        Person loggedInUser = personDAO.signIn(loginUsername, PasswordUtil.encodeBase64(loginPassword));

        if (loggedInUser != null) {
            System.out.println("Login successful.");
        } else {
            System.out.println("Login failed. Invalid username or password. Please try again.");
        }
        return loggedInUser;
    }

    private static void handleSignUp() throws SQLException {
        Person newPerson = new Person();
        System.out.print("Enter name: ");
        newPerson.setName(scanner.nextLine());
        System.out.print("Enter age: ");
        newPerson.setAge(scanner.nextInt());
        scanner.nextLine(); // Consume the newline
        System.out.print("Enter username: ");
        newPerson.setUsername(scanner.nextLine());
        System.out.print("Enter password: ");
        newPerson.setPassword(PasswordUtil.encodeBase64(scanner.nextLine())); // Encode the password

        newPerson.setRole(2); // Set role to USER

        System.out.print("Enter warehouse ID (0 for none): ");
        int idWarehouse = scanner.nextInt();
        scanner.nextLine(); // Consume the newline

        if (idWarehouse > 0) {
            Warehouse warehouse = warehouseDAO.getWarehouseById(idWarehouse);
            if (warehouse == null) {
                System.out.println("Invalid warehouse ID.");
                return;
            }
            newPerson.setWarehouse(warehouse);
        }

        personDAO.addPerson(newPerson);
        System.out.println("Sign-up successful. You can now sign in.");
    }

    private static boolean handleAdminMenu(Person loggedInUser) throws SQLException {
        boolean exit = false;

        while (!exit) {
            System.out.println("------Admin Menu------");
            System.out.println("1. Add a new user");
            System.out.println("2. Add a new warehouse");
            System.out.println("3. View warehouse");
            System.out.println("4. View product");
            System.out.println("5. View product in warehouse");
            System.out.println("6. Add warehouse to category");
            System.out.println("7. Get all Warehouse and category with warehouse");
            System.out.println("8. Backup warehouse and category with warehouse when delete warehouse");
            System.out.println("9. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline

            switch (choice) {
                case 1:
                    addNewUser();
                    break;
                case 2:
                    addNewWarehouse();
                    break;
                case 3:
                    viewWarehouses();
                    break;
                case 4:
                    viewProducts();
                    break;
                case 5:
                    viewProductsByWarehouse();
                    break;
                case 6:
                    addWarehouseToCategory();
                    break;
                case 7:
                    getAllWarehouseAndCategory();
                    break;
                case 8:
                    transferCategoriesAndDeleteWarehouse();
                    break;
                case 9:
                    exit = true;
                    System.out.println("Exiting to login/register menu.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
        return exit;
    }

    private static boolean handleUserMenu(Person loggedInUser) throws SQLException {
        boolean exit = false;

        while (!exit) {
            System.out.println("------User Menu------");
            System.out.println("1. View warehouse");
            System.out.println("2. View products in warehouse");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline

            switch (choice) {
                case 1:
                    viewUserWarehouse(loggedInUser);
                    break;
                case 2:
                    viewUserWarehouseProducts(loggedInUser);
                    break;
                case 3:
                    exit = true;
                    System.out.println("Exiting to login/register menu.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
        return exit;
    }

   	private static void viewUserWarehouse(Person loggedInUser) {
    Warehouse warehouse = loggedInUser.getWarehouse();
    if (warehouse == null) {
        System.out.println("No warehouse assigned to this user.");
        return;
    }

    System.out.println("Warehouse ID: " + warehouse.getId());
    System.out.println("Name: " + warehouse.getName());
    System.out.println("Address: " + warehouse.getAddress());
}

   	private static void viewUserWarehouseProducts(Person loggedInUser) {
    Warehouse warehouse = loggedInUser.getWarehouse();
    if (warehouse == null) {
        System.out.println("No warehouse assigned to this user.");
        return;
    }

    List<Product> products = productDAO.getProductsByWarehouseId(warehouse.getId());
    if (products.isEmpty()) {
        System.out.println("No products found in the specified warehouse.");
    } else {
        System.out.println("Product ID   Name                 Quantity   Price      Category");
        System.out.println("------------------------------------------------------------");
        for (Product product : products) {
            System.out.printf("%-12d%-20s%-10d%-10.2f%s%n",
                product.getId(), product.getName(), product.getQuantity(), product.getPrice(), product.getCategory().getName());
        }
    }
}

    private static void addNewUser() throws SQLException {
        System.out.println("------Add New User------");
        Person newPerson = new Person();

        System.out.print("Enter name: ");
        newPerson.setName(scanner.nextLine());
        System.out.print("Enter age: ");
        newPerson.setAge(scanner.nextInt());
        scanner.nextLine(); // Consume newline
        System.out.print("Enter username: ");
        newPerson.setUsername(scanner.nextLine());
        System.out.print("Enter password: ");
        newPerson.setPassword(PasswordUtil.encodeBase64(scanner.nextLine())); // Encode password       
        newPerson.setRole(1);

        System.out.print("Enter warehouse ID (0 for none): ");
        int warehouseId = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        if (warehouseId > 0) {
            Warehouse warehouse = warehouseDAO.getWarehouseById(warehouseId);
            if (warehouse != null) {
                newPerson.setWarehouse(warehouse);
            } else {
                System.out.println("Invalid warehouse ID.");
                return;
            }
        }

        personDAO.addPerson(newPerson);
        System.out.println("New user added successfully.");
    }

    private static void deleteUser() throws SQLException {
        System.out.print("Enter the ID of the user to delete: ");
        int userId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        Person person = personDAO.getPersonById(userId);
        if (person == null) {
            System.out.println("User not found.");
            return;
        }

        personDAO.deletePerson(userId);
        System.out.println("User deleted successfully.");
    }

    private static void addNewWarehouse() {
        System.out.println("------Add New Warehouse------");
        Warehouse newWarehouse = new Warehouse();

        System.out.print("Enter warehouse name: ");
        newWarehouse.setName(scanner.nextLine());
        System.out.print("Enter warehouse address: ");
        newWarehouse.setAddress(scanner.nextLine());

        warehouseDAO.addWarehouse(newWarehouse);
        System.out.println("New warehouse added successfully.");
    }

    private static void addNewProduct() throws SQLException {
        System.out.println("------Add New Product------");
        Product newProduct = new Product();

        System.out.print("Enter product name: ");
        newProduct.setName(scanner.nextLine());
        System.out.print("Enter quantity: ");
        newProduct.setQuantity(scanner.nextInt());
        System.out.print("Enter price: ");
        newProduct.setPrice(scanner.nextDouble());
        scanner.nextLine(); // Consume newline

        System.out.print("Enter category ID: ");
        int categoryId = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        Category category = categoryDAO.getCategoryById(categoryId, DatabaseConnection.getConnection());
        if (category != null) {
            newProduct.setCategory(category);
        } else {
            System.out.println("Invalid category ID.");
            return;
        }

        // Handle product attributes
        List<Attribute> attributes = new ArrayList<>();
        System.out.print("Enter the number of attributes: ");
        int attributeCount = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        for (int i = 0; i < attributeCount; i++) {
            Attribute attribute = new Attribute();
            System.out.print("Enter attribute description: ");
            attribute.setDescription(scanner.nextLine());
            attribute.setProduct(newProduct); // Set the product for the attribute
            attributes.add(attribute);
        }
        newProduct.setListAttributes(attributes);
        
        for(Attribute a : attributes) {
        	attributeDAO.createAttribute(a);
        }

        productDAO.addProduct(newProduct);
        System.out.println("New product added successfully.");
    }

    private static void addNewCategory() throws SQLException {
        System.out.println("------Add New Category------");
        Category newCategory = new Category();

        System.out.print("Enter category name: ");
        newCategory.setName(scanner.nextLine());

        warehouseDAO.getAllWarehouses(); // Assuming this fetches and displays all warehouses

        System.out.print("Enter warehouse ID to assign the category (0 for none): ");
        int warehouseId = scanner.nextInt();
        scanner.nextLine(); // Consume the newline

        if (warehouseId > 0) {
            Warehouse warehouse = warehouseDAO.getWarehouseById(warehouseId);
            if (warehouse != null) {
                newCategory.setWarehouse(warehouse);
            } else {
                System.out.println("Invalid warehouse ID.");
                return;
            }
        }

        categoryDAO.addCategory(newCategory); // Assuming categoryDAO is properly initialized
        System.out.println("New category added successfully.");
    }

    private static void addNewAttribute() throws SQLException {
        System.out.println("------Add New Attribute------");
        Attribute newAttribute = new Attribute();

        System.out.print("Enter attribute description: ");
        newAttribute.setDescription(scanner.nextLine());

        // Fetch all products to choose one
        List<Product> products = productDAO.getAllProducts(); // Assuming this method exists
        System.out.println("Available products:");
        for (Product product : products) {
            System.out.println(product.getId() + ": " + product.getName());
        }

        System.out.print("Enter product ID to assign the attribute: ");
        int productId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        Product product = productDAO.getProductById(productId);
        if (product != null) {
            newAttribute.setProduct(product);
        } else {
            System.out.println("Invalid product ID.");
            return;
        }

        attributeDAO.createAttribute(newAttribute); // Assuming attributeDAO is properly initialized
        System.out.println("New attribute added successfully.");
    }

    private static void viewAttributesByProductId() {
    	 System.out.print("Enter product ID to view attributes: ");
    	    int productId = scanner.nextInt();
    	    scanner.nextLine(); // Consume newline

    	    List<Attribute> attributes = attributeDAO.getAttributesByProductId(productId);

    	    if (attributes.isEmpty()) {
    	        System.out.println("No attributes found for the given product ID.");
    	    } else {
    	        System.out.println("Attributes for product ID " + productId + ":");
    	        System.out.printf("%-15s %-30s %-30s%n", "Attribute ID", "Description", "Product Name");
    	        System.out.println("---------------------------------------------------------------------------");

    	        for (Attribute attribute : attributes) {
    	            System.out.printf("%-15d %-30s %-30s%n", attribute.getId(), attribute.getDescription(), attribute.getProduct().getName());
    	        }

    	        System.out.println("---------------------------------------------------------------------------");
    	    }
    }

    private static void viewWarehouses() {
        List<Warehouse> warehouses = warehouseDAO.getAllWarehouses();
        System.out.println("------Warehouse List------");
        for (Warehouse warehouse : warehouses) {
            System.out.println("ID: " + warehouse.getId() + ", Name: " + warehouse.getName() + ", Address: " + warehouse.getAddress());
        }
    }

    private static void viewProducts() {
        List<Product> products = productDAO.getAllProducts();
        System.out.println("------Product List------");
        for (Product product : products) {
            System.out.println("ID: " + product.getId() + ", Name: " + product.getName() + 
            		", Quantity: " + product.getQuantity() + ", Price: " + product.getPrice() 
            		+", Category: " + product.getCategory().getName());
//            if (product.getCategory() != null) {
//                System.out.println("Category: " + product.getCategory().getName());
//            }
        }
    }

    private static void viewProductsByCategoryAndWarehouse() throws SQLException {
        System.out.print("Enter category ID: ");
        int categoryId = scanner.nextInt();
        System.out.print("Enter warehouse ID: ");
        int warehouseId = scanner.nextInt();
        scanner.nextLine(); // Consume the newline

        List<Product> products = productDAO.getProductsByCategoryAndWarehouse(categoryId, warehouseId);

        if (products.isEmpty()) {
            System.out.println("No products found for the specified category and warehouse.");
        } else {
            for (Product product : products) {
                System.out.println("Product ID: " + product.getId());
                System.out.println("Name: " + product.getName());
                System.out.println("Quantity: " + product.getQuantity());
                System.out.println("Price: " + product.getPrice());
                System.out.println("Category: " + product.getCategory().getName());
                System.out.println("Warehouse: " + product.getCategory().getWarehouse().getName());
                System.out.println("Warehouse Address: " + product.getCategory().getWarehouse().getAddress());
                System.out.println("-------------------------");
            }
        }
    }

    private static void viewProductsByWarehouse() {
        System.out.print("Enter warehouse ID: ");
        int warehouseId = scanner.nextInt();
        scanner.nextLine(); // Consume the newline

        List<Product> products = productDAO.getProductsByWarehouseId(warehouseId);

        if (products.isEmpty()) {
            System.out.println("No products found in the specified warehouse.");
        } else {
            // Print table header
            System.out.printf("%-10s %-20s %-10s %-10s %-20s %-20s %-1s\n",
                              "Product ID", "Name", "Quantity", "Price", "Category", "Warehouse", "Warehouse Address");
            System.out.println("--------------------------------------------------------------------------------------------------------");

            // Print each product in a tabular format
            for (Product product : products) {
                System.out.printf("%-10d %-20s %-10d %-10.2f %-20s %-20s %-1s\n",
                                  product.getId(),
                                  product.getName(),
                                  product.getQuantity(),
                                  product.getPrice(),
                                  product.getCategory().getName(),
                                  product.getCategory().getWarehouse().getName(),
                                  product.getCategory().getWarehouse().getAddress());
            }
        }
    }

    private static void addWarehouseToCategory() {
        System.out.print("Enter category ID: ");
        int categoryId = scanner.nextInt();
        scanner.nextLine(); // Consume the newline

        System.out.print("Enter warehouse ID: ");
        int warehouseId = scanner.nextInt();
        scanner.nextLine(); // Consume the newline

        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection(); // Ensure you have this method to get the connection
            CategoryDAO categoryDAO = new CategoryDAO(connection);
            categoryDAO.addWarehouseToCategory(categoryId, warehouseId);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private static void getAllWarehouseAndCategory() throws SQLException {
        List<Warehouse> warehouses = warehouseDAO.getAllWarehouses();

        if (warehouses.isEmpty()) {
            System.out.println("No warehouses found.");
        } else {
            // Print table header
            System.out.printf("%-10s %-20s %-30s %-10s %-20s\n", "Warehouse ID", "Warehouse Name", "Warehouse Address", "Category ID", "Category Name");
            System.out.println("---------------------------------------------------------------------------------------------");

            for (Warehouse warehouse : warehouses) {
                // Print warehouse details
                System.out.printf("%-10d %-20s %-30s\n", warehouse.getId(), warehouse.getName(), warehouse.getAddress());

                List<Category> categories = categoryDAO.getCategoriesByWarehouseId(warehouse.getId());
                if (categories.isEmpty()) {
                    // Print no category found for this warehouse
                    System.out.printf("%-10s %-20s %-30s %-10s %-20s\n", "", "", "", "No categories found", "");
                } else {
                    for (Category category : categories) {
                        // Print category details aligned with warehouse
                        System.out.printf("%-10s %-20s %-30s %-10d %-20s\n", "", "", "", category.getId(), category.getName());
                    }
                }
                System.out.println("---------------------------------------------------------------------------------------------");
            }
        }
    }

    private static void transferCategoriesAndDeleteWarehouse() throws SQLException {
        System.out.print("Enter source warehouse ID: ");
        int sourceWarehouseId = scanner.nextInt();
        scanner.nextLine(); // Consume the newline

        System.out.print("Enter target warehouse ID: ");
        int targetWarehouseId = scanner.nextInt();
        scanner.nextLine(); // Consume the newline

        boolean success = warehouseDAO.transferCategoriesAndDeleteWarehouse(sourceWarehouseId, targetWarehouseId);

        if (success) {
            System.out.println("Categories transferred and warehouse deleted successfully.");
        } else {
            System.out.println("Failed to transfer categories and delete warehouse.");
        }
    }


}
