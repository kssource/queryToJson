package de.ks.queryToJson;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.Loader;

// singleton
public class Query2Json {

	public static String resultSetToJson(ResultSet rs) throws Exception {
		List<Map<String, Object>> listOfMaps = resultSetToList(rs);
		String resultJson = Query2Json.listToJson(listOfMaps);
		return resultJson;
	}
	
	public static String listToJson(List<Map<String, Object>> listOfMaps) throws Exception {
		List<Object> listOfObjects = Query2Json.getInstance().listMapToObj(listOfMaps);
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String resultJson = gson.toJson(listOfObjects);
		return resultJson;
	}
	
	
	
	/**
	 * Convert the ResultSet to a List of Maps, where each Map represents a row with columnNames and columValues
	 */
	private static List<Map<String, Object>> resultSetToList(ResultSet rs) throws SQLException {
	    ResultSetMetaData md = rs.getMetaData();
	    int columns = md.getColumnCount();
	    List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
	    while (rs.next()){
	        Map<String, Object> row = new HashMap<String, Object>(columns);
	        for(int i = 1; i <= columns; ++i){
	            row.put(md.getColumnLabel(i), rs.getObject(i));
	        }
	        rows.add(row);
	    }
	    return rows;
	}

	/////////////////////////////////////////////////////////////////////////
	// private
	
	private static Query2Json instance;
	
	private ClassPool pool;
	private Loader cLoader;
	
	// dynamic created classes
	private Map<String, CtClass> dynClassesMap = new HashMap<>();

	private Query2Json() {

	}
	
	
	private static Query2Json getInstance() {
		if(instance==null) {
			instance = new Query2Json();
			instance.initInstance();
		}
		return instance;
	}
	
	private void initInstance() {
		pool = ClassPool.getDefault();
	    cLoader = new Loader(pool);
	}


	@SuppressWarnings("rawtypes")
	private List<Object> listMapToObj(List<Map<String, Object>> rowsAsMap) throws Exception {
		ArrayList<Object> listOfObj = new ArrayList<>();
		if(rowsAsMap.size()>0) {
			Map<String, Object> exampleRow = rowsAsMap.get(0);
			String className = createClassName(exampleRow);
			
			getRowClass(exampleRow, className);// check/create  
		    Class dynClass = cLoader.loadClass(className);
			
			for ( Map<String, Object> row : rowsAsMap) {
			    Object rowObj = dynClass.newInstance();
				
			    // iterate map
			    Set<String> keys = row.keySet();
			    for (String key : keys) {
					Object val = row.get(key);
					Field field = rowObj.getClass().getField(key);
					field.set(rowObj, val);
				}
				
			    listOfObj.add(rowObj);
			}
		}
		
		return listOfObj;
	}


	// create class name from fields
	private String createClassName(Map<String, Object> exampleRow) {
    	// reuse class witch same fields type/name (and same fields order)
		StringBuilder builder = new StringBuilder();

	    Set<String> keys = exampleRow.keySet();
	    for (String key : keys) {
	    	// determine value type
			Object val = exampleRow.get(key);
	    	String typeStr = val.getClass().getName();
			
	    	builder.append(typeStr).append(key);
		}

    	int fieldsHash = builder.toString().hashCode();
    	fieldsHash = Math.abs(fieldsHash);//avoid minus in className

    	String className = new StringBuilder("DynClass").append(fieldsHash).toString(); 
    	return className;
	}


	private CtClass getRowClass(Map<String, Object> exampleRow, String className) 
			throws Exception {
		
		CtClass cc = dynClassesMap.get(className);
		
		if(cc != null) {
			return cc;
		}
		// else create
		
		cc = pool.makeClass(className);

		// generate fields
	    Set<String> keys = exampleRow.keySet();
	    for (String key : keys) {
	    	// determine value type
			Object val = exampleRow.get(key);
	    	String typeStr = val.getClass().getName();
			
	    	StringBuilder builder = new StringBuilder("public ");
	    	builder.append(typeStr).append(" ").append(key).append(";");
	    	String fieldBody = builder.toString();
	    	
		    CtField f = CtField.make(fieldBody, cc);
			cc.addField(f);
		}
		
	    dynClassesMap.put(className, cc);
		return cc;
	}
	
}
