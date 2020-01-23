package com.rsa.netwitness.presidio.automation.converter.producers;

import ch.qos.logback.classic.Logger;
import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import com.rsa.netwitness.presidio.automation.converter.formatters.EventFormatter;
import com.rsa.netwitness.presidio.automation.s3.S3_Chunk;
import fortscale.common.general.Schema;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.rsa.netwitness.presidio.automation.s3.S3_Chunk.toChunkInterval;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

public class S3JsonGzipChunksProducer implements EventsProducer<NetwitnessEvent> {
    private static Logger LOGGER = (Logger) LoggerFactory.getLogger(S3JsonGzipChunksProducer.class);

    private final EventFormatter<NetwitnessEvent, String> formatter;
    private long total = 0;
    private S3_Chunk previousChunk;
    private boolean IS_PARALLEL = false;

    S3JsonGzipChunksProducer(EventFormatter<NetwitnessEvent, String> formatter) {
        requireNonNull(formatter);
        this.formatter = formatter;
    }

    @Override
    public Map<Schema, Long> send(Stream<NetwitnessEvent> eventsList) {
        List<NetwitnessEvent> events = eventsList.parallel().collect(toList());
        List<Schema> schema = events.parallelStream().map(e -> e.schema).distinct().collect(toList());
        assertThat(schema).as("same schema").hasSize(1);

        Map<Instant, List<NetwitnessEvent>> eventsByInterval = events.parallelStream()
                .collect(groupingBy(event -> toChunkInterval.apply(event.eventTimeEpoch)));

        List<S3_Chunk> chunksSorted = eventsByInterval.keySet().parallelStream()
                .map(interval -> new S3_Chunk(interval, schema.get(0)))
                .sorted()
                .collect(toList());

        Stream<S3_Chunk> chunksToProcess = IS_PARALLEL ? chunksSorted.parallelStream() : chunksSorted.stream();
        chunksToProcess.forEach(chunk -> chunk.process(formatAll(eventsByInterval.get(chunk.getInterval()))));

        Stream<S3_Chunk> chunksToClose = IS_PARALLEL ?
                chunksSorted.subList(0, chunksSorted.size() - 2).parallelStream() : chunksSorted.subList(0, chunksSorted.size() - 2).stream();
        chunksToClose.forEach(S3_Chunk::close);

        previousChunk = chunksSorted.get(chunksSorted.size() - 1);

        return new HashMap<>();
    }

    @Override
    public void close() {
        previousChunk.close();
    }

    private List<String> formatAll(List<NetwitnessEvent> netwitnessEvents) {
        return netwitnessEvents.parallelStream().map(formatter::format).collect(toList());
    }

}
