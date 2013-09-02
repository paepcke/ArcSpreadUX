package edu.stanford.infolab.arcspreadux.hadoop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
import com.mongodb.MongoClient;
import com.mongodb.QueryBuilder;

import edu.stanford.infolab.arcspreadux.photoSpread.PhotoSpread;
import edu.stanford.infolab.arcspreadux.photoSpread.PhotoSpreadException.DatabaseProblem;
import edu.stanford.infolab.arcspreadux.photoSpreadObjects.PhotoSpreadMongoDB;

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
		db.getCollection("testColl").insert(obj);
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
	@Ignore
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
	@Ignore
	public void testJSONToDBObjSimple() throws JSONException {
		JSONObject jObj = new JSONObject("{\"person\" : \"gaulle\"}");
		DBObject dbObj = mongoDB.jsonToDBObject(jObj);
		assertEquals("gaulle", dbObj.get("person"));
	}
		
	@Test
	@Ignore
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
	@Ignore
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
	@Ignore
	public void testInsert() throws DatabaseProblem {
		mongoDB.useCollection("testColl");
		mongoDB.insert("{testKey : 10}");
		DBObject dObj = db.getCollection("testColl").findOne();
		assertEquals("10", dObj.get("testKey"));
	}
	
	
	/*
	 * Querying DOES NOT WORK with the in-memory fongoDb we
	 * use here. So this method, while correct, won't find 
	 * any results.
	 */
	@Test
	@Ignore
	public void testQuery() throws DatabaseProblem {
		mongoDB.useCollection("testColl");
		mongoDB.insert("{testKey : 10}");
		
		System.out.println(String.format("Db has %d entries", mongoDB.getCount()));
		System.out.println(String.format("findOne() returns: %s", db.getCollection("testColl").findOne()));
		
		DBCursor cur0 = db.getCollection("testColl").find();
		printResults(cur0);
		
		BasicDBObject obj0 = new BasicDBObject("testKey", 10);
		cur0 = db.getCollection("testColl").find(obj0);
		printResults(cur0);
		
		
		QueryBuilder qbuilder = QueryBuilder.start();
		qbuilder.put("testKey");
		qbuilder.is(10);
		DBObject qObj = qbuilder.get();
		//*******DBCursor cursor = mongoDB.query(qObj);
		DBCursor cursor = db.getCollection("testColl").find(qObj);
		System.out.println(String.format("Cursor count: %d", cursor.count()));
		printResults(cursor);
	}
	
	// ----------------------  Utilities --------------------------
	
	private void printResults(DBCursor cursor) {
		while (cursor.hasNext())
			System.out.println(cursor.next());
	}
}
