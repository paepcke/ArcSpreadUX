/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.stanford.infolab.arcspreadux.photoSpreadObjects;

import org.apache.log4j.Logger;

import edu.stanford.infolab.arcspreadux.inputOutput.XMLProcessor;
import edu.stanford.infolab.arcspreadux.photoSpreadTable.PhotoSpreadCell;
import edu.stanford.infolab.arcspreadux.photoSpreadUtilities.Const;
import edu.stanford.infolab.arcspreadux.photoSpreadUtilities.PhotoSpreadHelpers;
import edu.stanford.infolab.arcspreadux.photoSpreadUtilities.UUID;

/**
 *
 * @author paepcke
 */
abstract public class PhotoSpreadDBObject extends PhotoSpreadObject {
    
	protected static Logger logger = Logger.getLogger("dbs"); 
	
    String _dbName = "";
    String _host = "";
    int _port = -1;
  
    public PhotoSpreadDBObject(PhotoSpreadCell _cell, 
    						   String _theDBName,
    						   String _theHost,
    						   int _thePort,
    						   UUID theUUID) {
        super(_cell, theUUID);
        this._dbName = _theDBName;
        this._host = _theHost;        
        this._port = _thePort;        
        this.setMetaData(
        		Const.permanentMetadataAttributeNames[Const.UUID_METADATA_ATTR_NAME], 
        		theUUID.toString());
        this.setMetaData(
        		Const.permanentMetadataAttributeNames[Const.FILENAME_METADATA_ATTR_NAME], 
        		_theDBName);
    }
    
    public String getDBName() {
        return _dbName;
    }

    public String getHost() {
        return _host;
    }
    
    public int getPort() {
        return _port;
    }
    
    
    public String toString() {
    	String res = getDBName();
    	if (_host.length() > 0)
    		res += ":" + getHost();
    	if (_port > -1)
    		res += ":" + getPort();
    	
    	return res;
    }
    
    public String valueOf() {
	  return toString();
    }

  public Double toDouble() {
	  throw new ClassCastException("Cannot convert from a database to a number.");
  }
  
	@Override
	public <T extends Object>  boolean contentEquals (T str) {
		return (toString().equals((String) str));
	}

    
    @Override
    public String constructorArgsToXML() {
        return (PhotoSpreadHelpers.getXMLElement(XMLProcessor.OBJECT_CONSTRUCTOR_ARGUMENT_ELEMENT,  getObjectID().toString()) +
        		PhotoSpreadHelpers.getXMLElement(XMLProcessor.OBJECT_CONSTRUCTOR_ARGUMENT_ELEMENT,  _dbName) +
        		PhotoSpreadHelpers.getXMLElement(XMLProcessor.OBJECT_CONSTRUCTOR_ARGUMENT_ELEMENT,  _host) +
        		PhotoSpreadHelpers.getXMLElement(XMLProcessor.OBJECT_CONSTRUCTOR_ARGUMENT_ELEMENT,  _port));
    }

}
