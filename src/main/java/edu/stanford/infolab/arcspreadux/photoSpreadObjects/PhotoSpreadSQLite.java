package edu.stanford.infolab.arcspreadux.photoSpreadObjects;

import java.awt.Component;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import edu.stanford.infolab.arcspreadux.photoSpread.PhotoSpreadException.CannotLoadImage;
import edu.stanford.infolab.arcspreadux.photoSpread.PhotoSpreadException.DatabaseProblem;
import edu.stanford.infolab.arcspreadux.photoSpreadTable.PhotoSpreadCell;
import edu.stanford.infolab.arcspreadux.photoSpreadUtilities.UUID;

public class PhotoSpreadSQLite extends PhotoSpreadDBObject {
	
	Connection connection = null;

	/****************************************************
	 * Constructor(s)
	 * @throws ClassNotFoundException 
	 *****************************************************/
	
	
	public PhotoSpreadSQLite(PhotoSpreadCell _cell, String _theDBPath, UUID theUUID) throws DatabaseProblem {
		super(_cell, 
			  _theDBPath, 
			  "", // no host
			  -1, // no port
			  theUUID);
		
		// Load the sqlite-JDBC driver using the current class loader
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			throw new DatabaseProblem("Could not load the SQLite JDBC driver");
		} 
		try {
			connection = DriverManager.getConnection(_theDBPath);
		} catch (SQLException e1) {
			throw new DatabaseProblem(String.format("Could not connect to db '%s': %s",
													_theDBPath, e1.getMessage()));
		}
		
	}

	// For unit tests:
	public PhotoSpreadSQLite(PhotoSpreadCell _cell, String _theDBPath) throws DatabaseProblem {
		this(_cell, _theDBPath, new UUID("foobar"));
	}
	
	/****************************************************
	 * Public Methods
	 *****************************************************/
	
	/*---------------------------
	* query() 
	*------------------*/
	
	/**
	 * @param sql
	 * @return
	 * @throws DatabaseProblem
	 */
	public ResultSet query(String sql) throws DatabaseProblem  {
		return query(sql, -1);	// -1: no query timeout 
	}
	
	/*---------------------------
	* query() 
	*------------------*/
	
	/**
	 * @param sql
	 * @param timeoutSecs
	 * @return
	 * @throws DatabaseProblem
	 */
	public ResultSet query(String sql, int timeoutSecs) throws DatabaseProblem  {
		Statement stat;
		try {
			stat = connection.createStatement();
			if (timeoutSecs > 0)
				stat.setQueryTimeout(timeoutSecs);
			stat.executeUpdate(sql);
		
		} catch (SQLException e) {
			throw new DatabaseProblem("Could not query SQLite via JCDB : " + e.getMessage());
		}
		return null;
	}
	
	@Override
	public <T extends PhotoSpreadObject> T copyObject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Component getObjectComponent(int height, int width)
			throws CannotLoadImage {
		// TODO Auto-generated method stub
		return null;
	}

}
