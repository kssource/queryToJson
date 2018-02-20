# queryToJson

Java class to create JSON from database query.
You create and execute a SQL query without need to write POJO.

Example JDBC:


	String selectSQL = "select sale_id, sale_date as \"saleDate\", product_name as productName "
			+ "from Sale, Product where Product.product_id=Sale.productId;";

	PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
	ResultSet rs = preparedStatement.executeQuery();

	String resultJson = Query2Json.resultSetToJson(rs);
	
	System.out.println("result: "+resultJson);



produce output

	result: [
	  {
	    "SALE_ID": 1,
	    "PRODUCTNAME": "Screen",
	    "saleDate": "Feb 20, 2018"
	  },
	  {
	    "SALE_ID": 2,
	    "PRODUCTNAME": "Keyboard",
	    "saleDate": "Feb 20, 2018"
	  },
	  {
	    "SALE_ID": 3,
	    "PRODUCTNAME": "Keyboard",
	    "saleDate": "Feb 20, 2018"
	  }
	]



To rename output variables use keyword AS in query string: "product_name as productName".

To prevent translation to uppercase close labels in quotation marks: "sale_date as \"saleDate\", ...   "


You can use it with Hibernate:


	SQLQuery query = session.createSQLQuery(selectSQL);
	query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
	List<Map<String,Object>> rows = query.list();

	String resultJson = Query2Json.listToJson(rows);
	
	System.out.println("result: "+resultJson);





Query2Json.java uses libs: javassist and gson (see pom.xml)
