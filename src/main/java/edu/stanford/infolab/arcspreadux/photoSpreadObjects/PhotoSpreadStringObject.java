/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.stanford.infolab.arcspreadux.photoSpreadObjects;

import java.awt.Component;

import edu.stanford.infolab.arcspreadux.inputOutput.XMLProcessor;
import edu.stanford.infolab.arcspreadux.photoSpread.PhotoSpreadException.BadUUIDStringError;
import edu.stanford.infolab.arcspreadux.photoSpreadObjects.photoSpreadComponents.DraggableLabel;
import edu.stanford.infolab.arcspreadux.photoSpreadTable.PhotoSpreadCell;
import edu.stanford.infolab.arcspreadux.photoSpreadUtilities.Const;
import edu.stanford.infolab.arcspreadux.photoSpreadUtilities.PhotoSpreadHelpers;
import edu.stanford.infolab.arcspreadux.photoSpreadUtilities.UUID;

/**
 *
 * @author skandel
 */
public class PhotoSpreadStringObject extends PhotoSpreadObject {

	String _value;
	public static String OBJECT_ELEMENT_NAME = "StringObject";
	public static String VALUE_ELEMENT_NAME = "value";
	
	
	public PhotoSpreadStringObject(PhotoSpreadCell _cell, String _value) {

		super(_cell, new UUID(_value));
		this._value = _value;
	}

	/**
	 * Takes a string that was generated by UUID.toString(), and
	 * returns a reconstructed UUID object.
	 * @param _cell
	 * @param uuidStr
	 * @param _value
	 * @throws BadUUIDStringError
	 */
	public PhotoSpreadStringObject(PhotoSpreadCell _cell, String uuidStr, String _value) 
	throws BadUUIDStringError {

		super(_cell, UUID.createFromUUIDString(uuidStr));
		this._value = _value;
	}
	
	
	@Override
	public Component getObjectComponent(int height, int width) {

		DraggableLabel label = new DraggableLabel( _value, this);
		return label;
	}

	public String toString(){
		if (_value.equals(Const.NULL_VALUE_STRING))
			return "null";
		return valueOf();
	}
	
	/* (non-Javadoc)
	 * @see photoSpreadObjects.PhotoSpreadObject#toDouble()
	 * Attempts to turn this string object into
	 * a double. Returns null if that is not possible. 
	 */
	public Double toDouble() {
		try {
			return Double.parseDouble(_value);
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	public String valueOf() {
		return _value;
	}
	
	@Override
	public <T extends Object>  boolean contentEquals (T str) {
		return (_value.equals((String) str));
	}

	@Override
	public String constructorArgsToXML() {
		return (PhotoSpreadHelpers.getXMLElement(XMLProcessor.OBJECT_CONSTRUCTOR_ARGUMENT_ELEMENT, getObjectID().toString())) +
        		PhotoSpreadHelpers.getXMLElement(XMLProcessor.OBJECT_CONSTRUCTOR_ARGUMENT_ELEMENT, _value);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public PhotoSpreadStringObject copyObject() {
		return new PhotoSpreadStringObject(_cell, _value);
	}
	
}
