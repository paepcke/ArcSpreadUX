/**
 * 
 */
package edu.stanford.infolab.arcspreadux.photoSpreadParser.photoSpreadExpression.photoSpreadFunctions;

import edu.stanford.infolab.arcspreadux.photoSpread.PhotoSpreadException;
import edu.stanford.infolab.arcspreadux.photoSpread.PhotoSpreadException.FormulaError;
import edu.stanford.infolab.arcspreadux.photoSpreadObjects.PhotoSpreadDoubleObject;
import edu.stanford.infolab.arcspreadux.photoSpreadObjects.PhotoSpreadObject;
import edu.stanford.infolab.arcspreadux.photoSpreadTable.PhotoSpreadCell;
import edu.stanford.infolab.arcspreadux.photoSpreadUtilities.PhotoSpreadObjIndexerFinder;
import edu.stanford.infolab.arcspreadux.photoSpreadUtilities.TreeSetRandomSubsetIterable;

/**
 * @author paepcke
 * 
 * Compute perplexity measure, given a language model,
 * a set of test sentences, and an ngram arity. The language 
 * model must be in the form (e.g. for bigram model):
 *     <probability>,<word1>,<word2>
 *
 */
public class Perplexity extends PhotoSpreadFunction {

	public Perplexity() {
		this("Perplexity");
	}
	
	public Perplexity(String funcName) {
		super(funcName);
	}
	
	public Double toDouble() throws FormulaError {
		return this.valueOf().toDouble();
	}
	
	public TreeSetRandomSubsetIterable<PhotoSpreadObject> evaluate(
			PhotoSpreadCell cell) throws FormulaError {

		TreeSetRandomSubsetIterable<PhotoSpreadObject> res;

		_cell = cell;

		res = new TreeSetRandomSubsetIterable<PhotoSpreadObject>();
		res.setIndexer(new PhotoSpreadObjIndexerFinder());
		res.add(this.valueOf());
		return res;
	}

	public PhotoSpreadDoubleObject valueOf() throws FormulaError {

		Double theSum = 0.0;
		AllArgEvalResults computedArgs;
		
		// Have the three arguments to this call to 'perplexity'
		// computed. We may get multiple results for each
		// argument. This conceptual ArrayList<ArrayList<PhotoSpreadDoubleConstant>>
		// is encapsulated in the ArgEvalResults class.
		// The valueOfArgs() method returns one of that
		// class' instances, filled with results:
		
		computedArgs = valueOfArgs();
		
		// Now just add everything together:
		
		AllArgEvalResults.FlattenedArgsIterator argResultsIt =
			computedArgs.flattenedArgsIterator();

		// Get the three args: model,
		
		
		while (argResultsIt.hasNext()) {
			try {
				PhotoSpreadObject oneRes = ((PhotoSpreadObject) argResultsIt.next());
				// Argument results may be empty sets, in
				// which case next() returns null. Just skip
				// over such results:
				if (oneRes == null) continue;
				theSum += oneRes.toDouble();
			} catch (ClassCastException e) {
				throw new PhotoSpreadException.FormulaError(
						"In function '" +
						getFunctionName() +
						"' the argument '" +
						argResultsIt.getMostRecentlyFedOut() +
						"' cannot be converted to a number.");
			}
		}
		
		return new PhotoSpreadDoubleObject(_cell, theSum);
	}
}
