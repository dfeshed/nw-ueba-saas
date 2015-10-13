package com.rsa.asoc.sa.ui.threat.convert;

import com.rsa.asoc.sa.ui.threat.domain.bean.Category;
import com.rsa.asoc.sa.ui.threat.domain.bean.Incident;
import com.rsa.asoc.sa.ui.threat.domain.bean.IncidentStatus;
import com.rsa.asoc.sa.ui.threat.domain.bean.InvestigationMilestone;
import com.rsa.asoc.sa.ui.threat.domain.bean.JournalEntry;
import com.rsa.asoc.sa.ui.threat.domain.bean.Person;
import com.rsa.asoc.sa.ui.threat.domain.bean.Priority;
import com.rsa.netwitness.carlos.plist.PropertyListProtocol;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.convert.converter.Converter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.rsa.asoc.sa.ui.common.protobuf.ProtocolBufferUtils.consumeEnumFromMap;
import static com.rsa.asoc.sa.ui.common.protobuf.ProtocolBufferUtils.consumeInstantFromMap;
import static com.rsa.asoc.sa.ui.common.protobuf.ProtocolBufferUtils.consumeValueFromMap;
import static com.rsa.asoc.sa.ui.common.protobuf.ProtocolBufferUtils.createMapFromDictionary;

/**
 * Converts a {@link com.rsa.netwitness.carlos.plist.PropertyListProtocol.Dictionary} to an
 * {@link Incident}.  This is mostly used in Protobuf-specific repositories.
 *
 * @author Abram Thielke
 * @since 10.6.0.0
 */
public class DictionaryToIncidentConverter implements Converter<PropertyListProtocol.Dictionary, Incident> {

    @Override
    public Incident convert(PropertyListProtocol.Dictionary source) {
        Map<String, Object> values = createMapFromDictionary(source);

        Incident incident = new Incident();
        consumeValueFromMap(values, "id", String.class, incident::setId);
        consumeValueFromMap(values, "name", String.class, incident::setName);
        consumeValueFromMap(values, "summary", String.class, incident::setSummary);
        consumeEnumFromMap(values, "priority", Priority.class, incident::setPriority);
        consumeValueFromMap(values, "prioritySort", Integer.class, incident::setPrioritySort);
        consumeValueFromMap(values, "alertCount", Integer.class, incident::setAlertCount);
        consumeValueFromMap(values, "averageAlertRiskScore", Integer.class, incident::setAverageAlertRiskScore);
        consumeValueFromMap(values, "riskScore", Integer.class, incident::setRiskScore);
        consumeValueFromMap(values, "createdBy", String.class, incident::setCreatedBy);
        consumeInstantFromMap(values, "created", incident::setCreated);
        consumeInstantFromMap(values, "lastUpdated", incident::setLastUpdated);
        if (values.containsKey("lastUpdatedByUser")) {
            Map<String, Object> lastUpdatedByUser = (Map<String, Object>) values.get("lastUpdatedByUser");
            if ( lastUpdatedByUser != null && !lastUpdatedByUser.isEmpty()) {
                Person person = new Person( Long.valueOf((String) lastUpdatedByUser.get("id")),
                        (String) lastUpdatedByUser.get("name"),
                        (String) lastUpdatedByUser.get("login"),
                        (String) lastUpdatedByUser.get("emailAddress"));
                incident.setLastUpdatedByUser(person);
            }
        } else if (values.containsKey("lastUpdatedByUserName")) {
            String name = (String) values.get("lastUpdatedByUserName");
            if (StringUtils.isNotBlank(name)) {
                Person person = new Person( null, name, null, null);
                incident.setLastUpdatedByUser(person);
            }
        }

        if (values.containsKey("assignee")) {
            Map<String, Object> assignee = (Map<String, Object>) values.get("assignee");
            if ( assignee != null && !assignee.isEmpty()) {
                Person person = new Person(Long.valueOf((String)assignee.get("id")),
                        (String) assignee.get("name"),
                        (String) assignee.get("login"),
                        (String) assignee.get("emailAddress"));
                incident.setAssignee(person);
            }
        }

        consumeEnumFromMap(values, "status", IncidentStatus.class, incident::setStatus);
        consumeValueFromMap(values, "statusSort", Integer.class, incident::setStatusSort);

        if (values.containsKey("categories")) {
            List<Map<String, String>> categories = (List<Map<String, String>>) values.get("categories");
            incident.setCategories(convertMapToCategoryList(categories));
        }

        if (values.containsKey("notes")) {
            List<Map<String, Object>> notes = (List<Map<String, Object>>) values.get("notes");
            incident.setNotes(convertMapToJournalEntryList(notes));
        }

        if (values.containsKey("sources")) {
            List<String> sources = (List<String>) values.get("sources");
            incident.setSources(new HashSet<>(sources));
        }


        consumeValueFromMap(values, "totalRemediationTaskCount", Long.class, incident::setTotalRemediationTaskCount);
        consumeValueFromMap(values, "openRemediationTaskCount", Long.class, incident::setOpenRemediationTaskCount);
        consumeValueFromMap(values, "hasRemediationTasks", Boolean.class, incident::setHasRemediationTasks);
        consumeValueFromMap(values, "sealed", Boolean.class, incident::setSealed);
        consumeValueFromMap(values, "ruleId", String.class, incident::setRuleId);
        consumeInstantFromMap(values, "firstAlertTime", incident::setFirstAlertTime);

        setStatusSortIfMissing(incident);
        setPrioritySortIfMissing(incident);

        return incident;
    }

    private void setStatusSortIfMissing(Incident incident) {
        if ( incident.getStatusSort() == null ) {
            incident.setStatusSort(Integer.valueOf(incident.getStatus().ordinal()));
        }
    }

    private void setPrioritySortIfMissing(Incident incident) {
        if ( incident.getPrioritySort() == null ) {
            incident.setPrioritySort(Integer.valueOf(incident.getPriority().ordinal()));
        }
    }

    private List<Category> convertMapToCategoryList(List<Map<String, String>> categories) {
        List<Category> categoryList = new ArrayList<>();
        for (Map<String, String> category : categories) {
            Category cat = new Category(category.get("parent"), category.get("name"));
            categoryList.add(cat);
        }
        return categoryList;
    }

    private List<JournalEntry> convertMapToJournalEntryList(List<Map<String, Object>> journalEntries) {
        List<JournalEntry> jes = new ArrayList<>();
        for (Map<String, Object> journalEntry : journalEntries) {
            JournalEntry je = new JournalEntry();
            consumeValueFromMap(journalEntry, "id", String.class, je::setId);
            consumeValueFromMap(journalEntry, "author", String.class, je::setAuthor);
            consumeValueFromMap(journalEntry, "notes", String.class, je::setNotes);
            consumeInstantFromMap(journalEntry, "created", je::setCreated);
            consumeInstantFromMap(journalEntry, "lastUpdated", je::setLastUpdated);
            consumeEnumFromMap(journalEntry, "milestone", InvestigationMilestone.class, je::setMilestone);
            consumeValueFromMap(journalEntry, "hasAttachment", Boolean.class, je::setHasAttachment);
            consumeValueFromMap(journalEntry, "attachmentFilename", String.class, je::setAttachmentFilename);
            consumeValueFromMap(journalEntry, "attachmentContentType", String.class, je::setAttachmentContentType);

            jes.add(je);
        }

        return jes;
    }
}
