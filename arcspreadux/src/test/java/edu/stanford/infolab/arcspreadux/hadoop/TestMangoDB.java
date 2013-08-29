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
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import edu.stanford.infolab.arcspreadux.photoSpread.PhotoSpread;
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
	public void testBasics() {
		//System.out.println(mongoDB.getDatabaseNames());
		List<String> dbNames = mongoDB.getDatabaseNames();
		//assertTrue(dbNames.contains("admin") && dbNames.contains("local"));
		assertTrue(dbNames.contains("local"));
		
		DBCollection coll = db.getCollection("testColl");
		coll.insert(new BasicDBObject("author", "thimblethorpe"));
		
		BasicDBObject query = new BasicDBObject();
		query.put("author", "thimblethorpe");
		DBCursor cursor = coll.find(query);
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
		// **** add assertion
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
	public void testJSONToDBObjArray() throws JSONException {
		JSONObject jObj = new JSONObject();
		jObj.accumulate("testArr", "green");
		jObj.accumulate("testArr", "blue");
		jObj.accumulate("testArr", "yellow");
		DBObject dbObj = mongoDB.jsonToDBObject(jObj);
		System.out.println(dbObj);
	}

}
