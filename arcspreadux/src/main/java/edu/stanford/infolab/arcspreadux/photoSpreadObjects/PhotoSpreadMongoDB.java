/**
 * 
 */
package edu.stanford.infolab.arcspreadux.photoSpreadObjects;

import java.awt.Component;
import java.net.UnknownHostException;
import java.security.AccessControlException;
import java.util.List;
import java.util.Random;

import com.mongodb.DB;
import com.mongodb.MongoClient;

import edu.stanford.infolab.arcspreadux.photoSpread.PhotoSpreadException.CannotLoadImage;
import edu.stanford.infolab.arcspreadux.photoSpreadTable.PhotoSpreadCell;
import edu.stanford.infolab.arcspreadux.photoSpreadTable.PhotoSpreadTableModel;
import edu.stanford.infolab.arcspreadux.photoSpreadUtilities.UUID;

/**
 * @author paepcke
 *
 */
public class PhotoSpreadMongoDB extends PhotoSpreadDBObject {

	private static Random randGen = new Random();
	private static int MONGO_DEFAULT_PORT = 27017;
	
	// We are supposed to use one MongoClient for the entire JVM:
	private static MongoClient mongoClient = null;
	String uid = "";
	String pwd = "";
	DB db = null;
	boolean auth = false;
	
	/****************************************************
	 * Constructor(s)
	 *****************************************************/
	
	public PhotoSpreadMongoDB(PhotoSpreadCell _cell, String _theDBName) throws UnknownHostException {
		this(_cell,
			 _theDBName, 
			 "localhost", 
			 MONGO_DEFAULT_PORT, 
			 "", // no uid 
			 "", // no pwd
			 new UUID(_theDBName + randGen.nextInt()));
	}
							  
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
	 *****************************************************/
	
	public List<String> getDatabaseNames() {
		return mongoClient.getDatabaseNames();
	}
	
	public String getUserID() {
		return uid;
	}

	/* (non-Javadoc)
	 * @see edu.stanford.infolab.arcspreadux.photoSpreadObjects.PhotoSpreadObject#copyObject()
	 */
	@SuppressWarnings("unchecked")
	@Override
	//public PhotoSpreadDBObject copyObject() {
	public <T extends PhotoSpreadObject> T copyObject() {
		try {
			return (T) new PhotoSpreadMongoDB(getCell(),
										  getDBName(), 
										  getHost(), 
										  getPort(), 
										  getUserID(),
										  pwd,
										  null);
		} catch (UnknownHostException e) {
			throw new RuntimeException("Error copying Mongo database object: " + e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see edu.stanford.infolab.arcspreadux.photoSpreadObjects.PhotoSpreadObject#getObjectComponent(int, int)
	 */
	@Override
	public Component getObjectComponent(int height, int width)
			throws CannotLoadImage {
		// TODO Auto-generated method stub
		return null;
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
}
