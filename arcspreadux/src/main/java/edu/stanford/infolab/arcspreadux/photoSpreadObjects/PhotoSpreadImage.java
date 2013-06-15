/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.stanford.infolab.arcspreadux.photoSpreadObjects;


import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import edu.stanford.infolab.arcspreadux.photoSpread.PhotoSpreadException.BadUUIDStringError;
import edu.stanford.infolab.arcspreadux.photoSpread.PhotoSpreadException.CannotLoadImage;
import edu.stanford.infolab.arcspreadux.photoSpreadLoaders.PhotoSpreadImageLoader;
import edu.stanford.infolab.arcspreadux.photoSpreadObjects.photoSpreadComponents.DraggableLabel;
import edu.stanford.infolab.arcspreadux.photoSpreadTable.PhotoSpreadCell;
import edu.stanford.infolab.arcspreadux.photoSpreadUtilities.UUID;
import edu.stanford.infolab.arcspreadux.photoSpreadUtilities.UUID.FileHashMethod;

/**
 *
 * @author skandel
 */
public class PhotoSpreadImage extends PhotoSpreadFileObject {

	private PhotoSpreadImageLoader _pil;


	/****************************************************
	 * Constructor(s)
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 *****************************************************/

	public PhotoSpreadImage(PhotoSpreadCell _cell, String _filePath) 
	throws FileNotFoundException, IOException {
		super(_cell, _filePath, new UUID(new File(_filePath), FileHashMethod.USE_FILE_SAMPLING));

		_pil = new PhotoSpreadImageLoader();
	}

	public PhotoSpreadImage(PhotoSpreadCell _cell, UUID _objectId, String _filePath) {
		super(_cell, _filePath, _objectId);

		_pil = new PhotoSpreadImageLoader();
	}
	
	/**
	 * This constructor is used when an XML saved-sheet
	 * file is loaded.
	 * @param _cell
	 * @param objectIdStr
	 * @param _filePath
	 * @throws BadUUIDStringError
	 */
	public PhotoSpreadImage(PhotoSpreadCell _cell, String objectIdStr, String _filePath) 
	throws BadUUIDStringError {
		super(_cell, _filePath, UUID.createFromUUIDString(objectIdStr));

		_pil = new PhotoSpreadImageLoader();
	}
	
	
	/****************************************************
	 * Methods
	 * @throws CannotLoadImage 
	 *****************************************************/

	public Component getObjectComponent( int height, int width) throws CannotLoadImage{
		return loadImageLabel(height, width);
	}

	public Component getWorkspaceComponent( int height, int width) throws CannotLoadImage{
		return _pil.getImageComponent(this, _filePath);
	}

	private DraggableLabel loadImageLabel( int height, int width) throws CannotLoadImage{
		return _pil.getImageComponent(this, _filePath, height, width);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public PhotoSpreadImage copyObject() {
		return new PhotoSpreadImage(_cell, getObjectID(), _filePath);
	}
	
}
