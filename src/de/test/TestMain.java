package de.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import de.ks.queryToJson.Query2Json;

public class TestMain {

	public static void main(String[] args) {
		try {
			test();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void test() throws Exception {
		DbUtil.createTestDb();
		Connection connection = DbUtil.getConnection();
		

		String selectSQL = "select sale_id, sale_date as \"saleDate\", product_name as productName "
				+ "from Sale, Product where Product.product_id=Sale.productId;";

		PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);

		// execute select SQL stetement
		ResultSet rs = preparedStatement.executeQuery();
		
		String resultJson = Query2Json.resultSetToJson(rs);
		
		System.out.println("result: "+resultJson);

	}

}
