/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.stanford.infolab.arcspreadux.photoSpreadParser.photoSpreadExpression;

import edu.stanford.infolab.arcspreadux.photoSpreadObjects.PhotoSpreadObject;
import edu.stanford.infolab.arcspreadux.photoSpreadParser.photoSpreadNormalizedExpression.PhotoSpreadNormalizedExpression;
import edu.stanford.infolab.arcspreadux.photoSpreadTable.PhotoSpreadCell;
import edu.stanford.infolab.arcspreadux.photoSpreadUtilities.TreeSetRandomSubsetIterable;


/**
 *
 * @author skandel
 */
public class PhotoSpreadCellRangeCondition extends PhotoSpreadCondition {
     private PhotoSpreadCellRange _rhs;

    public PhotoSpreadCellRangeCondition(String _lhs, String _compOp, PhotoSpreadCellRange _rhs) {
        super(_lhs, _compOp);
        this._rhs = _rhs;
       
    }

    public String getLhs() {
        return _lhs;
    }

    public PhotoSpreadCellRange getRhs() {
        return _rhs;
    }
    
    @Override
    public String toString() {
        
        return _lhs + " " + this._comparisionAsString + " " + _rhs.toString();
        
    }

    @Override
    public String copyCondition(int rowOffset, int colOffset){
        return _lhs + " " + this._comparisionAsString + " " + _rhs.copyCellRange(rowOffset, colOffset);
    }
        
    public boolean satisfiesCondition(PhotoSpreadObject object){
        
        String value = object.getMetaData(_lhs);
        return _compOp.satisfiesOperator(value, _rhs.evaluate(object.getCell()));
    }

    @Override
    public boolean canForceObject(PhotoSpreadObject object) {
        return super.canForceObject(object) && _rhs.evaluate(object.getCell()).size() <= 1;
    }

    @Override
    public void forceObject(PhotoSpreadObject object) {
        _compOp.forceObject(object, _lhs, _rhs.evaluate(object.getCell()));
    }

	public TreeSetRandomSubsetIterable<PhotoSpreadObject> evaluate(
			PhotoSpreadCell cell) {
		// TODO Auto-generated method stub
		return null;
	}

	public PhotoSpreadNormalizedExpression normalize(PhotoSpreadCell cell) {
		throw new RuntimeException("Normalize not implemented for PhotoSpreadCellRange");
	}
}
