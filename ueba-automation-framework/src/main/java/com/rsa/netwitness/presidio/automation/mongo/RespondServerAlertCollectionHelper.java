package com.rsa.netwitness.presidio.automation.mongo;

import ch.qos.logback.classic.Logger;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.gte;
import static com.rsa.netwitness.presidio.automation.utils.common.LambdaUtils.getOrNull;
import static java.time.temporal.ChronoUnit.DAYS;

public class RespondServerAlertCollectionHelper {
    private static Logger LOGGER = (Logger) LoggerFactory.getLogger(RespondServerAlertCollectionHelper.class);

    private MongoCollection<Document> collection;
    private Function<Document, RespondServerAlert> alertParser = doc -> {
        Instant receivedTime = getOrNull(doc.getDate("receivedTime"), Date::toInstant);
        Document originalAlert = doc.get("originalAlert", Document.class);
        Instant startDate = getOrNull(originalAlert.getString("startDate").replaceAll("\\+\\d+", "Z"), Instant::parse);
        Instant endDate = getOrNull(originalAlert.getString("endDate").replaceAll("\\+\\d+", "Z"), Instant::parse);
        String uebaIndicatorId = originalAlert.getString("id");
        String uebaAlertId = originalAlert.getString("alertId");
        return new RespondServerAlert(receivedTime, startDate, endDate, uebaIndicatorId, uebaAlertId);
    };

    public RespondServerAlertCollectionHelper() {
        MongoDatabase database = MongoClientEsaServer.getConnection().getDatabase("respond-server");
        collection = database.getCollection("alert");
    }

    public void truncateCollection() {
        LOGGER.warn("**************************************************");
        LOGGER.warn(" !!! Going to DROP respond-server alerts table.");
        LOGGER.warn("**************************************************");
        collection.drop();
    }

    public List<RespondServerAlert> getRespondServerAlertsForLastWeek(Instant indicatorStartDateInclusive, Instant indicatorEndDateInclusive) {
        return getRespondServerAlerts(indicatorStartDateInclusive, indicatorEndDateInclusive, Instant.now().minus(7, DAYS));
    }
        // ESA Alert == UEBA indicator
    public List<RespondServerAlert> getRespondServerAlerts(Instant indicatorStartDateInclusive, Instant indicatorEndDateInclusive, Instant fromReceivedTimeInclusive) {
        Bson filters = gte("receivedTime", fromReceivedTimeInclusive);

        FindIterable<Document> documents = collection
                .find(filters)
                .projection(Projections.include("receivedTime", "originalAlert"))
                .batchSize(500);

        MongoCursor<Document> iterator = documents.iterator();
        List<RespondServerAlert> alerts = new ArrayList<>();

        Predicate<RespondServerAlert> alertFilter = alert -> alert.startDate.plusMillis(1).isAfter(indicatorStartDateInclusive)
                && alert.endDate.minusMillis(1).isBefore(indicatorEndDateInclusive);

        Consumer<Document> collector = doc -> {
            RespondServerAlert alert = alertParser.apply(doc);
            if (alertFilter.test(alert)) {
                alerts.add(alert);
            }
        };

        iterator.forEachRemaining(collector);
        return alerts.parallelStream().collect(Collectors.toList());
    }

    public class RespondServerAlert {
        public final Instant receivedTime;
        public final Instant startDate;
        public final Instant endDate;
        public final String uebaIndicatorId;
        public final String uebaAlertId;


        public RespondServerAlert(Instant receivedTime, Instant startDate, Instant endDate, String uebaIndicatorId, String uebaAlertId) {
            this.receivedTime = receivedTime;
            this.startDate = startDate;
            this.endDate = endDate;
            this.uebaIndicatorId = uebaIndicatorId;
            this.uebaAlertId = uebaAlertId;
        }
    }

}
