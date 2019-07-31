package com.rsa.netwitness.presidio.automation.mapping.operation_type;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ActiveDirectoryOperationTypeMappingTest {

    @Test
    public void getOperationTypeToCategoryMapTest() {
        Map<String, List<String>> result = ActiveDirectoryOperationTypeMapping.getInstance().getOperationTypeToCategoryMap();
        assertThat(result).hasSizeGreaterThan(20);
    }

    @Test
    public void getOperationTypeToEventCodeMapTest() {
        Map<String, Integer> result = ActiveDirectoryOperationTypeMapping.getInstance().getOperationTypeToEventCodeMap();
        assertThat(result).hasSizeGreaterThan(20);
    }
}