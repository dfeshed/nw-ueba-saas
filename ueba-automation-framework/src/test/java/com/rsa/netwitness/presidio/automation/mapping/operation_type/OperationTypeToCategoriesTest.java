package com.rsa.netwitness.presidio.automation.mapping.operation_type;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class OperationTypeToCategoriesTest {

    @Test
    public void getForActiveDirectoryTest() {
        Map<String, List<String>> result = OperationTypeToCategories.getInstance().getForActiveDirectory();
        assertThat(result).hasSizeGreaterThan(10);
    }

    @Test
    public void getForFileTest() {
        Map<String, List<String>> result = OperationTypeToCategories.getInstance().getForFile();
        assertThat(result).hasSizeGreaterThan(10);
    }

}