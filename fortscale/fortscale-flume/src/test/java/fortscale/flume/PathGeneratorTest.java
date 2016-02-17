package fortscale.flume;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;

public class PathGeneratorTest {

	@Test
	public void testGetCurrentFileWithoutDataCenterName() throws Exception {

		PathGenerator pathGenerator = new PathGenerator();
		pathGenerator.setBaseDirectory(new File(""));
		pathGenerator.setDatacenterName(null);
		pathGenerator.setFilePrefix("SEC");
		pathGenerator.setFileSuffix("csv");

		File currentFile = pathGenerator.getCurrentFile();
		String fileName = currentFile.getName();
		fileName = fileName.substring(0,fileName.indexOf(".part"));
		String [] prefixArray = fileName.split("_");

		//validate that there is no data center name at the file name
		assertTrue(prefixArray.length == 2);

		String suffix = prefixArray[1].split("\\.")[1];


		assertTrue(suffix.equals("csv") && prefixArray[0].equals("SEC"));

	}
}
