package org.flume.sink.mongo;

import com.mongodb.DBObject;
import fortscale.domain.core.AbstractDocument;
import org.flume.sink.mongo.persistency.SinkMongoRepository;
import org.flume.utils.CountersUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class CountingPresidioMongoSinkTest {

    private CountingPresidioMongoSink testSubject;

    @Before
    public void setUp() throws Exception {
        final SinkMongoRepository mockedRepo = Mockito.mock(SinkMongoRepository.class);
        final CountersUtil mockedCountersUtil = Mockito.mock(CountersUtil.class);
        Mockito.when(mockedRepo.bulkSave(Mockito.anyListOf(DBObject.class), Mockito.anyString())).then(new Answer<Integer>() {
            @Override
            @SuppressWarnings("unchecked")
            public Integer answer(InvocationOnMock invocationOnMock) throws Throwable {
                final List<DBObject> events = (List<DBObject>) invocationOnMock.getArguments()[0];
                return events.size();
            }
        });
        testSubject = new CountingPresidioMongoSink<AbstractEventTestClass>(mockedRepo, mockedCountersUtil) {
            @Override
            protected String getEventSchemaName(AbstractEventTestClass event) {
                return event.eventSchemaName;
            }

            @Override
            protected Instant getEventTimeForCounter(AbstractEventTestClass event) {
                return event.eventTimeForCounter;
            }
        };

        Mockito.when(mockedCountersUtil.addToSinkCounter(Mockito.any(Instant.class), Mockito.anyString(), Mockito.anyInt())).then(new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock invocationOnMock) throws Throwable {
                return (Integer) invocationOnMock.getArguments()[2];
            }
        });

    }

    @Test
    @SuppressWarnings("unchecked")
    public void saveEvents() throws Exception {
        final List<AbstractDocument> eventsToSave = new ArrayList<>();

        final Instant time1 = Instant.parse("2017-07-27T14:45:01Z");
        final Instant endOfTime1 = Instant.parse("2017-07-27T15:00:00Z");

        final Instant time2 = Instant.parse("2017-07-27T17:22:16Z");
        final Instant endOfTime2 = Instant.parse("2017-07-27T18:00:00Z");

        final ADEventTestClass activeDirectoryRawEvent1 = new ADEventTestClass(time1);
        final ADEventTestClass activeDirectoryRawEvent2 = new ADEventTestClass(time1.plus(14, ChronoUnit.MINUTES));
        final ADEventTestClass activeDirectoryRawEvent3 = new ADEventTestClass(time2);

        final AuthenticationEventTestClass authenticationRawEvent1 = new AuthenticationEventTestClass(time1);
        final AuthenticationEventTestClass authenticationRawEvent2 = new AuthenticationEventTestClass(time2);

        final FileEventTestClass fileRawEvent1 = new FileEventTestClass(endOfTime1);

        eventsToSave.add(activeDirectoryRawEvent1);
        eventsToSave.add(activeDirectoryRawEvent2);
        eventsToSave.add(activeDirectoryRawEvent3);

        eventsToSave.add(authenticationRawEvent1);
        eventsToSave.add(authenticationRawEvent2);

        eventsToSave.add(fileRawEvent1);


        final int numOfSavedEvents = testSubject.saveEvents(eventsToSave);

        Assert.assertEquals(eventsToSave.size(), numOfSavedEvents);

        Mockito.verify(testSubject.countersUtil).addToSinkCounter(endOfTime1, "ad", 2);
        Mockito.verify(testSubject.countersUtil).addToSinkCounter(endOfTime2, "ad", 1);

        Mockito.verify(testSubject.countersUtil).addToSinkCounter(endOfTime1, "authentication", 1);
        Mockito.verify(testSubject.countersUtil).addToSinkCounter(endOfTime2, "authentication", 1);

        Mockito.verify(testSubject.countersUtil).addToSinkCounter(endOfTime1, "file", 1);

    }

    private static class AbstractEventTestClass extends AbstractDocument {
        protected String eventSchemaName;
        protected Instant eventTimeForCounter;
    }

    private static class ADEventTestClass extends AbstractEventTestClass {

        public ADEventTestClass(Instant eventTimeForCounter) {
            eventSchemaName = "ad";
            this.eventTimeForCounter = eventTimeForCounter;
        }
    }

    private static class AuthenticationEventTestClass extends AbstractEventTestClass {
        public AuthenticationEventTestClass(Instant eventTimeForCounter) {
            eventSchemaName = "authentication";
            this.eventTimeForCounter = eventTimeForCounter;
        }

    }

    private static class FileEventTestClass extends AbstractEventTestClass {

        public FileEventTestClass(Instant eventTimeForCounter) {
            eventSchemaName = "file";
            this.eventTimeForCounter = eventTimeForCounter;
        }
    }
}