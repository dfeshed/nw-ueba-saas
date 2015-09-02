package com.rsa.asoc.sa.ui.common.protobuf;

import com.google.common.collect.Lists;
import com.rsa.asoc.sa.ui.common.data.Request;
import com.rsa.netwitness.carlos.repository.RepositoryProtocol;
import org.junit.Test;

import java.net.URI;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for {@link RepositoryProtocolUtils}
 *
 * @author Abram Thielke
 * @since 10.6.0.0
 */
public class RepositoryProtocolUtilsTest {

    @Test
    public void testBuildCriteriaWithNoFilters() {
        Request request = new Request();

        RepositoryProtocol.Criteria criteria = RepositoryProtocolUtils.buildCriteria(request);

        assertNotNull(criteria);
        assertEquals(0, criteria.getExpressionCount());
    }

    @Test
    public void testBuildCriteriaWithNullFilter() {
        final String field = "nullable";
        Request.Filter filter = new Request.Filter();
        filter.setField(field);
        filter.setNull(true);

        Request request = createWebSocketRequest(filter);

        RepositoryProtocol.Criteria criteria = RepositoryProtocolUtils.buildCriteria(request);

        assertNotNull(criteria);
        assertEquals(1, criteria.getExpressionCount());

        RepositoryProtocol.Expression expression = criteria.getExpression(0);
        assertEquals(field, expression.getPropertyName());
        assertEquals(RepositoryProtocol.Expression.RestrictionType.IS_NULL, expression.getRestrictionType());
    }

    @Test
    public void testBuildCriteriaWithSingleValueStringFilter() {
        final String field = "string";
        final String value = "value";
        Request.Filter filter = new Request.Filter();
        filter.setField(field);
        filter.setValue(value);

        Request request = createWebSocketRequest(filter);

        RepositoryProtocol.Criteria criteria = RepositoryProtocolUtils.buildCriteria(request);

        assertNotNull(criteria);
        assertEquals(1, criteria.getExpressionCount());

        RepositoryProtocol.Expression expression = criteria.getExpression(0);
        assertEquals(field, expression.getPropertyName());
        assertEquals(RepositoryProtocol.Expression.RestrictionType.EQUAL, expression.getRestrictionType());
        assertEquals(1, expression.getPropertyValueCount());

        RepositoryProtocol.PropertyValue propertyValue = expression.getPropertyValue(0);
        assertTrue(propertyValue.hasStringValue());
        assertEquals(value, propertyValue.getStringValue());
    }

    @Test
    public void testBuildCriteriaWithSingleValueNumberFilter() {
        final String field = "number";
        final Integer value = 1234;
        Request.Filter filter = new Request.Filter();
        filter.setField(field);
        filter.setValue(value);

        Request request = createWebSocketRequest(filter);

        RepositoryProtocol.Criteria criteria = RepositoryProtocolUtils.buildCriteria(request);

        assertNotNull(criteria);
        assertEquals(1, criteria.getExpressionCount());

        RepositoryProtocol.Expression expression = criteria.getExpression(0);
        assertEquals(field, expression.getPropertyName());
        assertEquals(RepositoryProtocol.Expression.RestrictionType.EQUAL, expression.getRestrictionType());
        assertEquals(1, expression.getPropertyValueCount());

        RepositoryProtocol.PropertyValue propertyValue = expression.getPropertyValue(0);
        assertTrue(propertyValue.hasIntValue());
        assertEquals(value.intValue(), propertyValue.getIntValue());
    }

    @Test
    public void testBuildCriteriaWithListValuesStringFilter() {
        final String field = "multiple";
        final List<Object> values = Lists.newArrayList("A", "B", "C", "D");
        Request.Filter filter = new Request.Filter();
        filter.setField(field);
        filter.setValues(values);

        Request request = createWebSocketRequest(filter);

        RepositoryProtocol.Criteria criteria = RepositoryProtocolUtils.buildCriteria(request);

        assertNotNull(criteria);
        assertEquals(1, criteria.getExpressionCount());

        RepositoryProtocol.Expression expression = criteria.getExpression(0);
        assertEquals(field, expression.getPropertyName());
        assertEquals(RepositoryProtocol.Expression.RestrictionType.IN, expression.getRestrictionType());
        assertEquals(4, expression.getPropertyValueCount());
    }

    @Test
    public void testBuildCriteriaWithRangeBetweenFilter() {
        final long from = 1000L;
        final long to = 2000L;
        Request.Filter.Range range = new Request.Filter.Range();
        range.setFrom(from);
        range.setTo(to);

        final String field = "range";
        Request.Filter filter = new Request.Filter();
        filter.setField(field);
        filter.setRange(range);

        Request request = createWebSocketRequest(filter);

        RepositoryProtocol.Criteria criteria = RepositoryProtocolUtils.buildCriteria(request);

        assertNotNull(criteria);
        assertEquals(1, criteria.getExpressionCount());

        RepositoryProtocol.Expression expression = criteria.getExpression(0);
        assertEquals(field, expression.getPropertyName());
        assertEquals(RepositoryProtocol.Expression.RestrictionType.BETWEEN, expression.getRestrictionType());
        assertEquals(2, expression.getPropertyValueCount());

        RepositoryProtocol.PropertyValue fromValue = expression.getPropertyValue(0);
        assertTrue(fromValue.hasDateValue());
        assertEquals(from, fromValue.getDateValue());

        RepositoryProtocol.PropertyValue toValue = expression.getPropertyValue(1);
        assertTrue(toValue.hasDateValue());
        assertEquals(to, toValue.getDateValue());
    }

    @Test
    public void testBuildCriteriaWithRangeGreaterThanFilter() {
        final long from = 1000L;
        Request.Filter.Range range = new Request.Filter.Range();
        range.setFrom(from);

        final String field = "range";
        Request.Filter filter = new Request.Filter();
        filter.setField(field);
        filter.setRange(range);

        Request request = createWebSocketRequest(filter);

        RepositoryProtocol.Criteria criteria = RepositoryProtocolUtils.buildCriteria(request);

        assertNotNull(criteria);
        assertEquals(1, criteria.getExpressionCount());

        RepositoryProtocol.Expression expression = criteria.getExpression(0);
        assertEquals(field, expression.getPropertyName());
        assertEquals(RepositoryProtocol.Expression.RestrictionType.GREATER_THAN, expression.getRestrictionType());
        assertEquals(1, expression.getPropertyValueCount());

        RepositoryProtocol.PropertyValue fromValue = expression.getPropertyValue(0);
        assertTrue(fromValue.hasDateValue());
        assertEquals(from, fromValue.getDateValue());
    }

    @Test
    public void testBuildCriteriaWithRangeLessThanFilter() {
        final long to = 2000L;
        Request.Filter.Range range = new Request.Filter.Range();
        range.setTo(to);

        final String field = "range";
        Request.Filter filter = new Request.Filter();
        filter.setField(field);
        filter.setRange(range);

        Request request = createWebSocketRequest(filter);

        RepositoryProtocol.Criteria criteria = RepositoryProtocolUtils.buildCriteria(request);

        assertNotNull(criteria);
        assertEquals(1, criteria.getExpressionCount());

        RepositoryProtocol.Expression expression = criteria.getExpression(0);
        assertEquals(field, expression.getPropertyName());
        assertEquals(RepositoryProtocol.Expression.RestrictionType.LESS_THAN, expression.getRestrictionType());
        assertEquals(1, expression.getPropertyValueCount());

        RepositoryProtocol.PropertyValue fromValue = expression.getPropertyValue(0);
        assertTrue(fromValue.hasDateValue());
        assertEquals(to, fromValue.getDateValue());
    }

    @Test
    public void testBuildCriteriaWithMultipleFilters() {
        Request request = createWebSocketRequest(
                createFilter("string"),
                createFilter(true),
                createFilter(1234),
                createFilter(9876L),
                createFilter(1.0F),
                createFilter(1.0),
                createFilter(new Date()),
                createFilter(URI.create("http://rsa.com")),
                createFilter(TestingEnum.NAME),
                createFilter(new TestingObject()));

        RepositoryProtocol.Criteria criteria = RepositoryProtocolUtils.buildCriteria(request);

        assertNotNull(criteria);
        assertEquals(10, criteria.getExpressionCount());
    }

    @Test
    public void testBuildSortingWithNoSort() {
        Request request = new Request();

        RepositoryProtocol.Sorting sorting = RepositoryProtocolUtils.buildSorting(request);

        assertNotNull(sorting);
        assertEquals(0, sorting.getSortOrderCount());
    }

    @Test
    public void testBuildSortingWithSingleDescendingField() {
        final String field = "fieldName";
        Request.Sort sort = new Request.Sort();
        sort.setField(field);
        sort.setDescending(true);

        Request request = new Request();
        request.setSort(Lists.newArrayList(sort));

        RepositoryProtocol.Sorting sorting = RepositoryProtocolUtils.buildSorting(request);

        assertNotNull(sorting);
        assertEquals(1, sorting.getSortOrderCount());

        RepositoryProtocol.SortOrder sortOrder = sorting.getSortOrder(0);
        assertEquals(field, sortOrder.getPropertyName());
        assertEquals(RepositoryProtocol.SortOrder.Direction.DESC, sortOrder.getDirection());
    }

    @Test
    public void testBuildSortingWithSingleAscendingField() {
        final String field = "fieldName";
        Request.Sort sort = new Request.Sort();
        sort.setField(field);
        sort.setDescending(false);

        Request request = new Request();
        request.setSort(Lists.newArrayList(sort));

        RepositoryProtocol.Sorting sorting = RepositoryProtocolUtils.buildSorting(request);

        assertNotNull(sorting);
        assertEquals(1, sorting.getSortOrderCount());

        RepositoryProtocol.SortOrder sortOrder = sorting.getSortOrder(0);
        assertEquals(field, sortOrder.getPropertyName());
        assertEquals(RepositoryProtocol.SortOrder.Direction.ASC, sortOrder.getDirection());
    }

    @Test
    public void testBuildPaginationWithNoPage() {
        Request request = new Request();

        RepositoryProtocol.Pagination pagination = RepositoryProtocolUtils.buildPagination(request);

        assertNotNull(pagination);
        assertFalse(pagination.hasPageNumber());
        assertFalse(pagination.hasPageSize());
    }

    @Test
    public void testBuildPagination() {
        final int number = 3;
        final int size = 25;
        Request.Page page = new Request.Page();
        page.setIndex(number);
        page.setSize(size);

        Request request = new Request();
        request.setPage(page);

        RepositoryProtocol.Pagination pagination = RepositoryProtocolUtils.buildPagination(request);

        assertNotNull(pagination);
        assertTrue(pagination.hasPageNumber());
        assertTrue(pagination.hasPageSize());
        assertEquals(number, pagination.getPageNumber());
        assertEquals(size, pagination.getPageSize());
    }

    private Request createWebSocketRequest(Request.Filter ... filters) {
        Request request = new Request();
        request.setFilter(Arrays.asList(filters));
        return request;
    }

    private Request.Filter createFilter(Object value) {
        Request.Filter filter = new Request.Filter();
        filter.setField("fieldName");
        filter.setValue(value);
        return filter;
    }

    private enum TestingEnum {
        NAME,
        EMAIL
    }

    private static class TestingObject {}
}
