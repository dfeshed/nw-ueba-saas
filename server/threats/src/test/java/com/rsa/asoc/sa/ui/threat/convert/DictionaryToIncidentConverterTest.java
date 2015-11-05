package com.rsa.asoc.sa.ui.threat.convert;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.rsa.asoc.sa.ui.common.protobuf.ProtocolBufferUtils;
import com.rsa.asoc.sa.ui.threat.domain.bean.Incident;
import com.rsa.asoc.sa.ui.threat.domain.bean.IncidentStatus;
import com.rsa.asoc.sa.ui.threat.domain.bean.InvestigationMilestone;
import com.rsa.asoc.sa.ui.threat.domain.bean.Priority;
import com.rsa.netwitness.carlos.plist.PropertyListProtocol;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.junit.Test;
import org.springframework.core.convert.converter.Converter;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang.RandomStringUtils.randomAlphanumeric;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link DictionaryToIncidentConverter}
 *
 * @author Abram Thielke
 * @author Jay Garala
 * @since 10.6.0.0
 */
public class DictionaryToIncidentConverterTest {

    private final Converter<PropertyListProtocol.Dictionary, Incident> converter = new DictionaryToIncidentConverter();

    @Test
    public void testConvert() {
        final String id = "INC-10018";
        final String name = "INC-4";
        String summary = "Bad stuff happened";
        Priority priority = Priority.MEDIUM;
        final Integer prioritySort = 1;
        final int alertCount = 1;
        final int averageAlertRiskScore = 50;
        int riskScore = 40;
        List<Map<String, Object>> categories = Arrays.asList(buildCategory("Environmental", "Deterioration"),
                buildCategory("Hacking", "Abuse of functionality"));
        String createdBy = "Administrator";
        Date created = new Date(Instant.parse("2015-06-03T11:49:16.841Z").toEpochMilli());
        Date lastUpdated = new Date(Instant.parse("2015-06-03T11:49:16.841Z").toEpochMilli());
        Map<String, Object> lastUpdatedByUser = buildPerson("2", "Bob Foo", "bfoo", "bfoo@rsa.com");
        Set<String> sources = ImmutableSet.<String>builder().add("Security Analytics Investigator").build();
        final Map<String, Object> assignee = buildPerson(1L, "Admin", "admin", "admin@rsa.com");
        IncidentStatus status = IncidentStatus.ASSIGNED;
        final Integer statusSort = 1;
        List<Map<String, Object>> notes = ImmutableList.<Map<String, Object>>builder().add(buildRandomNote()).build();

        long totalRemediationTaskCount = 1;
        long openRemediationTaskCount = 1;
        boolean hasRemediationTasks = true;
        boolean sealed = true;
        String ruleId = "RULE-ABC";
        Date firstAlertTime = new Date();

        Map<String, Object> map =
                ImmutableMap.<String, Object>builder().put("id", id).put("name", name).put("summary", summary)
                        .put("priority", priority.name()).put("alertCount", alertCount)
                        .put("averageAlertRiskScore", averageAlertRiskScore).put("riskScore", riskScore)
                        .put("createdBy", createdBy).put("created", created).put("lastUpdated", lastUpdated)
                        .put("lastUpdatedByUser", lastUpdatedByUser).put("sources", sources).put("assignee", assignee)
                        .put("status", status.name()).put("categories", categories).put("notes", notes)
                        .put("totalRemediationTaskCount", totalRemediationTaskCount)
                        .put("openRemediationTaskCount", openRemediationTaskCount)
                        .put("hasRemediationTasks", hasRemediationTasks).put("sealed", sealed).put("ruleId", ruleId)
                        .put("firstAlertTime", firstAlertTime).build();

        PropertyListProtocol.Dictionary dictionary = ProtocolBufferUtils.createDictionaryFromMap(map);
        Incident incident = converter.convert(dictionary);

        assertNotNull(incident);
        assertEquals(id, incident.getId());
        assertEquals(name, incident.getName());
        assertEquals(summary, incident.getSummary());
        assertEquals(priority, incident.getPriority());
        assertEquals(prioritySort, incident.getPrioritySort());
        assertEquals(alertCount, incident.getAlertCount());
        assertEquals(averageAlertRiskScore, incident.getAverageAlertRiskScore());
        assertEquals(riskScore, incident.getRiskScore());
        assertTrue(compareList(categories, incident.getCategories()));
        assertEquals(createdBy, incident.getCreatedBy());
        assertEquals(created.getTime(), incident.getCreated().toEpochMilli());
        assertEquals(lastUpdated.getTime(), incident.getLastUpdated().toEpochMilli());
        assertTrue(compareMapObject(lastUpdatedByUser, incident.getLastUpdatedByUser()));
        assertEquals(sources, incident.getSources());
        assertTrue(compareMapObject(assignee, incident.getAssignee()));
        assertEquals(status, incident.getStatus());
        assertEquals(statusSort, incident.getStatusSort());
        assertTrue(compareList(notes, incident.getNotes()));
        assertEquals(totalRemediationTaskCount, incident.getTotalRemediationTaskCount());
        assertEquals(openRemediationTaskCount, incident.getOpenRemediationTaskCount());
        assertEquals(hasRemediationTasks, incident.isHasRemediationTasks());
        assertEquals(sealed, incident.isSealed());
        assertEquals(ruleId, incident.getRuleId());
        assertEquals(firstAlertTime.getTime(), incident.getFirstAlertTime().toEpochMilli());
    }

    private boolean compareList(List<Map<String, Object>> objects, List<?> objz) {
        boolean foundAll = true;

        if (objects == null && objz == null) {
            return true;
        }
        if (objects == null) {
            return false;
        }
        if (objz == null) {
            return false;
        }
        if (objects.size() != objz.size()) {
            return false;
        }

        for (Map<String, Object> object : objects) {
            boolean found = false;
            for (Object obj : objz) {
                found = found || compareMapObject(object, obj);
            }
            foundAll = foundAll && found;
        }
        return foundAll;
    }

    private boolean compareMapObject(Map<String, Object> map, Object object) {

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            try {

                Object objVal = FieldUtils.readField(object, entry.getKey(), true);
                if (value instanceof Date && objVal instanceof Instant) {
                    if (!ObjectUtils.equals(((Date) value).getTime(), ((Instant) objVal).toEpochMilli())) {
                        return false;
                    }
                } else if (objVal.getClass().isEnum()) {
                    if (!ObjectUtils.equals(value, ((Enum) objVal).name())) {
                        return false;
                    }
                } else if (objVal instanceof Number && value instanceof String) {
                    return ((Number) objVal).doubleValue() == Long.valueOf((String) value).doubleValue();
                } else if (!ObjectUtils.equals(value, objVal)) {
                    return false;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public ImmutableMap<String, Object> buildPerson(Object id, String name, String login, String emailAddress) {
        ImmutableMap.Builder<String, Object> builder =
                ImmutableMap.<String, Object>builder().put("id", id).put("name", name).put("login", login);
        if (emailAddress != null) {
            builder.put("emailAddress", emailAddress);
        }

        return builder.build();
    }

    public ImmutableMap<String, Object> buildCategory(String parent, String name) {
        return ImmutableMap.<String, Object>builder().put("parent", parent).put("name", name).build();
    }

    public ImmutableMap<String, Object> buildRandomNote() {
        return ImmutableMap.<String, Object>builder().put("id", randomAlphanumeric(5))
                .put("author", randomAlphabetic(10)).put("notes", randomAlphanumeric(30)).put("created", new Date())
                .put("lastUpdated", new Date()).put("milestone", InvestigationMilestone.CONTAINMENT.name())
                .put("hasAttachment", true).put("attachmentFilename", randomAlphanumeric(15))
                .put("attachmentContentType", "text/html").build();
    }
}
