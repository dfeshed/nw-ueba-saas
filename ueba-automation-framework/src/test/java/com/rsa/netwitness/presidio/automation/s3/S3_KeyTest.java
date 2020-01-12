package com.rsa.netwitness.presidio.automation.s3;

import fortscale.common.general.Schema;
import org.junit.Test;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class S3_KeyTest {

    private S3_Key key = new S3_Key();
    private String middle = "123456789012_us-east-1_NetworkTraffic_";
    private String prefix = "acme/NetWitness/123456789012/NetworkTraffic/us-east-1/";
    private String suffix = "_00000000_.json.gz";

    @Test
    public void passZeroMinuteTest() {
        Instant start = Instant.parse("2020-01-28T01:00:00.00Z");
        Instant end = Instant.parse("2020-01-28T01:00:00.00Z");

        List<String> allS3_keys = getSortedKeys(start, end);
        assertThat(allS3_keys)
                .containsExactly(prefix.concat("2020/01/28/").concat(middle).concat("20200128T0105Z").concat(suffix));
    }

    @Test
    public void passLastMinuteTest() {
        Instant start = Instant.parse("2020-01-28T01:59:59.00Z");
        Instant end = Instant.parse("2020-01-28T01:59:59.00Z");

        List<String> allS3_keys = getSortedKeys(start, end);
        assertThat(allS3_keys)
                .containsExactly(prefix.concat("2020/01/28/").concat(middle).concat("20200128T0200Z").concat(suffix));
    }

    @Test
    public void passLastOverDayTest() {
        Instant start = Instant.parse("2020-01-28T23:59:59.00Z");
        Instant end = Instant.parse("2020-01-28T23:59:59.00Z");

        List<String> allS3_keys = getSortedKeys(start, end);
        assertThat(allS3_keys)
                .containsExactly(prefix.concat("2020/01/29/").concat(middle).concat("20200129T0000Z").concat(suffix));
    }

    @Test
    public void passLastOverMonthTest() {
        Instant start = Instant.parse("2020-01-31T23:59:59.00Z");
        Instant end = Instant.parse("2020-01-31T23:59:59.00Z");

        List<String> allS3_keys = getSortedKeys(start, end);
        assertThat(allS3_keys)
                .containsExactly(prefix.concat("2020/02/01/").concat(middle).concat("20200232T0000Z").concat(suffix));
    }

    @Test
    public void passOverYearTest() {
        Instant start = Instant.parse("2019-12-31T23:50:01.00Z");
        Instant end = Instant.parse("2020-01-01T00:15:24.00Z");

        List<String> allS3_keys = getSortedKeys(start, end);
        assertThat(allS3_keys)
                .containsExactly(
                        prefix.concat("2019/12/31/").concat(middle).concat("202012365T2355Z").concat(suffix),
                        prefix.concat("2020/01/01/").concat(middle).concat("20200101T0000Z").concat(suffix),
                        prefix.concat("2020/01/01/").concat(middle).concat("20200101T0005Z").concat(suffix),
                        prefix.concat("2020/01/01/").concat(middle).concat("20200101T0010Z").concat(suffix),
                        prefix.concat("2020/01/01/").concat(middle).concat("20200101T0015Z").concat(suffix),
                        prefix.concat("2020/01/01/").concat(middle).concat("20200101T0020Z").concat(suffix)
                );

    }

    @Test
    public void passOverYearTest2() {
        Instant start = Instant.parse("2019-12-31T23:50:00.00Z");
        Instant end = Instant.parse("2020-01-01T00:00:00.00Z");

        List<String> allS3_keys = getSortedKeys(start, end);
        assertThat(allS3_keys)
                .containsExactly(
                        prefix.concat("2019/12/31/").concat(middle).concat("202012365T2355Z").concat(suffix),
                        prefix.concat("2020/01/01/").concat(middle).concat("20200101T0000Z").concat(suffix),
                        prefix.concat("2020/01/01/").concat(middle).concat("20200101T0005Z").concat(suffix)
                );

    }

    private List<String>  getSortedKeys(Instant start, Instant end) {
        return key.getAllS3_Keys(start, end, Schema.TLS)
                .stream()
                .sorted(String::compareTo)
                .collect(Collectors.toList());
    }
}