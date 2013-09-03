package edu.stanford.infolab.arcspreadux.hadoop;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.io.FilenameUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.stanford.infolab.arcspreadux.photoSpread.PhotoSpread;
import edu.stanford.infolab.arcspreadux.photoSpread.PhotoSpreadException.DatabaseProblem;
import edu.stanford.infolab.arcspreadux.photoSpreadObjects.PhotoSpreadSQLite;
import edu.stanford.infolab.arcspreadux.photoSpreadTable.PhotoSpreadCell;
import edu.stanford.infolab.arcspreadux.photoSpreadTable.PhotoSpreadTableModel;
import edu.stanford.infolab.arcspreadux.photoSpreadUtilities.UUID;

public class TestSQliteAccess {

	static PhotoSpreadSQLite sqliteDB = null;
	static String testDBPath = FilenameUtils.concat(System.getProperty("user.dir"),
													"src/test/resources/sqliteTest.db");
	
	/****************************************************
	 * Pre-Phase
	 *****************************************************/
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		PhotoSpread.initDefaultProperties();
		sqliteDB = new PhotoSpreadSQLite(new PhotoSpreadCell(new PhotoSpreadTableModel(), 0, 0),
										 testDBPath,
										 new UUID("foobar")); 
	}
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testQuery() throws DatabaseProblem, SQLException {
		ResultSet res = sqliteDB.query("SELECT word FROM Ngrams WHERE frequency=3;");
		while (res.next()) {
			String resWord = res.getString("word");
			System.out.println(resWord);
		}
	}

}
