package edu.stanford.infolab.arcspreadux.photoSpreadParser.photoSpreadExpression;

import edu.stanford.infolab.arcspreadux.photoSpreadObjects.PhotoSpreadDoubleObject;
import edu.stanford.infolab.arcspreadux.photoSpreadObjects.PhotoSpreadObject;
import edu.stanford.infolab.arcspreadux.photoSpreadParser.photoSpreadExpression.photoSpreadFunctions.FunctionResultable;
import edu.stanford.infolab.arcspreadux.photoSpreadTable.PhotoSpreadCell;
import edu.stanford.infolab.arcspreadux.photoSpreadUtilities.PhotoSpreadObjIndexerFinder;
import edu.stanford.infolab.arcspreadux.photoSpreadUtilities.TreeSetRandomSubsetIterable;

public class PhotoSpreadDoubleConstant extends PhotoSpreadConstant implements Comparable<PhotoSpreadDoubleConstant>,
																			  FunctionResultable {
	
	
	Double _number = 0.0;
	
	/****************************************************
	 * Constructors
	 *****************************************************/
	
	public PhotoSpreadDoubleConstant (PhotoSpreadCell cell, String numStr) {
		this(cell, Double.valueOf(numStr));
	}
	
	public PhotoSpreadDoubleConstant (PhotoSpreadCell cell, Double num) {
		_number = num;
		_cell = cell;
	}
	
	/****************************************************
	 * Methods
	 *****************************************************/

    public int compareTo(PhotoSpreadDoubleConstant doubleConst) {
    	return _number.compareTo(doubleConst._number); 
    }
	
	public String toString () {
		return "<PhotoSpreadDoubleConstant (" + _number + ")>";
	}

	public PhotoSpreadObject getObject () {
		return new PhotoSpreadDoubleObject (_cell, _number);
	}
	
	public TreeSetRandomSubsetIterable<PhotoSpreadObject> getObjects() {
		TreeSetRandomSubsetIterable<PhotoSpreadObject> res =
			new TreeSetRandomSubsetIterable<PhotoSpreadObject>();
		res.setIndexer(new PhotoSpreadObjIndexerFinder());
		res.add(getObject());
		return res;
	}
	
	@Override
	public Double valueOf () {
		return _number;
	}
	
	public void setValue (Double value) {
		_number = value;
	}

	public Double toDouble() {
		return _number;
	}
}
