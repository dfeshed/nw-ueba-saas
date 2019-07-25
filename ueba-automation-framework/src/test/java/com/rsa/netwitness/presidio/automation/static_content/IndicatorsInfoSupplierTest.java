package com.rsa.netwitness.presidio.automation.static_content;

import fortscale.common.general.Schema;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class IndicatorsInfoSupplierTest {

    @Test
    public void indicatorToClassificationTest() {
        Map<String, String> result = IndicatorsInfoSupplier.indicatorToClassification.get();
        assertThat(result).isNotNull().isNotEmpty();
    }

    @Test
    public void indicatorToSchemaTest() {
        Map<String, Schema> result = IndicatorsInfoSupplier.indicatorToSchema.get();
        assertThat(result).isNotNull().isNotEmpty();
    }

    @Test
    public void indicatorToFeatureNamesTest() {
        Map<String, String> result = IndicatorsInfoSupplier.indicatorToFeatureNames.get();
        assertThat(result).isNotNull().isNotEmpty();
    }

    @Test
    public void classificationsByPrioritiesDescTest() {
        List<String> resultDesc = IndicatorsInfoSupplier.classificationsByPrioritiesDesc.get();
        List<String> resultAsc = IndicatorsInfoSupplier.classificationsByPrioritiesAsc.get();
        assertThat(resultDesc).isNotNull().isNotEmpty();

        Collections.reverse(resultAsc);
        assertThat(resultDesc)
                .hasSameSizeAs(resultAsc)
                .hasSameElementsAs(resultAsc)
                .containsExactlyElementsOf(resultAsc);
    }
}