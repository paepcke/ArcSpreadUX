package edu.stanford.infolab.arcspreadux.photoSpreadObjects;

import java.awt.Component;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import edu.stanford.infolab.arcspreadux.photoSpread.PhotoSpreadException.CannotLoadImage;
import edu.stanford.infolab.arcspreadux.photoSpread.PhotoSpreadException.DatabaseProblem;
import edu.stanford.infolab.arcspreadux.photoSpreadTable.PhotoSpreadCell;
import edu.stanford.infolab.arcspreadux.photoSpreadUtilities.UUID;

public class PhotoSpreadSQLite extends PhotoSpreadRDBMSObject {
	
	Connection jdbcConnection = null;
	Statement  jdbcStatement  = null;
	ResultSet  jdbcResultSet  = null;
	

	/****************************************************
	 * Constructor(s)
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
			jdbcConnection = DriverManager.getConnection(String.format("jdbc:sqlite:%s",_theDBPath));
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
		;
		try {
			jdbcStatement = jdbcConnection.createStatement();
			if (timeoutSecs > 0)
				jdbcStatement.setQueryTimeout(timeoutSecs);
			jdbcResultSet = jdbcStatement.executeQuery(sql);
			return jdbcResultSet;
		
		} catch (SQLException e) {
			throw new DatabaseProblem("Could not query SQLite via JCDB : " + e.getMessage());
		}
	}
	
	// TODO: Write the two getNumRows(). Then continue in Perplexity.java
	
	@Override
	public long getNumRows(String tableName) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getNumRows(ResultSet rs) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getNumColumns(String tableName) throws SQLException {
		ResultSet rsColumns = null;
		int numCols = 0;
		try {
			DatabaseMetaData meta = jdbcConnection.getMetaData();
			rsColumns = meta.getColumns(null, // no catalog 
										null, // no schemaPattern
										tableName,
										null); // no columnNamePattern
			while (rsColumns.next()) {
				//System.out.println(rsColumns.getString("TYPE_NAME"));
				//System.out.println(rsColumns.getString("COLUMN_NAME"));
				numCols++;
			}
		} finally {
			if (rsColumns != null) rsColumns.close();
		}
		return numCols;
	}
	
	
	@Override
	public int getNumColumns(ResultSet rs) throws SQLException {
		Statement st = null;
		try {
			st = jdbcConnection.createStatement();
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnsNumber = rsmd.getColumnCount();
			return columnsNumber;
		}
		finally {
			if (st != null) st.close();
		}
	}
	
	public void closeAll() {
		if (jdbcResultSet != null) {
			try {
				jdbcResultSet.close();
			} catch (SQLException e) {
				// do nothing
			}
			jdbcResultSet = null;
		}
		if (jdbcStatement != null) {
			try {
				jdbcStatement.close();
			} catch (SQLException e) {
				// do nothing
			}
			jdbcStatement = null;
		}
		if (jdbcConnection!= null) {
			try {
				jdbcConnection.close();
			} catch (SQLException e) {
				// do nothing
			}
			jdbcConnection = null;
		}
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
