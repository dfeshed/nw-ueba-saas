package com.rsa.netwitness.presidio.automation.test.enrichment;


import com.rsa.netwitness.presidio.automation.common.scenarios.tls.SessionSplitEnrichmentData;
import com.rsa.netwitness.presidio.automation.domain.config.MongoConfig;
import com.rsa.netwitness.presidio.automation.domain.config.store.NetwitnessEventStoreConfig;
import com.rsa.netwitness.presidio.automation.domain.repository.TlsEnrichStoredDataRepository;
import com.rsa.netwitness.presidio.automation.domain.tls.TlsEnrichStoredData;
import com.rsa.netwitness.presidio.automation.utils.adapter.config.AdapterTestManagerConfig;
import org.assertj.core.api.SoftAssertions;
import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static com.rsa.netwitness.presidio.automation.common.scenarios.tls.SessionSplitEnrichmentData.MARKER;
import static org.assertj.core.api.Assertions.fail;

@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true"})
@SpringBootTest(classes = {MongoConfig.class, AdapterTestManagerConfig.class, NetwitnessEventStoreConfig.class})
public class TlsSessionSplitTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private TlsEnrichStoredDataRepository tlsEnrichStoredDataRepository;


    @Test
    public void ten_splitted_events_have_enriched_fields() {
        AssertHelper assertHelper = new AssertHelper();
        SessionSplitEnrichmentData.TestDataParameters expected = SessionSplitEnrichmentData.simpleEnrichmentTestDataParams;
        List<TlsEnrichStoredData> actual = tlsEnrichStoredDataRepository.findByIdContains(expected.id);
        assertHelper.assertEnreachmentFieldsMatchExpected(actual, expected);
        assertHelper.assertAll();
    }

    @Test
    public void zero_split_session_before_12_hours_is_ignored() {
        AssertHelper assertHelper = new AssertHelper();
        SessionSplitEnrichmentData.TestDataParameters expected = SessionSplitEnrichmentData.maxIntervalTestDataParams;
        List<TlsEnrichStoredData> result = tlsEnrichStoredDataRepository.findByIdContains(expected.id);
        List<TlsEnrichStoredData> actual = result.subList(1, result.size());
        assertHelper.assertEnreachmentFieldsAreNull(actual);
        assertHelper.assertAll();
    }


    @Test
    public void missing_sessions_in_the_middle() {
        AssertHelper assertHelper = new AssertHelper();
        SessionSplitEnrichmentData.TestDataParameters expected = SessionSplitEnrichmentData.missingSessionsInTheMiddleTestDataParams;
        List<TlsEnrichStoredData> result = tlsEnrichStoredDataRepository.findByIdContains(expected.id);
        result.sort(Comparator.comparing(TlsEnrichStoredData::getId));
        Optional<Integer> first = IntStream.range(0, result.size()).filter(i -> result.get(i).getEventId().startsWith(MARKER)).boxed().findFirst();
        if (!first.isPresent()) fail("First missing event id marker is missing");
        int firstIndex = first.get();

        /** enrichment should be until the first missing element*/
        List<TlsEnrichStoredData> actualEnriched = result.subList(0, firstIndex + 1);
        List<TlsEnrichStoredData> actualNotEnriched = result.subList(firstIndex + 2, result.size());
        assertHelper.assertEnreachmentFieldsMatchExpected(actualEnriched, expected);
        assertHelper.assertEnreachmentFieldsAreNull(actualNotEnriched);
        assertHelper.assertAll();
    }

    @Ignore("ASOC-81706")
    public void duplicated_sessions_in_the_middle() {
        AssertHelper assertHelper = new AssertHelper();
        SessionSplitEnrichmentData.TestDataParameters expected = SessionSplitEnrichmentData.duplicatedSessionsInTheMiddleTestDataParams;
        List<TlsEnrichStoredData> result = tlsEnrichStoredDataRepository.findByIdContains(expected.id);
        result.sort(Comparator.comparing(TlsEnrichStoredData::getId));
        Optional<Integer> first = IntStream.range(0, result.size()).filter(i -> result.get(i).getEventId().startsWith(MARKER)).boxed().findFirst();
        if (!first.isPresent()) fail("First duplicate event id marker is missing");
        int firstIndex = first.get();

        /** enrichment should be until the first duplicate element*/
        List<TlsEnrichStoredData> actualEnriched = result.subList(0, firstIndex + 1);
        List<TlsEnrichStoredData> actualNotEnriched = result.subList(firstIndex + 2, result.size());
        assertHelper.assertEnreachmentFieldsMatchExpected(actualEnriched, expected);
        assertHelper.assertEnreachmentFieldsAreNull(actualNotEnriched);
        assertHelper.assertAll();
    }

    @Test
    public void missing_zero_session() {
        AssertHelper assertHelper = new AssertHelper();
        SessionSplitEnrichmentData.TestDataParameters expected = SessionSplitEnrichmentData.missingZeroSessionTestDataParams;
        List<TlsEnrichStoredData> actual = tlsEnrichStoredDataRepository.findByIdContains(expected.id);
        assertHelper.assertEnreachmentFieldsAreNull(actual);
        assertHelper.assertAll();
    }

    @Test
    public void multiple_sessions_from_same_src_to_same_dest() {
        AssertHelper assertHelper = new AssertHelper();
        SessionSplitEnrichmentData.TestDataParameters expectedSession1 = SessionSplitEnrichmentData.newSessionOpenedFirstSession;
        List<TlsEnrichStoredData> actualSession1 = tlsEnrichStoredDataRepository.findByIdContains(expectedSession1.id);
        assertHelper.assertEnreachmentFieldsMatchExpected(actualSession1, expectedSession1);

        SessionSplitEnrichmentData.TestDataParameters expectedSession2 = SessionSplitEnrichmentData.newSessionOpenedSecondSession;
        List<TlsEnrichStoredData> actualSession2 = tlsEnrichStoredDataRepository.findByIdContains(expectedSession2.id);
        assertHelper.assertEnreachmentFieldsMatchExpected(actualSession2, expectedSession2);

        assertHelper.assertAll();
    }

    @Ignore("ASOC-81706")
    public void multiple_sessions_with_missing_zero_session() {
        AssertHelper assertHelper = new AssertHelper();
        SessionSplitEnrichmentData.TestDataParameters expected = SessionSplitEnrichmentData.newSessionOpenedAndZeroEventIsMissing;
        List<TlsEnrichStoredData> result = tlsEnrichStoredDataRepository.findByIdContains(expected.id);
        result.sort(Comparator.comparing(TlsEnrichStoredData::getId));
        int firstIndex = 4;

        /** enrichment should be until the first new session element*/
        List<TlsEnrichStoredData> actualEnriched = result.subList(0, firstIndex);
        List<TlsEnrichStoredData> actualNotEnriched = result.subList(firstIndex + 1, result.size());
        assertHelper.assertEnreachmentFieldsMatchExpected(actualEnriched, expected);
        assertHelper.assertEnreachmentFieldsAreNull(actualNotEnriched);
        assertHelper.assertAll();
    }

    @Test
    public void zero_session_event_has_null_ja3_and_ssl_subject() {
        SoftAssertions softly = new SoftAssertions();
        SessionSplitEnrichmentData.TestDataParameters expected = SessionSplitEnrichmentData.zeroSessionEventHasNullJa3AndSslSubject;
        List<TlsEnrichStoredData> result = tlsEnrichStoredDataRepository.findByIdContains(expected.id);
        List<TlsEnrichStoredData> actual = result.subList(1, result.size());

        softly.assertThat(actual).extracting("sslSubject").containsOnlyNulls();
        softly.assertThat(actual).extracting("ja3").containsOnlyNulls();
        softly.assertThat(actual).extracting("ja3s").containsOnly(expected.ja3s);

        softly.assertAll();
    }


    @Test
    public void second_event_have_ja3_and_ssl_subject() {
        AssertHelper assertHelper = new AssertHelper();
        SessionSplitEnrichmentData.TestDataParameters expected = SessionSplitEnrichmentData.secondEventHaveJa3AndSslSubject;
        List<TlsEnrichStoredData> actual = tlsEnrichStoredDataRepository.findByIdContains(expected.id);
        assertHelper.assertEnreachmentFieldsMatchExpected(actual, expected);
        assertHelper.assertAll();
    }


    class AssertHelper {
        private SoftAssertions softly = new SoftAssertions();

        void assertEnrichmentFieldsMatchExpected(List<TlsEnrichStoredData> actual, SessionSplitEnrichmentData.TestDataParameters expected) {
            softly.assertThat(actual).extracting("sslSubject.name").containsOnly(expected.sslSubject);
            softly.assertThat(actual).extracting("ja3.name").containsOnly(expected.ja3);
            softly.assertThat(actual).extracting("ja3s").containsOnly(expected.ja3s);
            // todo: softly.assertThat(actual).extracting("sslCa").containsOnly(expected.sslCa);
        }

        void assertEnrichmentFieldsAreNull(List<TlsEnrichStoredData> actual) {
            softly.assertThat(actual).extracting("sslSubject.name").containsOnlyNulls();
            softly.assertThat(actual).extracting("ja3.name").containsOnlyNulls();
            softly.assertThat(actual).extracting("ja3s").containsOnlyNulls();
            // todo: softly.assertThat(actual).extracting("sslCa").containsOnlyNulls();
        }

        void assertAll() {
            softly.assertAll();
        }
    }
}
