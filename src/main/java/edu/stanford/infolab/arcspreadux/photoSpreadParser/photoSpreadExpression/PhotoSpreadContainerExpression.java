/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.stanford.infolab.arcspreadux.photoSpreadParser.photoSpreadExpression;

import edu.stanford.infolab.arcspreadux.photoSpread.PhotoSpread;
import edu.stanford.infolab.arcspreadux.photoSpreadObjects.PhotoSpreadObject;
import edu.stanford.infolab.arcspreadux.photoSpreadParser.photoSpreadNormalizedExpression.PhotoSpreadNormalizedExpression;
import edu.stanford.infolab.arcspreadux.photoSpreadTable.PhotoSpreadCell;
import edu.stanford.infolab.arcspreadux.photoSpreadUtilities.TreeSetRandomSubsetIterable;

/**
 *
 * @author skandel
 */
public class PhotoSpreadContainerExpression extends PhotoSpreadFormulaExpression {
    
    PhotoSpreadCellRange _cellRange;

    public PhotoSpreadContainerExpression(PhotoSpreadCellRange _cellRange) {
        super();
        this._cellRange = _cellRange;
		PhotoSpread.trace("New " + this);
    }

    @Override
    public String copyExpression(int rowOffset, int colOffset) {
        return _cellRange.copyCellRange(rowOffset, colOffset) + 
        conditionsAndSelectionToString(rowOffset, colOffset);
    }

    @Override
    public TreeSetRandomSubsetIterable<PhotoSpreadObject> evaluate(PhotoSpreadCell cell) {
        
        TreeSetRandomSubsetIterable<PhotoSpreadObject> objects  = 
                _cellRange.evaluate(cell);
        
        objects = applyConditionsAndSelection( objects);
        return objects;
    }

    @Override
    public PhotoSpreadNormalizedExpression normalize(PhotoSpreadCell cell) {
        
        PhotoSpreadNormalizedExpression normalizedExpression = _cellRange.normalize(cell);
        normalizedExpression.addConditions(_conditions);
		PhotoSpread.trace("Normalized expression to " + normalizedExpression);
        return normalizedExpression;
    }
    
	@Override
	public String toString() {

		String ce = "<PhotoSpreadContainerExpression ";
		if (_cellRange != null)
			ce += _cellRange.toString();
		if (_conditions != null) 
			if (_conditions.size() > 0) {
				ce += "[";
				ce += _conditions.get(0).toString();
				for(int i = 1; i < _conditions.size(); i++){
					ce += " & " + _conditions.get(i).toString();
				}
				ce += "]";
			}
		if (_selection != null){
			ce += ".<String " + _selection + ">";
		}

		return ce + ">";
	}    
}
