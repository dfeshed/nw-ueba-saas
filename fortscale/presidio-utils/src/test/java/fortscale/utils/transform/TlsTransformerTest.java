package fortscale.utils.transform;

import com.fasterxml.jackson.core.JsonProcessingException;
import fortscale.utils.transform.predicate.JsonObjectRegexPredicate;
import fortscale.utils.transform.regexcaptureandformat.CaptureAndFormatConfiguration;
import fortscale.utils.transform.regexcaptureandformat.CapturingGroupConfiguration;
import fortscale.utils.transform.stringformat.StringFormat;
import fortscale.utils.transform.stringformat.StringFormatTransformer;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TlsTransformerTest extends TransformerTest{
    private static final String UDM_EVENT_SOURCE_ID_FIELD_NAME = "event_source_id";
    private static final String UDM_EVENT_TIME_FIELD_NAME = "event_time";
    private static final String UDM_SRC_IP_FIELD_NAME = "ip_src";
    private static final String UDM_DST_COUNTRY_FIELD_NAME = "country_dst";
    private static final String UDM_SSL_SUBJECT_FIELD_NAME = "ssl_subject";
    private static final String UDM_ALIAS_HOST_FIELD_NAME = "alias_host";
    private static final String UDM_DST_ORG_FIELD_NAME = "org_dst";
    private static final String UDM_DST_ASN_FIELD_NAME = "asn_dst";
    private static final String UDM_NUM_OF_BYTES_SENT_FIELD_NAME = "payload_req";
    private static final String UDM_NUM_OF_BYTES_RECEIVED_FIELD_NAME = "payload_res";
    private static final String UDM_NETNAME_FIELD_NAME = "netname";
    private static final String UDM_JA3_FIELD_NAME = "ja3";
    private static final String UDM_JA3S_FIELD_NAME = "ja3s";
    private static final String UDM_DIRECTION_FIELD_NAME = "direction";
    private static final String UDM_DST_PORT_FIELD_NAME = "tcp_dstport";

    private static final String EVENT_ID_FIELD_NAME = "eventId";
    private static final String DATA_SOURCE_FIELD_NAME = "dataSource";
    private static final String DATE_TIME_FIELD_NAME = "dateTime";
    private static final String SRC_IP_FIELD_NAME = "srcIp";
    private static final String DST_COUNTRY_FIELD_NAME = "dstCountry";
    private static final String SSL_SUBJECT_FIELD_NAME = "sslSubject";
    private static final String DOMAIN_FIELD_NAME = "domain";
    private static final String DST_ORG_FIELD_NAME = "dstOrg";
    private static final String DST_ASN_FIELD_NAME = "dstAsn";
    private static final String NUM_OF_BYTES_SENT_FIELD_NAME = "numOfBytesSent";
    private static final String NUM_OF_BYTES_RECEIVED_FIELD_NAME = "numOfBytesReceived";
    private static final String SRC_NETNAME_FIELD_NAME = "srcNetname";
    private static final String DST_NETNAME_FIELD_NAME = "dstNetname";
    private static final String JA3_FIELD_NAME = "ja3";
    private static final String JA3S_FIELD_NAME = "ja3s";
    private static final String DIRECTION_FIELD_NAME = "direction";
    private static final String DST_PORT_FIELD_NAME = "dstPort";

    private static final String OUTBOUND_DIRECTION = "OUTBOUND";


    private IJsonObjectTransformer buildTlsTransformer(){
        List<IJsonObjectTransformer> transformerChainList = new ArrayList<>();

        // Filtering in only events with direction=='OUTBOUND'
        JsonObjectRegexPredicate directionWhiteListPredicate = new JsonObjectRegexPredicate("direction-white-list", DIRECTION_FIELD_NAME, "outbound");
        FilterTransformer directionFilter = new FilterTransformer("direction-filter", directionWhiteListPredicate, true);
        transformerChainList.add(directionFilter);


        //Convert time field from EPOCH millis to EPOCH seconds
        EpochTimeToNanoRepresentationTransformer dateTimeMillisToSeconds =
                new EpochTimeToNanoRepresentationTransformer("date-time-millis-to-nano-representation", UDM_EVENT_TIME_FIELD_NAME, DATE_TIME_FIELD_NAME);
        transformerChainList.add(dateTimeMillisToSeconds);

        //rename event_source_id to eventId
        CopyValueTransformer ranameEventSourceIdToEventId =
                new CopyValueTransformer(
                        "rename-event-source-id-to-event-id",
                        UDM_EVENT_SOURCE_ID_FIELD_NAME,
                        true,
                        Collections.singletonList(EVENT_ID_FIELD_NAME));
        transformerChainList.add(ranameEventSourceIdToEventId);

        //change direction format to upper case
        StringFormatTransformer convertStringFormatOfDirection =
                new StringFormatTransformer(
                        "convert-string-format-of-direction",
                        UDM_DIRECTION_FIELD_NAME,
                        DIRECTION_FIELD_NAME,
                        StringFormat.LOWER_UNDERSCORE,
                        StringFormat.UPPER_UNDERSCORE);
        transformerChainList.add(convertStringFormatOfDirection);

        //rename ip.src
        CopyValueTransformer ranameIpSrc =
                new CopyValueTransformer(
                        "rename-ip-src",
                        UDM_SRC_IP_FIELD_NAME,
                        true,
                        Collections.singletonList(SRC_IP_FIELD_NAME));
        transformerChainList.add(ranameIpSrc);

        //rename country.dst
        CopyValueTransformer ranameCountryDst =
                new CopyValueTransformer(
                        "rename-country-dst",
                        UDM_DST_COUNTRY_FIELD_NAME,
                        true,
                        Collections.singletonList(DST_COUNTRY_FIELD_NAME));
        transformerChainList.add(ranameCountryDst);

        //rename ssl.subject
        CopyValueTransformer ranameSslSubject =
                new CopyValueTransformer(
                        "rename-ssl-subject",
                        UDM_SSL_SUBJECT_FIELD_NAME,
                        true,
                        Collections.singletonList(SSL_SUBJECT_FIELD_NAME));
        transformerChainList.add(ranameSslSubject);

        //extract from the first value of alias host the top level domain.
        TopLevelDomainTransformer aliasHostToTopLevelDomain =
                new TopLevelDomainTransformer(
                        "alias-host-to-top-level-domain",
                        UDM_ALIAS_HOST_FIELD_NAME+"[0]",
                        false,
                        DOMAIN_FIELD_NAME);
        transformerChainList.add(aliasHostToTopLevelDomain);

        //rename org.dst
        CopyValueTransformer ranameOrgDst =
                new CopyValueTransformer(
                        "rename-org-dst",
                        UDM_DST_ORG_FIELD_NAME,
                        true,
                        Collections.singletonList(DST_ORG_FIELD_NAME));
        transformerChainList.add(ranameOrgDst);

        //rename asn.dst
        CopyValueTransformer ranameAsnDst =
                new CopyValueTransformer(
                        "rename-asn-dst",
                        UDM_DST_ASN_FIELD_NAME,
                        true,
                        Collections.singletonList(DST_ASN_FIELD_NAME));
        transformerChainList.add(ranameAsnDst);

        //rename payload.req
        CopyValueTransformer ranamePayloadReq =
                new CopyValueTransformer(
                        "rename-payload-req",
                        UDM_NUM_OF_BYTES_SENT_FIELD_NAME,
                        true,
                        Collections.singletonList(NUM_OF_BYTES_SENT_FIELD_NAME));
        transformerChainList.add(ranamePayloadReq);

        //rename payload.res
        CopyValueTransformer ranamePayloadRes =
                new CopyValueTransformer(
                        "rename-payload-res",
                        UDM_NUM_OF_BYTES_RECEIVED_FIELD_NAME,
                        true,
                        Collections.singletonList(NUM_OF_BYTES_RECEIVED_FIELD_NAME));
        transformerChainList.add(ranamePayloadRes);

        //extract src netname
        CaptureAndFormatConfiguration srcNetnameConfiguration = new CaptureAndFormatConfiguration("(.+) src", "%s",
                Collections.singletonList(new CapturingGroupConfiguration(1, "LOWER")));
        FirstArrayValueRegexCaptorAndFormatter extractSrcNetname =
                new FirstArrayValueRegexCaptorAndFormatter(
                        "extract-src-netname",
                        UDM_NETNAME_FIELD_NAME,
                        SRC_NETNAME_FIELD_NAME,
                        srcNetnameConfiguration);
        transformerChainList.add(extractSrcNetname);

        //extract dst netname
        CaptureAndFormatConfiguration dstNetnameConfiguration = new CaptureAndFormatConfiguration("(.+) dst", "%s",
                Collections.singletonList(new CapturingGroupConfiguration(1, "LOWER")));
        FirstArrayValueRegexCaptorAndFormatter extractDstNetname =
                new FirstArrayValueRegexCaptorAndFormatter(
                        "extract-dst-netname",
                        UDM_NETNAME_FIELD_NAME,
                        DST_NETNAME_FIELD_NAME,
                        dstNetnameConfiguration);
        transformerChainList.add(extractDstNetname);

        //tcp.dstport
        CopyValueTransformer ranameTcpDstPort =
                new CopyValueTransformer(
                        "rename-tcp-dstport",
                        UDM_DST_PORT_FIELD_NAME,
                        true,
                        Collections.singletonList(DST_PORT_FIELD_NAME));
        transformerChainList.add(ranameTcpDstPort);


        //The File Windows Audit Transformer that chain all the transformers together.
        JsonObjectChainTransformer tlsTransformer =
                new JsonObjectChainTransformer("tls-transformer", transformerChainList);

        return tlsTransformer;
    }

    @Test
    public void deserialize_auth_transformer_test() throws JsonProcessingException {
        IJsonObjectTransformer transformer = buildTlsTransformer();

        String transformerJsonAsString = mapper.writeValueAsString(transformer);

        Assert.assertNotNull(transformerJsonAsString);
    }
}
