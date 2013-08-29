package edu.stanford.infolab.arcspreadux.hadoop;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
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
	public void test() {
		//System.out.println(mongoDB.getDatabaseNames());
		List<String> dbNames = mongoDB.getDatabaseNames();
		assertTrue(dbNames.contains("admin") && dbNames.contains("local"));
		
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
		DBCursor cursor = coll.find(query);
		
		
	}

}
