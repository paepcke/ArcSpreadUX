package edu.stanford.infolab.arcspreadux.photoSpreadParser.photoSpreadExpression;

import edu.stanford.infolab.arcspreadux.photoSpreadObjects.PhotoSpreadImage;
import edu.stanford.infolab.arcspreadux.photoSpreadObjects.PhotoSpreadObject;
import edu.stanford.infolab.arcspreadux.photoSpreadParser.photoSpreadExpression.photoSpreadFunctions.FunctionResultable;
import edu.stanford.infolab.arcspreadux.photoSpreadUtilities.PhotoSpreadObjIndexerFinder;
import edu.stanford.infolab.arcspreadux.photoSpreadUtilities.TreeSetRandomSubsetIterable;

public class PhotoSpreadImageConstant extends PhotoSpreadConstant 
implements FunctionResultable {
	
	PhotoSpreadImage _imgObj = null;

	public PhotoSpreadImageConstant (PhotoSpreadImage imgObj) {
		_imgObj = imgObj;
	}
	
	
	@Override
	PhotoSpreadObject getObject() {
		return _imgObj;
	}

	@Override
	TreeSetRandomSubsetIterable<PhotoSpreadObject> getObjects() {
		TreeSetRandomSubsetIterable<PhotoSpreadObject> res =
			new TreeSetRandomSubsetIterable<PhotoSpreadObject>();
		res.setIndexer(new PhotoSpreadObjIndexerFinder());
		
		res.add(getObject());
		return res;
	}

	@Override
	public Object valueOf() {
		return _imgObj;
	}
}
