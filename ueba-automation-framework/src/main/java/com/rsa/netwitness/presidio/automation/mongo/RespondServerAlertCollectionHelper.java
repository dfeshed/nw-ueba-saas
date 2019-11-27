package com.rsa.netwitness.presidio.automation.mongo;

import ch.qos.logback.classic.Logger;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
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

import static com.mongodb.client.model.Filters.*;
import static com.rsa.netwitness.presidio.automation.utils.common.LambdaUtils.getOrNull;

public class RespondServerAlertCollectionHelper {
    private static Logger LOGGER = (Logger) LoggerFactory.getLogger(RespondServerAlertCollectionHelper.class);

    private MongoCollection<Document> collection;

    public RespondServerAlertCollectionHelper() {
        MongoDatabase database = MongoClientEsaServer.getConnection().getDatabase("respond-server");
        collection = database.getCollection("alert");
    }

    public void truncateCollection() {
        LOGGER.warn("Going to drop respond-server alerts table.");
        collection.drop();
    }

    // ESA Alert == UEBA indicator
    public List<RespondServerAlert> getRespondServerAlerts(String schema, Instant fromIndicatorStartDate) {
        Bson filters = and(gte("receivedTime", fromIndicatorStartDate), eq("originalAlert.schema", schema), exists("originalAlert.id"));

        FindIterable<Document> documents = collection.find(filters)
                .projection(Projections.include("receivedTime", "originalAlert"));

        List<RespondServerAlert> alerts = new ArrayList<>();
        Consumer<Document> idCollector = alert -> alerts.add(alertConverter.apply(alert));
        documents.forEach(idCollector);
        return alerts;
    }

    private Function<Document, RespondServerAlert> alertConverter = doc -> {
        Instant receivedTime = getOrNull(doc.getDate("receivedTime"), Date::toInstant);
        Document originalAlert = doc.get("originalAlert", Document.class);

        Instant startDate = getOrNull(originalAlert.getString("startDate").replaceAll("\\+\\d+", "Z"), Instant::parse);
        Instant endDate = getOrNull(originalAlert.getString("endDate").replaceAll("\\+\\d+", "Z"), Instant::parse);

        String uebaIndicatorId = originalAlert.getString("id");
        String uebaAlertId = originalAlert.getString("alertId");
        return new RespondServerAlert(receivedTime, startDate, endDate, uebaIndicatorId, uebaAlertId);
    };

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
