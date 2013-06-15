package edu.stanford.infolab.arcspreadux.photoSpreadParser.photoSpreadExpression;

import edu.stanford.infolab.arcspreadux.photoSpreadObjects.PhotoSpreadObject;
import edu.stanford.infolab.arcspreadux.photoSpreadObjects.PhotoSpreadStringObject;
import edu.stanford.infolab.arcspreadux.photoSpreadTable.PhotoSpreadCell;
import edu.stanford.infolab.arcspreadux.photoSpreadUtilities.PhotoSpreadObjIndexerFinder;
import edu.stanford.infolab.arcspreadux.photoSpreadUtilities.TreeSetRandomSubsetIterable;

public class PhotoSpreadStringConstant extends PhotoSpreadConstant {
	
	String _str = "";
	
	public PhotoSpreadStringConstant (PhotoSpreadCell cell, String str) {
		_str = str;
		_cell = cell;
	}
	
	public String toString () {
		return "<PhotoSpreadStringConstant '" + _str + "'>";
	}
	
	public PhotoSpreadObject getObject () {
		return new PhotoSpreadStringObject (_cell, _str);
	}
	
	public TreeSetRandomSubsetIterable<PhotoSpreadObject> getObjects() {
		TreeSetRandomSubsetIterable<PhotoSpreadObject> res =
			new TreeSetRandomSubsetIterable<PhotoSpreadObject>();
		res.setIndexer(new PhotoSpreadObjIndexerFinder());
		res.add(getObject());
		return res;
	}

	@Override
	public String valueOf () {
		return _str;
	}
}
