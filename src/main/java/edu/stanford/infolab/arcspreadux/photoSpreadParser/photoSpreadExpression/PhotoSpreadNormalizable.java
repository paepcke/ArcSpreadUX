/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.stanford.infolab.arcspreadux.photoSpreadParser.photoSpreadExpression;

import edu.stanford.infolab.arcspreadux.photoSpreadParser.photoSpreadNormalizedExpression.PhotoSpreadNormalizedExpression;
import edu.stanford.infolab.arcspreadux.photoSpreadTable.PhotoSpreadCell;

/**
 *
 * @author skandel
 */
public interface PhotoSpreadNormalizable {
    
    PhotoSpreadNormalizedExpression normalize(PhotoSpreadCell cell);
    
}
