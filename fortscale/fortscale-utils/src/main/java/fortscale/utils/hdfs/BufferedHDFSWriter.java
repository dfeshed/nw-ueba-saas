package fortscale.utils.hdfs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;

/**
 * HDFS writer that buffers events written to the underlying writer. Used in order to reduce 
 * the time a connection to hdfs open file is kept alive
 */
public class BufferedHDFSWriter implements HDFSWriter {

	private HDFSWriter writer;
	private String filename;
	private WriteBuffer buffer;
	
	public BufferedHDFSWriter(HDFSWriter writer, String filename, int maxBufferSize) {
		this.writer = writer;
		this.filename = filename;
		
		// create buffer for items written to hdfs
		this.buffer = new WriteBuffer(maxBufferSize);
	}

	public HDFSWriter getWriter() {
		return writer;
	}
	
	@Override
	public void open(String filename) throws IOException {
		// do nothing
	}

	@Override
	public void writeLine(String line) throws IOException {
		writeLine(line, System.currentTimeMillis());
	}

	@Override
	public void writeLine(String line, long timestamp) throws IOException {
		// add line to buffer
		buffer.add(timestamp, line);
		
		// see if the buffer exceeded the limit, if so write them all to hdfs
		if (buffer.isFull()) 
			writeBuffer();
	}

	@Override
	public void write(String str) throws IOException {
		throw new NotImplementedException("method write without timestamp is not implemented");
	}

	@Override
	public void flush() throws IOException {
		writeBuffer();
	}

	@Override
	public void close() throws IOException {
		writeBuffer();
	}

	private void writeBuffer() throws IOException {
		// write all items to hdfs appender
		writer.open(filename);
		for (WriteBuffer.LineEntry item : buffer.getItems()) {
			writer.writeLine(item.line, item.timestamp);
		}
		writer.flush();
		writer.close();
		buffer.clear();
	}
	
	
	private class WriteBuffer {
		
		public class LineEntry {
			public LineEntry(long timestamp, String line) {
				this.timestamp = timestamp;
				this.line = line;
			}
			
			public long timestamp;
			public String line;
		}
		
		private int sizeLimit;
		private List<LineEntry> buffer;
		
		public WriteBuffer(int sizeLimit) {
			this.sizeLimit = sizeLimit;
			this.buffer = new ArrayList<LineEntry>(sizeLimit);
		}
		
		public void add(long timestamp, String line) {
			buffer.add(new LineEntry(timestamp, line));
		}
		
		public boolean isFull() {
			return buffer.size()>=sizeLimit;
		}
		
		public void clear() {
			buffer.clear();
		}
		
		public Iterable<LineEntry> getItems() {
			return buffer;
		}
	}
}
