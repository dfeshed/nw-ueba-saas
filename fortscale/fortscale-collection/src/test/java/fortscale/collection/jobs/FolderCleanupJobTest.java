package fortscale.collection.jobs;

import static org.mockito.Mockito.*;

import java.io.File;

import org.junit.Test;
import org.junit.Before;
import org.mockito.internal.verification.VerificationModeFactory;

public class FolderCleanupJobTest {

	private FolderCleanupJob subject;
	private File folderMock;
	private File fileA;
	private File fileB;
	
	@Before
	public void setup() {
		subject = new FolderCleanupJob();
		folderMock = mock(File.class);
		when(folderMock.isDirectory()).thenReturn(new Boolean(true));
		when(folderMock.exists()).thenReturn(new Boolean(true));
		
		// create mock files in folder
		fileA = mock(File.class);
		when(fileA.getName()).thenReturn("a.txt");
		when(fileA.isDirectory()).thenReturn(false);
		when(fileA.compareTo(any(File.class))).thenReturn(new Integer(-1));
		
		fileB = mock(File.class);
		when(fileB.getName()).thenReturn("b.txt");
		when(fileB.isDirectory()).thenReturn(false);
		when(fileB.compareTo(any(File.class))).thenReturn(new Integer(1));
		
		File[] contentList = new File[] { fileA, fileB };
		when(folderMock.listFiles()).thenReturn(contentList);
	}
	
	@Test
	public void usable_space_greater_then_threshold_should_not_delete_files() throws Exception {
		// arrange
		when(folderMock.getUsableSpace()).thenReturn(new Long(60 * 1024 * 1024));
		
		// act
		subject.cleanupFolder(folderMock, 50);
		
		// assert
		verifyNoMoreInteractions(fileA, fileB);
	}

	@Test
	public void cleanup_should_delete_only_the_needed_amount_of_files_to_get_over_the_threshold() throws Exception {
		// arrange
		when(folderMock.getUsableSpace()).thenReturn(new Long(40 * 1024 * 1024)).thenReturn(new Long(60 * 1024 * 1024));
		when(fileA.delete()).thenReturn(Boolean.TRUE);
		
		// act
		subject.cleanupFolder(folderMock, 50);
		
		// assert
		verify(fileA).delete();
		verify(fileB, VerificationModeFactory.times(0)).delete();
	}
	
}
