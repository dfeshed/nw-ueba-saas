package com.rsa.asoc.sa.ui.common.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Testing the {@link Request} object.
 *
 * @author Abram Thielke
 * @since 10.6.0.0
 */
public class RequestTest {

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Verify we can deserialize the JSON payload passed in from the frontend
     */
    @Test
    public void testDeserialization() throws Exception {
        String payload = "{\n" +
                "  \"page\": {\n" +
                "    \"index\": 1,\n" +
                "    \"size\": 10000\n" +
                "  },\n" +
                "  \"sort\": [\n" +
                "    { \"field\": \"created\", \"descending\": true },\n" +
                "    { \"field\": \"assigned\", \"descending\": false }\n" +
                "  ],\n" +
                "  \"filter\": [\n" +
                "    {\n" +
                "      \"field\": \"assigned\",\n" +
                "      \"value\": \"Ian\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"field\": \"status\",\n" +
                "      \"values\": [\"NEW\", \"OPEN\"]\n" +
                "    },\n" +
                "    {\n" +
                "      \"field\": \"number\",\n" +
                "      \"values\": [1, 2, 3]\n" +
                "    },\n" +
                "    {\n" +
                "      \"field\": \"nullable\",\n" +
                "      \"isNull\": true\n" +
                "    },\n" +
                "    {\n" +
                "      \"field\": \"created\",\n" +
                "      \"range\": {\n" +
                "        \"from\": 1234,\n" +
                "        \"to\": 9876\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        Request request = mapper.readValue(payload, Request.class);
        assertNotNull(request);

        // Verify pagination object
        assertTrue(request.hasPage());
        Request.Page page = request.getPage();
        assertNotNull(page);
        assertEquals(Integer.valueOf(1), page.getIndex());
        assertEquals(Integer.valueOf(10000), page.getSize());


        // Verify sorting
        assertTrue(request.hasSort());
        List<Request.Sort> sort = request.getSort();
        assertNotNull(sort);
        assertEquals(2, sort.size());

        Request.Sort createdSort = sort.get(0);
        assertEquals("created", createdSort.getField());
        assertTrue(createdSort.isDescending());

        Request.Sort assignedSort = sort.get(1);
        assertEquals("assigned", assignedSort.getField());
        assertFalse(assignedSort.isDescending());


        // Verify filtering
        assertTrue(request.hasFilter());
        List<Request.Filter> filter = request.getFilter();
        assertNotNull(filter);
        assertEquals(5, filter.size());

        Request.Filter valueFilter = filter.get(0);
        assertEquals("assigned", valueFilter.getField());
        assertEquals("Ian", valueFilter.getValue());

        Request.Filter valuesStringFilter = filter.get(1);
        assertEquals("status", valuesStringFilter.getField());
        List<Object> valuesString = valuesStringFilter.getValues();
        assertEquals(2, valuesString.size());
        assertEquals("NEW", valuesString.get(0));
        assertEquals("OPEN", valuesString.get(1));

        Request.Filter valuesNumberFilter = filter.get(2);
        assertEquals("number", valuesNumberFilter.getField());
        List<Object> valuesNumber = valuesNumberFilter.getValues();
        assertEquals(3, valuesNumber.size());
        assertEquals(1, valuesNumber.get(0));
        assertEquals(2, valuesNumber.get(1));
        assertEquals(3, valuesNumber.get(2));

        Request.Filter nullFilter = filter.get(3);
        assertEquals("nullable", nullFilter.getField());
        assertTrue(nullFilter.isNull());

        Request.Filter rangeFilter = filter.get(4);
        assertEquals("created", rangeFilter.getField());
        assertNotNull(rangeFilter.getRange());
        assertEquals(Long.valueOf(1234), rangeFilter.getRange().getFrom());
        assertEquals(Long.valueOf(9876), rangeFilter.getRange().getTo());
    }

    @Test
    public void testFluentApi() {
        Request request = Request.newBuilder()
                .withPage(Request.Page.newBuilder()
                        .withIndex(0)
                        .withSize(25))
                .withSort(Request.Sort.newBuilder()
                        .withField("created")
                        .withDescending(true))
                .withSort(Request.Sort.newBuilder()
                        .withField("assigned")
                        .withDescending(false))
                .withFilter(Request.Filter.newBuilder()
                        .withField("status")
                        .withValue("ASSIGNED"))
                .withFilter(Request.Filter.newBuilder()
                        .withField("updated")
                        .withRange(Request.Filter.Range.newBuilder()
                                .withFrom(1L)
                                .withTo(2L)))
                .build();

        assertNotNull(request);
    }
}
