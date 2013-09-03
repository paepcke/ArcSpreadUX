package edu.stanford.infolab.arcspreadux.hadoop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.foursquare.fongo.Fongo;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;

import edu.stanford.infolab.arcspreadux.photoSpread.PhotoSpread;
import edu.stanford.infolab.arcspreadux.photoSpread.PhotoSpreadException.DatabaseProblem;
import edu.stanford.infolab.arcspreadux.photoSpreadObjects.PhotoSpreadMongoDB;

@SuppressWarnings("unused")
public class TestMangoDB {

	static final String DB_NAME = "UnitTest";
	static final String COLL_NAME = "testColl";
	
	static PhotoSpreadMongoDB mongoDB = null;
	static DB db = null;
	static Fongo fongoServer = null;
	static DBCollection coll = null;
	
	/****************************************************
	 * Pre-Phase
	 *****************************************************/
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		PhotoSpread.initDefaultProperties();
		// Test using same PhotoSpreadMongoDB instance throughout:
		mongoDB = new PhotoSpreadMongoDB();
		fongoServer = new Fongo("mongo server 1");
		
		//***************************
		// Simple test with real MongoDB on virtual machine:
/*		MongoClient client = new MongoClient("maple.stanford.edu");
		DB db = client.getDB("MyTest");
		BasicDBObject obj = new BasicDBObject("testKey", 20);
		db.getCollection("testColl").update(obj);
		QueryBuilder qbuilder = QueryBuilder.start();
		qbuilder.put("testKey");
		qbuilder.is(20);
		DBObject qObj = qbuilder.get();
		DBCursor cursor = db.getCollection("testColl").find(qObj);
		while (cursor.hasNext())
			System.out.println(cursor.next());
*/				
		//***************************
		
		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		fongoServer.dropDatabase("UnitTest");
	}

	@Before
	public void setUp() throws Exception {
		db = fongoServer.getDB(TestMangoDB.DB_NAME);
		mongoDB.setDB(db);
		mongoDB.setDBName(TestMangoDB.DB_NAME);
		coll = db.getCollection(TestMangoDB.COLL_NAME); 
	}

	@After
	public void tearDown() throws Exception {
		db.dropDatabase();
	}

	/****************************************************
	 * Tests
	 *****************************************************/
	
	@Test
	public void test() {
		
		DBCursor cursor = null;
		//System.out.println(mongoDB.getDatabaseNames());
		List<String> dbNames = mongoDB.getDatabaseNames();
		//assertTrue(dbNames.contains("admin") && dbNames.contains("local"));
		assertTrue(dbNames.contains("local"));
		
		DBCollection coll = db.getCollection("testColl");
		coll.insert(new BasicDBObject("author", "thimblethorpe"));
		
		BasicDBObject query = new BasicDBObject();
		query.put("author", "thimblethorpe");
		cursor = coll.find(query);
		assertTrue(cursor.hasNext());
		DBObject res = cursor.next();
		assertEquals(res.get("author"), "thimblethorpe");
		//System.out.println(res);
		
		BasicDBObject glen = new BasicDBObject();
		glen.put("firstName", "john");
		glen.put("firstName", "glen");
		glen.put("profession", "astronaut");
		coll.insert(new BasicDBObject("author", glen));
		
		query = new BasicDBObject();
		query.put("author", "thimblethorpe");
		cursor = coll.find(query);

		while (cursor.hasNext()) {
			// Returns: { "author" : "thimblethorpe" , "_id" : { "$oid" : "52226484e4b012688aeadfb1"}}
			DBObject resObj = cursor.next();
			assertEquals("thimblethorpe", resObj.get("author"));
		}
	}
	
	@Test
	public void testJSONToDBObjSimple() throws JSONException {
		JSONObject jObj = new JSONObject("{\"person\" : \"gaulle\"}");
		DBObject dbObj = mongoDB.jsonToDBObject(jObj);
		assertEquals("gaulle", dbObj.get("person"));
	}
		
	@Test
	public void testJSONToDBObjNested() throws JSONException {
		JSONObject jObj = new JSONObject();
		JSONObject pJObj = new JSONObject();
		pJObj.put("name", "gaulle");
		pJObj.put("dob", "10.11.2004");
		jObj.put("person", pJObj);
		DBObject dbObj = mongoDB.jsonToDBObject(jObj);
		BasicDBObject subObj = (BasicDBObject) dbObj.get("person"); 
		assertEquals(subObj.get("name"), "gaulle");
		assertEquals(subObj.get("dob"), "10.11.2004");
	}
	
	@Test
	public void testJSONToDBObjArray() throws JSONException {
		JSONObject jObj = new JSONObject();
		jObj.accumulate("testArr", "green");
		jObj.accumulate("testArr", "blue");
		jObj.accumulate("testArr", "yellow");
		DBObject dbObj = mongoDB.jsonToDBObject(jObj);
		assertEquals(3, ((BasicDBList)dbObj.get("testArr")).size());
		assertEquals("green", ((BasicDBList)dbObj.get("testArr")).get(0));
		assertEquals("blue", ((BasicDBList)dbObj.get("testArr")).get(1));
		assertEquals("yellow", ((BasicDBList)dbObj.get("testArr")).get(2));
	}
	
	@Test
	public void testNativeUpdate() throws DatabaseProblem {
		mongoDB.useCollection("testColl");
		mongoDB.update("{testKey : 10}");
		DBObject dObj = db.getCollection("testColl").findOne();
		assertEquals("10", dObj.get("testKey"));
	}
	
	@Test
	public void testQuery() throws DatabaseProblem {
		DBCursor cursor = null;
		DBObject qObj = null;
		DBObject resObj = null;
		
		mongoDB.useCollection("unitTest");
		mongoDB.update("{testKey : 10}");
		
		//System.out.println(String.format("Db has %d entries", mongoDB.getCount()));
		//System.out.println(String.format("findOne() returns: %s", db.getCollection("unitTest").findOne()));
		
		//cursor = db.getCollection("unitTest").find();
		//printResults(cursor);
		
		qObj = new BasicDBObject("testKey", "10");
		cursor = db.getCollection("unitTest").find(qObj);
		if (!cursor.hasNext()) 
			fail("Could not retrieve {'testKey' : '10'}");
		resObj = cursor.next();
		assertEquals("10", resObj.get("testKey"));
	}
	
	@Test
	public void testJSONQuery() throws DatabaseProblem, JSONException {
		DBCursor cursor = null;
		DBObject resObj = null;
		
		mongoDB.useCollection("unitTest");
		mongoDB.update("{testKey : 10}");
		
		cursor = mongoDB.query("{testKey : 10}");
		if (!cursor.hasNext()) 
			fail("Could not retrieve {'testKey' : '10'}");
		resObj = cursor.next();
		assertEquals("10", resObj.get("testKey"));
	}
	
	@Test
	
	public void testHealthyCSVWithColHeadersImport() throws DatabaseProblem, IOException {
		mongoDB.useCollection("csvTest");
		File csvFile = new File("src/test/resources/csvWithColHeaders.csv");
		mongoDB.importCSV(csvFile, ",", PhotoSpreadMongoDB.CSVColHeaderInfo.HAS_COL_HEADERS);
		
		QueryBuilder qBuilder = QueryBuilder.start();
		qBuilder.put("myCol");
		qBuilder.is("4");
		DBObject qObj = qBuilder.get();
		DBCursor cursor = mongoDB.query(qObj);
		assertEquals(1, cursor.count());
		DBObject resObj = cursor.next();
		assertEquals("4", resObj.get("myCol"));
		
		qBuilder = QueryBuilder.start();
		qBuilder.put("herCol");
		qBuilder.is("herColFld");
		qObj = qBuilder.get();
		cursor = mongoDB.query(qObj);
		resObj = cursor.next();
		assertEquals("herColFld", resObj.get("herCol"));
	}
	
	/**
	 * The test CSV file looks like this:
	 * 
	 * myCol,yourCol
	 * 1,2,3
	 * 4,5
	 * "myColFld","yourColFld","herColFld"
	 * 
	 * @throws DatabaseProblem
	 * @throws IOException
	 */
	@Test
	public void testTooFewHeadersWithLax() throws DatabaseProblem, IOException {
		mongoDB.useCollection("unitTest");
		File csvFile = new File("src/test/resources/csvTooFewHeaders.csv");
		
		mongoDB.setCSVImportStrictness(PhotoSpreadMongoDB.CSVStrictness.LAX);
		mongoDB.importCSV(csvFile, ",", PhotoSpreadMongoDB.CSVColHeaderInfo.HAS_COL_HEADERS);
		
		// Since strictness is LAX, the first row
		// should have been imported, even though it has
		// more vals than cols. Test 1st row, 1st col:
		QueryBuilder qBuilder = QueryBuilder.start();
		qBuilder.put("myCol");
		qBuilder.is("1");
		DBObject qObj = qBuilder.get();
		
		DBCursor cursor = mongoDB.query(qObj);
		if (!cursor.hasNext()) {
			fail("Legal row '1,2,3' was not imported (1st col does not match)");
		}
		DBObject resObj = cursor.next();
		assertEquals("1", resObj.get("myCol"));

		// Test 1st row, last (excessive) column:
		qBuilder = QueryBuilder.start();
		qBuilder.put("col2");
		qBuilder.is("3");
		qObj = qBuilder.get();
		
		cursor = mongoDB.query(qObj);
		if (!cursor.hasNext()) {
			fail("Legal CSV row: doc '1,2,3' was not imported (3rd col does not match");
		}
		resObj = cursor.next();
		assertEquals("3", resObj.get("col2"));
		
		
		// Check the one legal row:
		
		qBuilder = QueryBuilder.start();
		qBuilder.put("yourCol");
		qBuilder.is("5");
		qObj = qBuilder.get();
		cursor = mongoDB.query(qObj);
		if (!cursor.hasNext()) {
			fail("The one legal CSV row: doc {'myCol' : 4, 'yourCol' : 5} did not get imported.");
		}
		resObj = cursor.next();
		assertEquals("5", resObj.get("yourCol"));
	}

	@Test
	public void testTooFewHeadersWithAllowTooFew() throws DatabaseProblem, IOException {
		QueryBuilder qBuilder = null;
		DBObject qObj = null;
		DBObject resObj = null;
		DBCursor cursor = null;
		
		mongoDB.useCollection("unitTest");
		File csvFile = new File("src/test/resources/csvTooFewHeaders.csv");
		mongoDB.setCSVImportStrictness(PhotoSpreadMongoDB.CSVStrictness.MISSING_COLS_OK);
		
		boolean gotError = false;
		try{
			mongoDB.importCSV(csvFile, ",", PhotoSpreadMongoDB.CSVColHeaderInfo.HAS_COL_HEADERS);
		} catch (DatabaseProblem e) {
			gotError = true;
		}
		assertTrue(gotError);

		// Since strictness disallows too many cols, the second (and third) rows
		// should only have their first two cols imported. Test 2nd row, 3rd col:
		// 
		qBuilder = QueryBuilder.start();
		qBuilder.put("myCol");
		qBuilder.is("1");
		qObj = qBuilder.get();
		
		cursor = mongoDB.query(qObj);
		if (!cursor.hasNext()) {
			fail("First col of 2nd row not present.");
		}
		resObj = cursor.next();
		assertEquals("1", resObj.get("myCol"));
		
		// Check the one legal row:

		qBuilder = QueryBuilder.start();
		qBuilder.put("yourCol");
		qBuilder.is("5");
		qObj = qBuilder.get();
		cursor = mongoDB.query(qObj);
		if (!cursor.hasNext()) {
			fail("The one legal CSV row: doc {'myCol' : 4, 'yourCol' : 5} did not get imported.");
		}
		resObj = cursor.next();
		assertEquals("5", resObj.get("yourCol"));

	}

	
	
	
	// ----------------------  Utilities --------------------------
	
	private void printResults(DBCursor cursor) {
		while (cursor.hasNext())
			System.out.println(cursor.next());
	}
}
