/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.stanford.infolab.arcspreadux.photoSpreadObjects;

import java.awt.Component;

import edu.stanford.infolab.arcspreadux.inputOutput.XMLProcessor;
import edu.stanford.infolab.arcspreadux.photoSpreadObjects.photoSpreadComponents.DraggableLabel;
import edu.stanford.infolab.arcspreadux.photoSpreadTable.PhotoSpreadCell;
import edu.stanford.infolab.arcspreadux.photoSpreadUtilities.PhotoSpreadHelpers;
import edu.stanford.infolab.arcspreadux.photoSpreadUtilities.UUID;

/**
 *
 * @author skandel
 */
public class PhotoSpreadDoubleObject extends PhotoSpreadObject {
    
    Double _value;

    public static String OBJECT_ELEMENT_NAME = "double_object";
    public static String VALUE_ELEMENT_NAME = "value";
    
    /****************************************************
	 * Constructors
	 *****************************************************/

    public PhotoSpreadDoubleObject(PhotoSpreadCell _cell, String n) {

        super(_cell, new UUID(Double.parseDouble(n)));

        this._value = new Double(n);
    }
    
    public PhotoSpreadDoubleObject(PhotoSpreadCell _cell, Double n) {
        super(_cell, new UUID(n));
           
        this._value = n;
    }

    /****************************************************
	 * Methods
	 *****************************************************/

    
    @Override
    public Component getObjectComponent(int height, int width) {
       
        DraggableLabel label = new DraggableLabel( _value.toString(), this);
        return label;
    }
    
    public String toString(){
        
        return _value.toString();
    }
    
    public Double valueOf(){
        return _value;
    }


	@Override
	public Double toDouble() throws ClassCastException {
		return _value;
	}
	
	@Override
	public <T extends Object>  boolean contentEquals (T num) {
		try {
			return (_value == (Double) num);
		} catch (Exception e) {
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	public PhotoSpreadDoubleObject copyObject() {
		return new PhotoSpreadDoubleObject(_cell, _value);
	}
    
    @Override
    public String constructorArgsToXML() {
        return PhotoSpreadHelpers.getXMLElement(XMLProcessor.OBJECT_CONSTRUCTOR_ARGUMENT_ELEMENT,  _value.toString());
    
    }
}
