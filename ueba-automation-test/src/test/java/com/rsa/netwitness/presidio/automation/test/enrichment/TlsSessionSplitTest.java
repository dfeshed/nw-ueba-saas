package com.rsa.netwitness.presidio.automation.test.enrichment;


import ch.qos.logback.classic.Logger;
import com.rsa.netwitness.presidio.automation.common.scenarios.tls.SessionSplitEnrichmentData;
import com.rsa.netwitness.presidio.automation.domain.config.MongoConfig;
import com.rsa.netwitness.presidio.automation.domain.config.store.NetwitnessEventStoreConfig;
import com.rsa.netwitness.presidio.automation.domain.repository.TlsEnrichStoredDataRepository;
import com.rsa.netwitness.presidio.automation.domain.tls.TlsEnrichStoredData;
import com.rsa.netwitness.presidio.automation.utils.adapter.config.AdapterTestManagerConfig;
import fortscale.domain.core.entityattributes.Ja3;
import fortscale.domain.core.entityattributes.SslSubject;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.util.Lists;
import org.junit.Ignore;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.rsa.netwitness.presidio.automation.common.scenarios.tls.SessionSplitEnrichmentData.MARKER;
import static com.rsa.netwitness.presidio.automation.utils.common.LambdaUtils.getOrNull;
import static org.assertj.core.api.Assertions.fail;

@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true"})
@SpringBootTest(classes = {MongoConfig.class, AdapterTestManagerConfig.class, NetwitnessEventStoreConfig.class})
public class TlsSessionSplitTest extends AbstractTestNGSpringContextTests {
    private static Logger LOGGER = (Logger) LoggerFactory.getLogger(TlsSessionSplitTest.class);

    @Autowired
    private TlsEnrichStoredDataRepository tlsEnrichStoredDataRepository;

    /** dstOrg field contains unique event identifier including test name, marker, session split id  */

    @Test
    public void ten_splitted_events_have_enriched_fields() {
        AssertHelper assertHelper = new AssertHelper();
        SessionSplitEnrichmentData.TestDataParameters expected = SessionSplitEnrichmentData.simpleEnrichmentTestDataParams;
        List<TlsEnrichStoredData> actual = tlsEnrichStoredDataRepository.findByDstOrgContains(expected.id);
        assertHelper.assertEnrichmentFieldsMatchExpected(actual, expected);
        assertHelper.assertAll();
    }

    @Ignore
    public void zero_split_session_before_12_hours_is_ignored() {
        AssertHelper assertHelper = new AssertHelper();
        SessionSplitEnrichmentData.TestDataParameters expected = SessionSplitEnrichmentData.maxIntervalTestDataParams;
        List<TlsEnrichStoredData> result = tlsEnrichStoredDataRepository.findByDstOrgContains(expected.id);
        List<TlsEnrichStoredData> actual = result.subList(1, result.size());
        assertHelper.assertEnrichmentFieldsAreNull(actual);
        assertHelper.assertAll();
    }


    @Test
    public void missing_sessions_in_the_middle() {
        AssertHelper assertHelper = new AssertHelper();
        SessionSplitEnrichmentData.TestDataParameters expected = SessionSplitEnrichmentData.missingSessionsInTheMiddleTestDataParams;
        List<TlsEnrichStoredData> result = tlsEnrichStoredDataRepository.findByDstOrgContains(expected.id);
        result.sort(Comparator.comparing(TlsEnrichStoredData::getId));
        Optional<Integer> first = IntStream.range(0, result.size()).filter(i -> result.get(i).getDstOrg().getName().startsWith(MARKER)).boxed().findFirst();
        if (first.isEmpty()) fail("First missing event id marker is missing");
        int firstIndex = first.get();

        /** enrichment should be until the first missing element*/
        List<TlsEnrichStoredData> actualEnriched = result.subList(0, firstIndex + 1);
        List<TlsEnrichStoredData> actualNotEnriched = result.subList(firstIndex + 1, result.size());
        assertHelper.assertEnrichmentFieldsMatchExpected(actualEnriched, expected);
        assertHelper.assertEnrichmentFieldsAreNull(actualNotEnriched);
        assertHelper.assertAll();
    }

    @Test
    public void duplicated_sessions_in_the_middle() {
        AssertHelper assertHelper = new AssertHelper();
        SessionSplitEnrichmentData.TestDataParameters expected = SessionSplitEnrichmentData.duplicatedSessionsInTheMiddleTestDataParams;
        List<TlsEnrichStoredData> result = tlsEnrichStoredDataRepository.findByDstOrgContains(expected.id);
        result.sort(Comparator.comparing(TlsEnrichStoredData::getId));
        Optional<Integer> first = IntStream.range(0, result.size()).filter(i -> result.get(i).getDstOrg().getName().startsWith(MARKER)).boxed().findFirst();
        if (first.isEmpty()) fail("First duplicate event id marker is missing");
        int firstIndex = first.get();

        /** enrichment is including the first duplicate element */
        List<TlsEnrichStoredData> actualEnriched = result.subList(0, firstIndex + 2);
        List<TlsEnrichStoredData> actualNotEnriched = result.subList(firstIndex + 2, result.size());
        assertHelper.assertEnrichmentFieldsMatchExpected(actualEnriched, expected);
        assertHelper.assertEnrichmentFieldsAreNull(actualNotEnriched);
        assertHelper.assertAll();
    }

    @Test
    public void missing_zero_session() {
        AssertHelper assertHelper = new AssertHelper();
        SessionSplitEnrichmentData.TestDataParameters expected = SessionSplitEnrichmentData.missingZeroSessionTestDataParams;
        List<TlsEnrichStoredData> actual = tlsEnrichStoredDataRepository.findByDstOrgContains(expected.id);
        assertHelper.assertEnrichmentFieldsAreNull(actual);
        assertHelper.assertAll();
    }

    @Test
    public void multiple_sessions_from_same_src_to_same_dest() {
        AssertHelper assertHelper = new AssertHelper();
        SessionSplitEnrichmentData.TestDataParameters expectedSession1 = SessionSplitEnrichmentData.newSessionOpenedFirstSession;
        List<TlsEnrichStoredData> actualSession1 = tlsEnrichStoredDataRepository.findByDstOrgContains(expectedSession1.id);
        assertHelper.assertEnrichmentFieldsMatchExpected(actualSession1, expectedSession1);

        SessionSplitEnrichmentData.TestDataParameters expectedSession2 = SessionSplitEnrichmentData.newSessionOpenedSecondSession;
        List<TlsEnrichStoredData> actualSession2 = tlsEnrichStoredDataRepository.findByDstOrgContains(expectedSession2.id);
        assertHelper.assertEnrichmentFieldsMatchExpected(actualSession2, expectedSession2);

        assertHelper.assertAll();
    }

    @Test
    public void multiple_sessions_with_missing_zero_session() {
        AssertHelper assertHelper = new AssertHelper();
        SessionSplitEnrichmentData.TestDataParameters expected = SessionSplitEnrichmentData.newSessionOpenedAndZeroEventIsMissing;
        List<TlsEnrichStoredData> result = tlsEnrichStoredDataRepository.findByDstOrgContains(expected.id);
        result.sort(Comparator.comparing(TlsEnrichStoredData::getId));
        int firstIndex = 4;

        /** enrichment should be until the first new session element*/
        List<TlsEnrichStoredData> actualEnriched = result.subList(0, firstIndex);
        List<TlsEnrichStoredData> actualNotEnriched = result.subList(firstIndex + 1, result.size());
        assertHelper.assertEnrichmentFieldsMatchExpected(actualEnriched, expected);
        assertHelper.assertEnrichmentFieldsAreNull(actualNotEnriched);
        assertHelper.assertAll();
    }

    @Test
    public void zero_session_event_has_null_ja3_and_ssl_subject() {
        SoftAssertions softly = new SoftAssertions();
        SessionSplitEnrichmentData.TestDataParameters expected = SessionSplitEnrichmentData.zeroSessionEventHasNullJa3AndSslSubject;
        List<TlsEnrichStoredData> result = tlsEnrichStoredDataRepository.findByDstOrgContains(expected.id);
        List<TlsEnrichStoredData> actual = result.subList(1, result.size());

        softly.assertThat(actual).extracting("sslSubject").containsOnlyNulls();
        softly.assertThat(actual).extracting("ja3").containsOnlyNulls();
        softly.assertThat(actual).extracting("ja3s").containsOnly(expected.ja3s);
        softly.assertThat(actual).flatExtracting("sslCas").as("SslCas").containsOnly(expected.sslCa);

        softly.assertAll();
    }


    @Test
    public void second_event_have_ja3_and_ssl_subject() {
        AssertHelper assertHelper = new AssertHelper();
        SessionSplitEnrichmentData.TestDataParameters expected = SessionSplitEnrichmentData.secondEventHaveJa3AndSslSubject;
        List<TlsEnrichStoredData> actual = tlsEnrichStoredDataRepository.findByDstOrgContains(expected.id);
        assertHelper.assertEnrichmentFieldsMatchExpected(actual, expected);
        assertHelper.assertAll();
    }


    class AssertHelper {
        Function<TlsEnrichStoredData, String> sslSubject = e -> getOrNull(e.getSslSubject(), SslSubject::getName);
        Function<TlsEnrichStoredData, String> ja3 = e -> getOrNull(e.getJa3(), Ja3::getName);
        Function<TlsEnrichStoredData, String> ja3s = TlsEnrichStoredData::getJa3s;
        Function<TlsEnrichStoredData, List<String>> sslCas = e -> Optional.ofNullable(e.getSslCas()).orElse(Lists.emptyList());
        Function<TlsEnrichStoredData, String> eventId = TlsEnrichStoredData::getEventId;


        private SoftAssertions softly = new SoftAssertions();
        private List<TlsEnrichStoredData> enriched = Lists.newArrayList();
        private List<TlsEnrichStoredData> notEnriched = Lists.newArrayList();
        private Function<TlsEnrichStoredData, String> toString = e -> "[eventId=" + eventId.apply(e)
                + ", sslSubject=" + sslSubject.apply(e) + ", ja3=" + ja3.apply(e) + ", ja3s=" + ja3s.apply(e)
                + ", sslCas={" + String.join(",", sslCas.apply(e)) + "} ]";


        void assertEnrichmentFieldsMatchExpected(List<TlsEnrichStoredData> actual, SessionSplitEnrichmentData.TestDataParameters expected) {
            enriched.addAll(actual);
            softly.assertThat(actual).extracting(sslSubject).as("SslSubject").isNotNull().containsOnly(expected.sslSubject);
            softly.assertThat(actual).extracting(ja3).as("Ja3").isNotNull().containsOnly(expected.ja3);
            softly.assertThat(actual).extracting(ja3s).as("Ja3s").isNotNull().containsOnly(expected.ja3s);
            softly.assertThat(actual).flatExtracting(sslCas).as("SslCas").isNotNull();
        }

        void assertEnrichmentFieldsAreNull(List<TlsEnrichStoredData> actual) {
            notEnriched.addAll(actual);
            softly.assertThat(actual).extracting(sslSubject).as("SslSubject").containsOnlyNulls();
            softly.assertThat(actual).extracting(ja3).as("Ja3").containsOnlyNulls();
            softly.assertThat(actual).extracting(ja3s).as("Ja3s").containsOnlyNulls();
            softly.assertThat(actual).flatExtracting(sslCas).as("SslCas").isNullOrEmpty();
        }

        void assertAll() {
            if (! softly.wasSuccess()) {
                LOGGER.error("Session split events expected to be enriched:");
                LOGGER.error(enriched.stream().map(toString).collect(Collectors.joining("\n")));
                LOGGER.error("Session split events expected to NOT be enriched:");
                LOGGER.error(notEnriched.stream().map(toString).collect(Collectors.joining("\n")));
            }

            softly.assertAll();
        }
    }
}
