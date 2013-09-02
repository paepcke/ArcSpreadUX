package edu.stanford.infolab.arcspreadux.photoSpreadParser.photoSpreadExpression.photoSpreadFunctions;

import java.util.NoSuchElementException;

import edu.stanford.infolab.arcspreadux.photoSpread.PhotoSpreadException.FormulaError;
import edu.stanford.infolab.arcspreadux.photoSpreadObjects.PhotoSpreadDoubleObject;
import edu.stanford.infolab.arcspreadux.photoSpreadObjects.PhotoSpreadObject;
import edu.stanford.infolab.arcspreadux.photoSpreadParser.photoSpreadExpression.PhotoSpreadEvaluatable;
import edu.stanford.infolab.arcspreadux.photoSpreadParser.photoSpreadExpression.PhotoSpreadFormulaExpression;
import edu.stanford.infolab.arcspreadux.photoSpreadTable.PhotoSpreadCell;
import edu.stanford.infolab.arcspreadux.photoSpreadUtilities.PhotoSpreadObjIndexerFinder;
import edu.stanford.infolab.arcspreadux.photoSpreadUtilities.TreeSetRandomSubsetIterable;

public class Count<A extends PhotoSpreadFormulaExpression> extends
		PhotoSpreadFunction implements PhotoSpreadEvaluatable {
	
	public Count() {
		this("Count");
	}

	public Count(String _functionName) {
        super(_functionName);
    }
	
	@Override
	public TreeSetRandomSubsetIterable<PhotoSpreadObject> evaluate(
			PhotoSpreadCell cell) throws FormulaError {
		
		TreeSetRandomSubsetIterable<PhotoSpreadObject> res =
			new TreeSetRandomSubsetIterable<PhotoSpreadObject>();
		res.setIndexer(new PhotoSpreadObjIndexerFinder());
		res.add(this.valueOf());
		return res;
	}

	public PhotoSpreadDoubleObject valueOf() throws FormulaError {
		
		AllArgEvalResults computedArgs;
		
		// Have all the arguments to this call to 'Count'
		// computed. We may get multiple results for each
		// argument. This conceptual ArrayList<ArrayList<PhotoSpreadxxx>>
		// is encapsulated in the ArgEvalResults class.
		// The valueOfArgs() method returns one of that
		// class' instances, filled with results:
		
		computedArgs = valueOfArgs();

		// Now count the number of arguments:

		AllArgEvalResults.FlattenedArgsIterator argResultsIt =
			computedArgs.flattenedArgsIterator();
		
		// We need to pull the args out from the above iterator
		// one by one and count them, b/c the iterator does the
		// flattening on the fly:
		double numObjs = 0;
		while (true) {
			try {
				argResultsIt.next();
				numObjs++;
			} catch (NoSuchElementException e) {
				break;
			}
		}
		return new PhotoSpreadDoubleObject(_cell, numObjs);
	}
}
