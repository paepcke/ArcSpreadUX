/**
 * 
 */
package edu.stanford.infolab.arcspreadux.photoSpreadObjects;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.NotImplementedException;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.mortbay.log.Log;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;

import edu.stanford.infolab.arcspreadux.photoSpread.PhotoSpreadException.DatabaseProblem;
import edu.stanford.infolab.arcspreadux.photoSpreadTable.PhotoSpreadCell;
import edu.stanford.infolab.arcspreadux.photoSpreadTable.PhotoSpreadTableModel;
import edu.stanford.infolab.arcspreadux.photoSpreadUtilities.UUID;

/**
 * @author paepcke
 *
 */
public class PhotoSpreadMongoDB extends PhotoSpreadDBObject {

	public enum CSVColHeaderInfo {
		HAS_COL_HEADERS,
		NO_COL_HEADERS;
	}
	
	public enum CSVStrictness {
		LAX,
		MISSING_COLS_OK,
		VALUES_MUST_MATCH_COLS
	}

	private static Random randGen = new Random();
	private static int MONGO_DEFAULT_PORT = 27017;
	
	// We are supposed to use one MongoClient for the entire JVM:
	private static MongoClient mongoClient = null;
	String uid = "";
	String pwd = "";
	DB db = null;
	DBCollection currColl = null;
	boolean auth = false;
	WriteConcern currWriteConcern = WriteConcern.ACKNOWLEDGED; // the default anyway
	
	// Should CSV import insist on having as many fields
	// in each row as there are column names? If yes
	CSVStrictness importStrictness = CSVStrictness.MISSING_COLS_OK;
	
	
	/****************************************************
	 * Constructor(s)
	 *****************************************************/

	
	/*---------------------------
	* Constructor cell,dbName  
	*------------------*/
	
	public PhotoSpreadMongoDB(PhotoSpreadCell _cell, String _theDBName) throws UnknownHostException {
		this(_cell,
			 _theDBName, 
			 "localhost", 
			 MONGO_DEFAULT_PORT, 
			 "", // no uid 
			 "", // no pwd
			 new UUID(_theDBName + randGen.nextInt()));
	}
							  
	/*---------------------------
	* Constructor cell,dbName,host,port,uid,pwd,uuid 
	*------------------*/
	
	
	public PhotoSpreadMongoDB(PhotoSpreadCell _cell, 
							  String _theDBName,
							  String _theHost, 
							  int _thePort,
							  String _userID,
							  String _pwd,
							  UUID theUUID) throws UnknownHostException {
		super(_cell, _theDBName, _theHost, _thePort, theUUID);
		uid = _userID;
		pwd = _pwd;
		if (_theHost.length() == 0)
			_theHost = "localhost";
		if (_thePort < 0)
			_thePort = MONGO_DEFAULT_PORT;
		if ((_theDBName.length() == 0) || (_theDBName == null))
			_theDBName = "test";
		mongoClient = new MongoClient(getHost(), getPort());
		db = mongoClient.getDB( getDBName() );
		if ((uid.length() > 0) && (pwd.length() > 0)) {
			auth = db.authenticate(uid, pwd.toCharArray());
			if (!auth) {
				throw new AccessControlException(String.format("MongoDB '%s' refuses access by user %s.",
								toString(), getUserID()));
			}
		}
	}
	
	/*---------------------------
	* Constructor for unit testing only 
	*------------------*/
	
	/**
	 * Only used for unit testing. The unit test methods set the
	 * various instance vars to point to a 'fake' server.
	 */
	public PhotoSpreadMongoDB() {
		super(new PhotoSpreadCell(new PhotoSpreadTableModel(), 0, 0),
			  "", // no dbname for now. Set later by unit tests
			  "localhost",
			  MONGO_DEFAULT_PORT,
			  new UUID("foobar"));
		try {
			mongoClient = new MongoClient(getHost(), getPort());
		} catch (UnknownHostException e) {
			// Shouldn't happen (remember: this constructor only called
			// from unit tests.
			e.printStackTrace();
		}
	}
	
	/****************************************************
	 * Public Methods
	 * @throws JSONException 
	 * @throws DatabaseProblem 
	 *****************************************************/
	
	/*---------------------------
	* query()
	*------------------*/
	
	public DBCursor query(String jsonStr) throws JSONException, DatabaseProblem {
		JSONObject jObj = new JSONObject(jsonStr);
		DBObject dbObj = jsonToDBObject(jObj);
		return query(dbObj);
	}
	
	
	/*---------------------------
	* query()
	*------------------*/
	
	/**
	 * Use QueryBuilder to build queries for this method.
	 * Example:
	 * 	QueryBuilder qbuilder = QueryBuilder.start();
	 *	qbuilder.put("testKey");
	 *	qbuilder.is(10);  // add an 'equals'
	 *	DBObject qObj = qbuilder.get();  // get the final DBObject
	 *
	 * @param query
	 * @return
	 * @throws DatabaseProblem 
	 */
	public DBCursor query(DBObject query) throws DatabaseProblem {
		if (currColl == null)
			throwUnspecifiedCollection();
		return currColl.find(query);
	}
	
	/*---------------------------
	* query()
	*------------------*/
	
	/**
	 * Query, getting a list of results objects
	 * @param query Query object, built manually or using QueryBuilder
	 * @param numResults limit of number of results. -1: get all
	 * @return List of result objects
	 * @throws DatabaseProblem 
	 */
	public List<Object> query(DBObject query, int numResults) throws DatabaseProblem {
		if (currColl == null)
			throwUnspecifiedCollection();
		List<Object> res = new ArrayList<Object>();
		if (numResults == 1) {
			res.add(currColl.findOne());
		}
		else {
			DBCursor cursor = query(query);
			if (numResults > -1) {
				// Limit the set of items that the cursor will feed:
				cursor.limit(numResults);
			}
			while (cursor.hasNext() && numResults > 0) {
				res.add(cursor.next());
				numResults--;
			}
		}
		return res;
	}
	
	/*---------------------------
	* update()
	*------------------*/
	
	/**
	 * Insert a given JSON string into the currently used collection.
	 * The JSON string is first converted to a JSON object, then
	 * to a Mongo DBObject.
	 * @param jsonStr
	 * @throws DatabaseProblem
	 */
	public void update(String jsonStr) throws DatabaseProblem {
		if (currColl == null)
			throwUnspecifiedCollection();
		update(currColl, jsonStr);
	}
	
	/*---------------------------
	* update()
	*------------------*/
	
	/**
	 * Insert a given JSON string into the given collection.
	 * The JSON string is first converted to a JSON object, then
	 * to a Mongo DBObject.
	 * @param coll
	 * @param jsonStr
	 * @throws DatabaseProblem
	 */
	public void update(DBCollection coll, String jsonStr) throws DatabaseProblem {
		JSONObject jObj;
		try {
			jObj = new JSONObject(jsonStr);
		} catch (JSONException e) {
			throw new DatabaseProblem("Could not parse JSON string: " + e.getMessage());
		}
		update(coll, jObj);
	}
	
	/*---------------------------
	* update() 
	*------------------*/
	
	/**
	 * Insert a given JSON object into the given collection.
	 * @param coll
	 * @param jObj
	 */
	public void update(DBCollection coll, JSONObject jObj) {
		DBObject dbObj = jsonToDBObject(jObj);
		coll.insert(dbObj);
	}

	/*---------------------------
	* getCount() 
	*------------------*/
	
	public long getCount() {
		return currColl.getCount();
	}
	
	/*---------------------------
	* useCollection() 
	*------------------*/
	
	/**
	 * Set the default collection in this MongoDB
	 * @param collName
	 */
	public void useCollection(String collName) {
		if (db == null)
			throw new RuntimeException("Mongo database object was not initialized before call to setCurrColl");
		currColl = db.getCollection(collName);
	}

	/*---------------------------
	* importCSV() 
	*------------------*/
	
	public void importCSV(File csvFile, String fldSep, CSVColHeaderInfo firstLineHasColNames) throws IOException, DatabaseProblem {
		LineIterator lineIt = FileUtils.lineIterator(csvFile);
		if (! lineIt.hasNext()) {
			if (firstLineHasColNames == CSVColHeaderInfo.NO_COL_HEADERS)
				return; // No col headers promised, just return: all done
			else
				throw new IOException("CSV file is empty, but caller claimed first line had column headings: " + csvFile.getAbsolutePath());
		}
		String firstLine = lineIt.next();
		String[] maybeColNames = firstLine.split(fldSep);
		if (firstLineHasColNames == CSVColHeaderInfo.HAS_COL_HEADERS) {
			importCSV(lineIt, fldSep, maybeColNames, csvFile);
			LineIterator.closeQuietly(lineIt);
			return;
		} else {
			// First line does not have col names. Invent some
			// based on the first data line that we read:
			String[] colNames = new String[maybeColNames.length];
			for (int i=0; i<maybeColNames.length; i++) {
				colNames[i] = "col" + i;
			}
			// Close and re-open the iterator so that the first
			// line we read above is read again:
			lineIt.close();
			importCSV(csvFile, fldSep, colNames);
			return;
		}
	}
	
	public void importCSV(File csvFile, String fldSep, String[] colNames) throws IOException, DatabaseProblem {
		LineIterator lineIt = FileUtils.lineIterator(csvFile);
		importCSV(lineIt, fldSep, colNames, csvFile);
		LineIterator.closeQuietly(lineIt);
	}
	
	private void importCSV(Iterator<String> lineIt, 
						   String fldSep, 
						   String[] colNames,
						   File csvFile) throws DatabaseProblem {
		int rowNum = -1; // for better error msgs
		String colName = null;
		while (lineIt.hasNext()) {
			rowNum++;
			String row = lineIt.next();
			int colIndex = 0;
			for (String fld : row.split(fldSep)) {
				if (colIndex >= colNames.length) {
					// Too few column names given. Check strictness:
					if (importStrictness == CSVStrictness.LAX) {
						Log.warn(String.format("More col values than column header names for row %d in CSV file %s; colNames: %s", 
												rowNum, csvFile.getAbsolutePath(), colNames));
						colName = "col" + colIndex;
					} else {
						throw new DatabaseProblem(String.format("CSV file %s has more values in row %d than column headers",
												  csvFile.getAbsolutePath(), rowNum));
					}
				} else {
					colName = colNames[colIndex];
				}
				
				update(String.format("{%s : %s}", colName, fld));
				colIndex++;
			}
			// Did we have a value for every col? (too many values was
			// checked in the loop. This is a check for too few):
			if (colIndex < colNames.length) {
				// Too few:
				if (importStrictness == CSVStrictness.VALUES_MUST_MATCH_COLS)
					throw new DatabaseProblem(String.format("Too few column values for row %d in CSV file %s; colNames: %s", 
											  rowNum, csvFile.getAbsolutePath(), colNames));
			}
		}
	}
	
		
	/*---------------------------
	* setCSVImportStrictness() 
	*------------------*/
	
	/**
	 * Set how strict CSV import is about the imported CSV files.
	 * Three choices: CSVStrictness.LAX allows rows with more 
	 * column values than there are column headers, as well as
	 * with fewer values. When more values are provided than column
	 * headers, a new column
	 * header is created. CSVStrictness.MISSING_COLS_OK allows
	 * fewer column values to be present in rows than there are
	 * column headers, but more values than column headers is not
	 * allowed. CSVStrictness.VALUES_MUST_MATCH_COLS insists that
	 * every row has as many columns as column headers.
	 * @param strictness desired level of CSV row length to length of column headers.
	 */
	
	public void setCSVImportStrictness(CSVStrictness strictness) {
		importStrictness = strictness;
	}
	
	/*---------------------------
	* CSVImportStrictness() 
	*------------------*/
	
	public CSVStrictness CSVImportStrictness() {
		return importStrictness;
	}
	
	
	/*---------------------------
	* getDatabaseNames() 
	*------------------*/
	
	public List<String> getDatabaseNames() {
		return mongoClient.getDatabaseNames();
	}
	
	/*---------------------------
	* getUserID() 
	*------------------*/
	
	
	public String getUserID() {
		return uid;
	}

	/*---------------------------
	* copyObject()
	*------------------*/
	
	
	/* (non-Javadoc)
	 * @see edu.stanford.infolab.arcspreadux.photoSpreadObjects.PhotoSpreadObject#copyObject()
	 */
	@SuppressWarnings("unchecked")
	@Override
	//public PhotoSpreadDBObject copyObject() {
	public <T extends PhotoSpreadObject> T copyObject() {
		T newDb = null;
		try {
			  newDb =  (T) new PhotoSpreadMongoDB(getCell(),
												  getDBName(), 
												  getHost(), 
												  getPort(), 
												  getUserID(),
												  pwd,
												  null);
		} catch (UnknownHostException e) {
			throw new RuntimeException("Error copying Mongo database object: " + e.getMessage());
		}
		((PhotoSpreadMongoDB) newDb).useCollection(currColl.getName());
		((PhotoSpreadMongoDB) newDb).setCSVImportStrictness(importStrictness);
		return newDb;
	}

	/*---------------------------
	* getObjectComponent() 
	*------------------*/
	
	/* (non-Javadoc)
	 * @see edu.stanford.infolab.arcspreadux.photoSpreadObjects.PhotoSpreadObject#getObjectComponent(int, int)
	 */
	@Override
	public Component getObjectComponent(int height, int width)
			throws NotImplementedException {
		throw new NotImplementedException();
	}

	/****************************************************
	 * Protected Methods
	 *****************************************************/
	
	// Somehow TestMangoDB cannot access the following methods when
	// they are set to 'protected,' even when TestMangoDB is made to inherit
	// from PhotoSpreadMongoDB. So we declare them public, though
	// the intent is not to.
	
	// The following protected methods are for use by the junit tests:
	public void setDBName(String dbName) {
		_dbName = dbName;
	}

	public void setDB(DB theDbObj) {
		db = theDbObj;
	}
	
	/****************************************************
	 * Private Methods (some declared public only for unit tests
	 * @return 
	 * @throws DatabaseProblem 
	 *****************************************************/
	
	private void throwUnspecifiedCollection() throws DatabaseProblem {
		throw new DatabaseProblem(
				String.format("No collection was designated as default in database '%s'. Call useCollection() to fix",
						db.getName()));
	}
	
	/**
	 * Convert a JSON object into a MongoDB object
	 * @param jObj
	 * @return a new MongoDB object
	 */
	public DBObject jsonToDBObject(JSONObject jObj) {
		BasicDBObject dbObj = new BasicDBObject();
		dbObj = jsonToDBObjectHelper(dbObj, jObj);
		return dbObj;
	}
	
	/**
	 * Recursive workhorse for converting JSON objects to MongoDB BasicDBObject.
	 * @param dbObj
	 * @param jObj
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private BasicDBObject jsonToDBObjectHelper(BasicDBObject dbObj, JSONObject jObj) {
		
		Iterator<String> jsonIt = jObj.keys();
		String jKey = null;
		while (jsonIt.hasNext()) {
			try {
				// Assume that value is a nested JSON object; if
				// exception, value is a primitive:
				jKey = jsonIt.next();
				JSONObject val = jObj.getJSONObject(jKey);
				dbObj.append(jKey, jsonToDBObjectHelper(new BasicDBObject(), val));
				continue;
			} catch (JSONException e) {
				try {
				// Was not a JSONObject. How about an array?
				JSONArray jArr = jObj.getJSONArray(jKey);
				// If we didn't bomb, we in fact have an array:
				BasicDBList dbList = new BasicDBList();
				for (int i=0; i<jArr.length(); i++) {
					try {
						dbList.add(jArr.get(i));
					} catch (JSONException e1) {
						try {
							dbObj.append(jKey, jObj.get(jKey));
							continue;
						} catch (JSONException e2) {
							// the jObj.get() should succeed, if not, debug:
							e1.printStackTrace();
						}
					}
				}
				dbObj.append(jKey, dbList);
				} catch (JSONException e1) {
					try {
						// Was neither a JSON object nor a JSON array: was just an attr/value pair
						dbObj.put(jKey, jObj.get(jKey).toString());
						
					} catch (JSONException e2) {
						logger.error(String.format("Could not convert JSON obj field to BasicDBObject; JSON key: %s, JSON val: %s",
												   jKey));
												   
					}
				}
			}
		}
		return dbObj;
	}
}

