package fortscale.ml.scorer.record;

import org.springframework.data.mongodb.core.mapping.Document;
import presidio.ade.domain.record.AdeRecord;
import presidio.ade.domain.record.AdeRecordReader;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

/**
 * An ADE record for scorer unit tests.
 *
 * Created by Lior Govrin on 29/06/2017.
 */
@Document
public class TestAdeRecord extends AdeRecord {
	@SuppressWarnings("all") private String username;
	@SuppressWarnings("all") private String sourceMachine;
	@SuppressWarnings("all") private String featureFieldName;
	@SuppressWarnings("all") private String city;
	@SuppressWarnings("all") private String sourceIp;
	@SuppressWarnings("all") private Double writeBytes;
	@SuppressWarnings("all") private Double readBytes;
	@SuppressWarnings("all") private Double totalBytes;
	@SuppressWarnings("all") private String field1;
	@SuppressWarnings("all") private String context;
	@SuppressWarnings("all") private String testFieldName1;
	@SuppressWarnings("all") private String testFieldName2;

	public TestAdeRecord(Instant date_time) {
		super(date_time);
	}

	public TestAdeRecord() {
		this(Instant.now());
	}

	@Override
	public String getAdeEventType() {
		return "test";
	}

	@Override
	public List<String> getDataSources() {
		return Collections.singletonList(getAdeEventType());
	}

	public AdeRecordReader getAdeRecordReader() {
		return new AdeRecordReader(this);
	}

	public TestAdeRecord setUsername(String username) {
		this.username = username;
		return this;
	}

	public TestAdeRecord setSourceMachine(String sourceMachine) {
		this.sourceMachine = sourceMachine;
		return this;
	}

	public TestAdeRecord setFeatureFieldName(String featureFieldName) {
		this.featureFieldName = featureFieldName;
		return this;
	}

	public TestAdeRecord setCity(String city) {
		this.city = city;
		return this;
	}

	public TestAdeRecord setSourceIp(String sourceIp) {
		this.sourceIp = sourceIp;
		return this;
	}

	public TestAdeRecord setWriteBytes(Double writeBytes) {
		this.writeBytes = writeBytes;
		return this;
	}

	public TestAdeRecord setReadBytes(Double readBytes) {
		this.readBytes = readBytes;
		return this;
	}

	public TestAdeRecord setTotalBytes(Double totalBytes) {
		this.totalBytes = totalBytes;
		return this;
	}

	public TestAdeRecord setField1(String field1) {
		this.field1 = field1;
		return this;
	}

	public TestAdeRecord setContext(String context) {
		this.context = context;
		return this;
	}

	public TestAdeRecord setTestFieldName1(String testFieldName1) {
		this.testFieldName1 = testFieldName1;
		return this;
	}

	public TestAdeRecord setTestFieldName2(String testFieldName2) {
		this.testFieldName2 = testFieldName2;
		return this;
	}
}
