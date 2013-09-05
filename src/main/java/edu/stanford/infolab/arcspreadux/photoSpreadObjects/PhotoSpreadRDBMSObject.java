/**
 * 
 */
package edu.stanford.infolab.arcspreadux.photoSpreadObjects;

import java.sql.ResultSet;
import java.sql.SQLException;

import edu.stanford.infolab.arcspreadux.photoSpreadTable.PhotoSpreadCell;
import edu.stanford.infolab.arcspreadux.photoSpreadUtilities.UUID;

/**
 * @author paepcke
 *
 */
public abstract class PhotoSpreadRDBMSObject extends PhotoSpreadDBObject {

	/**
	 * @param _cell
	 * @param _theDBName
	 * @param _theHost
	 * @param _thePort
	 * @param theUUID
	 */
	public PhotoSpreadRDBMSObject(PhotoSpreadCell _cell, String _theDBName,
			String _theHost, int _thePort, UUID theUUID) {
		super(_cell, _theDBName, _theHost, _thePort, theUUID);
		// TODO Auto-generated constructor stub
	}

	public abstract long getNumRows(String tableName) throws SQLException;
	public abstract long getNumRows(ResultSet rs) throws SQLException;	
	public abstract int  getNumColumns(String tableName)  throws SQLException;
	public abstract int  getNumColumns(ResultSet rs) throws SQLException;
}
