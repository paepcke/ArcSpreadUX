/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.stanford.infolab.arcspreadux.photoSpreadObjects;

import java.io.File;

import edu.stanford.infolab.arcspreadux.inputOutput.XMLProcessor;
import edu.stanford.infolab.arcspreadux.photoSpreadTable.PhotoSpreadCell;
import edu.stanford.infolab.arcspreadux.photoSpreadUtilities.Const;
import edu.stanford.infolab.arcspreadux.photoSpreadUtilities.PhotoSpreadHelpers;
import edu.stanford.infolab.arcspreadux.photoSpreadUtilities.UUID;

/**
 *
 * @author skandel
 */
abstract public class PhotoSpreadFileObject extends PhotoSpreadObject {
    
    String _filePath;
  
    public PhotoSpreadFileObject(PhotoSpreadCell _cell, String _filePath, UUID theUUID) {
        super(_cell, theUUID);
        this._filePath = _filePath;
        this.setMetaData(
        		Const.permanentMetadataAttributeNames[Const.UUID_METADATA_ATTR_NAME], 
        		theUUID.toString());
        this.setMetaData(
        		Const.permanentMetadataAttributeNames[Const.FILENAME_METADATA_ATTR_NAME], 
        		_filePath);
    }
    
    public String getFilePath() {
        return _filePath;
    }
    
    public String toString() {
    	return getFilePath();
    }
    
    public File valueOf() {
	  return new File(_filePath);
	  
  }

  public Double toDouble() {
	  throw new ClassCastException("Cannot convert from a file to a number.");
  }
  
	@Override
	public <T extends Object>  boolean contentEquals (T str) {
		return (_filePath.equals((String) str));
	}

    
    @Override
    public String constructorArgsToXML() {
        return (PhotoSpreadHelpers.getXMLElement(XMLProcessor.OBJECT_CONSTRUCTOR_ARGUMENT_ELEMENT,  getObjectID().toString()) +
        		PhotoSpreadHelpers.getXMLElement(XMLProcessor.OBJECT_CONSTRUCTOR_ARGUMENT_ELEMENT,  _filePath));
    }
    
}
