package de.test;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DbUtil {

	private static Connection connection;

	public static Connection getConnection() {
		return connection;
	}

	public static void createTestDb() throws SQLException, ClassNotFoundException {

		connection = DriverManager.getConnection("jdbc:hsqldb:mem:mymemdb", "SA", "");

		createDb();
	}

	private static void createDb() throws SQLException {

		Statement stmt = connection.createStatement();

		stmt.executeUpdate(
				"CREATE TABLE Product " + "(product_id INT NOT NULL, " + "product_name VARCHAR(50) NOT NULL, "
						+ "product_description VARCHAR(50), " + "PRIMARY KEY (product_id)); ");

		stmt.executeUpdate("CREATE TABLE User " + "(user_id INT NOT NULL, " + "user_name VARCHAR(50) NOT NULL, "
				+ "user_age int NOT NULL, " + "PRIMARY KEY (user_id)); ");

		stmt.executeUpdate("CREATE TABLE Sale " + "(sale_id INT NOT NULL, " + "sale_date DATE NOT NULL, "
				+ "userId INT NOT NULL, " 
				+ "FOREIGN KEY (userId) REFERENCES User (user_id) ON DELETE CASCADE, "
				+ "productId INT NOT NULL, "
				+ "FOREIGN KEY (productId) REFERENCES Product (product_id) ON DELETE CASCADE, "
				+ "PRIMARY KEY (sale_id)); ");

//		System.out.println("Tables Created");

		String insertTableSQL = "INSERT INTO User" + "(user_id, user_name, user_age) VALUES" + "(?,?,?)";
		PreparedStatement preparedStatement = connection.prepareStatement(insertTableSQL);
		preparedStatement.setInt(1, 1);
		preparedStatement.setString(2, "John");
		preparedStatement.setInt(3, 30);
		preparedStatement.executeUpdate();

		preparedStatement = connection.prepareStatement(insertTableSQL);
		preparedStatement.setInt(1, 2);
		preparedStatement.setString(2, "Mike");
		preparedStatement.setInt(3, 40);
		preparedStatement.executeUpdate();

		String insertTableSQL2 = "INSERT INTO Product" + "(product_id, product_name, product_description)"
				+ " VALUES(?,?,?)";
		preparedStatement = connection.prepareStatement(insertTableSQL2);
		preparedStatement.setInt(1, 1);
		preparedStatement.setString(2, "Screen");
		preparedStatement.setString(3, "1080p HD Screen");
		preparedStatement.executeUpdate();

		preparedStatement = connection.prepareStatement(insertTableSQL2);
		preparedStatement.setInt(1, 2);
		preparedStatement.setString(2, "Keyboard");
		preparedStatement.setString(3, null);
		preparedStatement.executeUpdate();

		java.util.Date today = new java.util.Date();
		Date sqlDate = new Date(today.getTime());

		String insertTableSQL3 = "INSERT INTO Sale" + "(sale_id, sale_date, userId, productId)" 
					+ " VALUES(?,?,?,?)";
		preparedStatement = connection.prepareStatement(insertTableSQL3);
		preparedStatement.setInt(1, 1);
		preparedStatement.setDate(2, sqlDate);
		preparedStatement.setInt(3, 1);
		preparedStatement.setInt(4, 1);
		preparedStatement.executeUpdate();

		preparedStatement = connection.prepareStatement(insertTableSQL3);
		preparedStatement.setInt(1, 2);
		preparedStatement.setDate(2, sqlDate);
		preparedStatement.setInt(3, 1);
		preparedStatement.setInt(4, 2);
		preparedStatement.executeUpdate();

		preparedStatement = connection.prepareStatement(insertTableSQL3);
		preparedStatement.setInt(1, 3);
		preparedStatement.setDate(2, sqlDate);
		preparedStatement.setInt(3, 2);
		preparedStatement.setInt(4, 2);
		preparedStatement.executeUpdate();

	}

	
	
	
}
