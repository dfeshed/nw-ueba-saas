package fortscale.utils.recordreader;

import fortscale.utils.recordreader.transformation.EpochtimeTransformation;
import fortscale.utils.recordreader.transformation.Transformation;
import org.junit.Assert;
import org.junit.Test;

import java.time.Instant;
import java.util.Collections;

/**
 * Unit tests for {@link ReflectionRecordReader}.
 *
 * Created by Lior Govrin on 06/06/2017.
 */
public class ReflectionRecordReaderTest {
	@Test
	public void test_reader_with_default_field_path_delimiter() {
		// First level of hierarchical record
		Record1 record1 = new Record1(1, "myFirstString", new Record2(2, new Record3("mySecondString")));
		RecordReader record1Reader = new ReflectionRecordReader(record1);
		Integer a = record1Reader.get("a", Integer.class);
		Assert.assertEquals(new Integer(1), a);
		String b = record1Reader.get("b", String.class);
		Assert.assertEquals("myFirstString", b);

		// Second level of hierarchical record
		Integer d = record1Reader.get("c.d", Integer.class);
		Assert.assertEquals(new Integer(2), d);

		// Third level of hierarchical record
		String f = record1Reader.get("c.e.f", String.class);
		Assert.assertEquals("mySecondString", f);

		// Flat record
		Record3 e = record1Reader.get("c.e", Record3.class);
		RecordReader record3Reader = new ReflectionRecordReader(e);
		f = record3Reader.get("f", String.class);
		Assert.assertEquals("mySecondString", f);
	}

	@Test
	public void test_reader_with_custom_field_path_delimiter() {
		Record1 record = new Record1(100, "numberOne", new Record2(200, new Record3("numberTwo")));
		RecordReader recordReader = new ReflectionRecordReader(record, "\\[DELIMITER\\]");
		String f = recordReader.get("c[DELIMITER]e[DELIMITER]f", String.class);
		Assert.assertEquals("numberTwo", f);
	}

	@Test
	public void reader_should_return_null_if_field_does_not_exist() {
		Record2 record = new Record2(1, new Record3("myString"));
		RecordReader recordReader = new ReflectionRecordReader(record);
		Integer x = recordReader.get("x", Integer.class);
		Assert.assertNull(x);
		String y = recordReader.get("e.y", String.class);
		Assert.assertNull(y);
	}

	@Test
	public void reader_should_return_null_if_value_is_of_unexpected_type() {
		Record3 record = new Record3("myString");
		RecordReader recordReader = new ReflectionRecordReader(record);
		Integer f = recordReader.get("f", Integer.class);
		Assert.assertNull(f);
	}

	@Test
	public void test_reader_with_epochtime_transformation() {
		Record3 record = new Record3("myString", Instant.ofEpochSecond(1483276275)); // 2017-01-01T13:11:15.000Z
		Transformation<Long> epochtimeTransformation = new EpochtimeTransformation("myEpochtime", "g", 120);
		RecordReader recordReader = new ReflectionRecordReader(record, Collections.singleton(epochtimeTransformation));
		Long myEpochtime = recordReader.get("myEpochtime", Long.class);
		Assert.assertEquals(new Long(1483276200), myEpochtime); // 2017-01-01T13:10:00.000Z
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
		@SuppressWarnings("unused") private Instant g;

		public Record3(String f, Instant g) {
			this.f = f;
			this.g = g;
		}

		public Record3(String f) {
			this(f, null);
		}
	}
}
