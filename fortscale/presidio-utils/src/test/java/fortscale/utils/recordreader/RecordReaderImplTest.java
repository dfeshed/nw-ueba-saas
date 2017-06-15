package fortscale.utils.recordreader;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link RecordReaderImpl}.
 * Created by Lior Govrin on 06/06/2017.
 */
public class RecordReaderImplTest {
	@Test
	public void test_record_reader_impl_with_default_field_path_delimiter() {
		// First level of hierarchical record
		Record1 record1 = new Record1(1, "myFirstString", new Record2(2, new Record3("mySecondString")));
		RecordReader<Record1> record1Reader = new RecordReaderImpl<>();
		Integer a = record1Reader.get(record1, "a", Integer.class);
		Assert.assertEquals(new Integer(1), a);
		String b = record1Reader.get(record1, "b", String.class);
		Assert.assertEquals("myFirstString", b);

		// Second level of hierarchical record
		Integer d = record1Reader.get(record1, "c.d", Integer.class);
		Assert.assertEquals(new Integer(2), d);

		// Third level of hierarchical record
		String f = record1Reader.get(record1, "c.e.f", String.class);
		Assert.assertEquals("mySecondString", f);

		// Flat record
		Record3 e = record1Reader.get(record1, "c.e", Record3.class);
		RecordReader<Record3> record3Reader = new RecordReaderImpl<>();
		f = record3Reader.get(e, "f", String.class);
		Assert.assertEquals("mySecondString", f);
	}

	@Test
	public void test_record_reader_impl_with_custom_field_path_delimiter() {
		Record1 record = new Record1(100, "numberOne", new Record2(200, new Record3("numberTwo")));
		RecordReader<Record1> recordReader = new RecordReaderImpl<>("\\[DELIMITER\\]");
		String f = recordReader.get(record, "c[DELIMITER]e[DELIMITER]f", String.class);
		Assert.assertEquals("numberTwo", f);
	}

	@Test
	public void should_return_null_if_field_does_not_exist() {
		Record2 record = new Record2(1, new Record3("myString"));
		RecordReader<Record2> recordReader = new RecordReaderImpl<>();
		Integer x = recordReader.get(record, "x", Integer.class);
		Assert.assertNull(x);
		String y = recordReader.get(record, "e.y", String.class);
		Assert.assertNull(y);
	}

	@Test
	public void should_return_null_if_value_is_of_unexpected_type() {
		Record3 record = new Record3("myString");
		RecordReader<Record3> recordReader = new RecordReaderImpl<>();
		Integer f = recordReader.get(record, "f", Integer.class);
		Assert.assertNull(f);
	}

	/**
	 * Example records.
	 */
	private static final class Record1 {
		@SuppressWarnings("unused") private Integer a;
		@SuppressWarnings("unused") private String b;
		@SuppressWarnings("unused") private Record2 c;

		public Record1(Integer a, String b, Record2 c) {
			this.a = a;
			this.b = b;
			this.c = c;
		}
	}

	private static final class Record2 {
		@SuppressWarnings("unused") private Integer d;
		@SuppressWarnings("unused") private Record3 e;

		public Record2(Integer d, Record3 e) {
			this.d = d;
			this.e = e;
		}
	}

	private static final class Record3 {
		@SuppressWarnings("unused") private String f;

		public Record3(String f) {
			this.f = f;
		}
	}
}
