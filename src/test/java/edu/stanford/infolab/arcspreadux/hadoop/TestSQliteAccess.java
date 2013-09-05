package edu.stanford.infolab.arcspreadux.hadoop;

import static org.junit.Assert.assertEquals;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
		
	}
	
	@Before
	public void setUp() throws Exception {
		PhotoSpread.initDefaultProperties();
		sqliteDB = new PhotoSpreadSQLite(new PhotoSpreadCell(new PhotoSpreadTableModel(), 0, 0),
										 testDBPath,
										 new UUID("foobar")); 
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testQuery() throws DatabaseProblem, SQLException {
		try {
			ResultSet res = sqliteDB.query("SELECT word FROM Ngrams WHERE frequency=3;");
			List<String> resVals = new ArrayList<String>(); 
			while (res.next()) {
				String resWord = res.getString("word");
				resVals.add(resWord);
				//System.out.println(resWord);
			}
			String[] trueRes = new String[] {"one", "one", "one", "three", "six", "eight", "ten", "twelve"};     
			for (int i=0; i<trueRes.length; i++) {
				assertEquals(trueRes[i], resVals.get(i));
			}
		} finally {
			sqliteDB.closeAll();
		}
	}
	
	public void testResultSetColNum() throws DatabaseProblem, SQLException {
		try {
			ResultSet res = sqliteDB.query("SELECT word FROM Ngrams WHERE frequency=3;");
			// Result is just one column wide:
			assertEquals(1, sqliteDB.getNumColumns(res));
		} finally {
			sqliteDB.closeAll();
		}
	}
	
	@Test
	public void testGetNumCols() throws SQLException {
		int numCols = sqliteDB.getNumColumns("Ngrams");
		// Number cols should be: word,follower,frequency:
		assertEquals(3,numCols);
	}

}
