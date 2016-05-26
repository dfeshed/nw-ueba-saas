package com.rsa.asoc.sa.ui.common.protobuf;

import com.google.common.base.Preconditions;
import com.rsa.asoc.sa.ui.common.data.Request;
import com.rsa.netwitness.carlos.repository.RepositoryProtocol;

import java.net.URI;
import java.util.Date;

/**
 * Utilities for building {@link RepositoryProtocol} messages.
 *
 * @author Kien Nguyen
 * @author Abram Thielke
 * @since 10.6.0.0
 */
public final class RepositoryProtocolUtils {

    private RepositoryProtocolUtils() {}

    /**
     * Builds a {@link com.rsa.netwitness.carlos.repository.RepositoryProtocol.Criteria} message from
     * the filters provided in the {@link Request}.
     *
     * @param request the request to extract the filters from
     * @return the criteria Protobuf message
     */
    public static RepositoryProtocol.Criteria buildCriteria(Request request) {
        RepositoryProtocol.Criteria.Builder criteria = RepositoryProtocol.Criteria.newBuilder()
                .setPredicateType(RepositoryProtocol.Criteria.PredicateType.AND);

        if (request.hasFilter()) {
            for (Request.Filter filter : request.getFilter()) {
                if (filter.isNull()) {
                    criteria.addExpression(buildNullExpression(filter));
                }
                else if (filter.hasValue()) {
                    criteria.addExpression(buildSingleValueExpression(filter));
                }
                else if (filter.hasValues()) {
                    criteria.addExpression(buildListExpression(filter));
                }
                else if (filter.hasRange()) {
                    criteria.addExpression(buildRangeExpression(filter));
                }
            }
        }

        return criteria.build();
    }

    /**
     * Builds a {@link com.rsa.netwitness.carlos.repository.RepositoryProtocol.Sorting} message from
     * the sorting properties provided in the {@link Request}.
     *
     * @param request the request to extract the sorting information from
     * @return the sorting Protobuf message
     */
    public static RepositoryProtocol.Sorting buildSorting(Request request) {
        RepositoryProtocol.Sorting.Builder builder = RepositoryProtocol.Sorting.newBuilder();

        if (request.hasSort()) {
            for (Request.Sort sort : request.getSort()) {
                RepositoryProtocol.SortOrder.Direction direction = sort.isDescending()
                        ? RepositoryProtocol.SortOrder.Direction.DESC : RepositoryProtocol.SortOrder.Direction.ASC;

                builder.addSortOrder(RepositoryProtocol.SortOrder.newBuilder()
                        .setPropertyName(sort.getField())
                        .setDirection(direction)
                        .build());
            }
        }

        return builder.build();
    }

    /**
     * Builds a {@link com.rsa.netwitness.carlos.repository.RepositoryProtocol.Pagination} message from
     * the page properties provided in the {@link Request}.
     *
     * @param request the request to extract the pagination information from
     * @return the pagination Protobuf message
     */
    public static RepositoryProtocol.Pagination buildPagination(Request request) {
        RepositoryProtocol.Pagination.Builder builder = RepositoryProtocol.Pagination.newBuilder();

        if (request.hasPage()) {
            Request.Page page = request.getPage();

            builder.setPageNumber(page.getIndex())
                    .setPageSize(page.getSize());
        }

        return builder.build();
    }

    private static RepositoryProtocol.Expression buildNullExpression(Request.Filter filter) {
        return RepositoryProtocol.Expression.newBuilder()
                .setPropertyName(filter.getField())
                .setCaseInsensitive(false)
                .setRestrictionType(RepositoryProtocol.Expression.RestrictionType.IS_NULL)
                .build();
    }

    private static RepositoryProtocol.Expression buildSingleValueExpression(Request.Filter filter) {
        Preconditions.checkArgument(filter.getValue() != null, "Invalid filter value");

        return RepositoryProtocol.Expression.newBuilder()
                .setPropertyName(filter.getField())
                .setCaseInsensitive(false)
                .setRestrictionType(RepositoryProtocol.Expression.RestrictionType.EQUAL)
                .addPropertyValue(buildPropertyValue(filter.getValue()))
                .build();
    }

    private static RepositoryProtocol.Expression buildListExpression(Request.Filter filter) {
        Preconditions.checkArgument(!filter.getValues().isEmpty(), "Invalid filter values");

        RepositoryProtocol.Expression.Builder builder = RepositoryProtocol.Expression.newBuilder()
                .setPropertyName(filter.getField())
                .setCaseInsensitive(false)
                .setRestrictionType(RepositoryProtocol.Expression.RestrictionType.IN);

        filter.getValues()
                .stream()
                .map(RepositoryProtocolUtils::buildPropertyValue)
                .forEach(builder::addPropertyValue);

        return builder.build();
    }

    private static RepositoryProtocol.Expression buildRangeExpression(Request.Filter filter) {
        RepositoryProtocol.Expression.Builder builder = RepositoryProtocol.Expression.newBuilder()
                .setPropertyName(filter.getField())
                .setCaseInsensitive(false);


        Request.Filter.Range range = filter.getRange();
        if (range.hasFrom() && range.hasTo()) {
            builder.setRestrictionType(RepositoryProtocol.Expression.RestrictionType.BETWEEN)
                    .addPropertyValue(RepositoryProtocol.PropertyValue.newBuilder()
                            .setDateValue(range.getFrom()))
                    .addPropertyValue(RepositoryProtocol.PropertyValue.newBuilder()
                            .setDateValue(range.getTo()));
        }
        else if (range.hasFrom()) {
            builder.setRestrictionType(RepositoryProtocol.Expression.RestrictionType.GREATER_THAN)
                    .addPropertyValue(RepositoryProtocol.PropertyValue.newBuilder()
                            .setDateValue(range.getFrom()));
        }
        else if (range.hasTo()) {
            builder.setRestrictionType(RepositoryProtocol.Expression.RestrictionType.LESS_THAN)
                    .addPropertyValue(RepositoryProtocol.PropertyValue.newBuilder()
                            .setDateValue(range.getTo()));
        }

        return builder.build();
    }

    private static RepositoryProtocol.PropertyValue buildPropertyValue(Object value) {
        RepositoryProtocol.PropertyValue.Builder builder = RepositoryProtocol.PropertyValue.newBuilder();

        if (value instanceof String) {
            builder.setStringValue((String) value);
        }
        else if (value instanceof Boolean) {
            builder.setBooleanValue((Boolean) value);
        }
        else if (value instanceof Integer) {
            builder.setIntValue((Integer) value);
        }
        else if (value instanceof Long) {
            builder.setLongValue((Long) value);
        }
        else if (value instanceof Float) {
            builder.setFloatValue((Float) value);
        }
        else if (value instanceof Double) {
            builder.setDoubleValue((Double) value);
        }
        else if (value instanceof Date) {
            builder.setDateValue(((Date) value).getTime());
        }
        else if (value instanceof URI) {
            builder.setUriValue(value.toString());
        }
        else if (value instanceof Enum) {
            builder.setEnumValue(RepositoryProtocol.EnumType.newBuilder()
                    .setEnumClass(value.getClass().getName())
                    .setEnumValue(value.toString()));
        }
        else {
            builder.setStringValue(value.toString());
        }

        return builder.build();
    }
}
