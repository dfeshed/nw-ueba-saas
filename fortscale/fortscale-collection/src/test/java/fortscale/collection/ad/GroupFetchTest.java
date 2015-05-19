package fortscale.collection.ad;

import fortscale.collection.io.BufferedLineReader;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GroupFetchTest {

	private BufferedLineReader subject; 
	
	@Before
	public void setUp() {
		subject = new BufferedLineReader();
	}
	
	@Test
	public void open_non_existing_file_should_not_throw_exception() {
		File file = mock(File.class);
		when(file.exists()).thenReturn(false);
		
		subject.open(file);
	}
	
	@Test
	public void open_with_null_file_should_not_throw_exception() {
		subject.open(null);
	}
	
	@Test
	public void open_with_directory_should_not_throw_exception() {
		File file = mock(File.class);
		when(file.exists()).thenReturn(true);
		when(file.isDirectory()).thenReturn(true);
		when(file.isFile()).thenReturn(false);
		
		subject.open(file);
	}
	
	@Test
	public void readLine_after_failed_open_should_return_null() {
		subject.open(null);
	
		String line = subject.readLine();
		assertNull(line);
	}
	
	@Test
	public void close_after_failed_open_should_not_fail() {
		subject.open(null);
		
		subject.close();
	}

}
