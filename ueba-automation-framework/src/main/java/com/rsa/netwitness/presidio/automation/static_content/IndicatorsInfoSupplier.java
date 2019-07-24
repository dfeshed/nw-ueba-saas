package com.rsa.netwitness.presidio.automation.static_content;

import fortscale.common.general.Schema;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;

public class IndicatorsInfoSupplier {
    private static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger)
            LoggerFactory.getLogger(IndicatorsInfoSupplier.class.getName());

    private static final String RESOURCE_NAME = "supporting_information_config.yml";
    private final List<Map<String, Object>> indicators;
    private final Map<String, Integer> classificationPriorityAsc;

    private static UnaryOperator<List<String>> toDesc = listAsc -> {
        Collections.reverse(listAsc);
        return listAsc;
    };

    private static Function<Map<String, Object>, Schema> asSchema = map -> Schema.valueOf(map.get("schema").toString());



    public static Supplier<Map<String, Schema>> indicatorToSchema = () -> getInstance()
            .indicators.stream()
            .collect(toMap(e -> e.get("name").toString(), e -> asSchema.apply(e)));

    public static Supplier<Map<String, String>> indicatorToClassification = () -> getInstance()
            .indicators.stream()
            .collect(toMap(e -> e.get("name").toString(), e -> e.get("classification").toString()));

    public static Supplier<List<String>> classificationsByPrioritiesAsc = () -> getInstance()
            .classificationPriorityAsc.entrySet()
            .stream()
            .map(e -> e.getKey())
            .collect(Collectors.toList());

    public static Supplier<List<String>> classificationsByPrioritiesDesc = () -> toDesc.apply(classificationsByPrioritiesAsc.get());




    private IndicatorsInfoSupplier() {
        Yaml yaml = new Yaml();
        LOGGER.debug("Going to load resource: " + RESOURCE_NAME);
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream(RESOURCE_NAME);

        Map<String, List<Map<String, Object>>> obj = yaml.load(inputStream);
        indicators = obj.get("indicators");

        Map<String, Integer> classifications = obj.get("classificationPriority").stream()
                .collect(toMap(e -> e.get("classificationName").toString(), e -> (int) e.get("priority")));

        classificationPriorityAsc = classifications.entrySet().stream()
                .sorted(comparingByValue())
                .collect(toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2,
                                LinkedHashMap::new));

        LOGGER.trace("indicators: " + indicators);
        LOGGER.trace("classificationPriority: " + classificationPriorityAsc);
    }

    private static class SingletonHelper {
        private static final IndicatorsInfoSupplier INSTANCE = new IndicatorsInfoSupplier();
    }

    private static IndicatorsInfoSupplier getInstance() {
        return SingletonHelper.INSTANCE;
    }
}
