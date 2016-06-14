package com.rsa.asoc.sa.ui.common.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Encapsulates all the request information (pagination, sorting and filtering) from a web socket message.
 *
 * @author Abram Thielke
 * @since 10.6.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Request {
    private String id;
    private Page page;
    private Stream stream;
    private List<Sort> sort;
    private List<Filter> filter;

    public Request() {}

    private Request(Builder builder) {
        setId(builder.id);
        setPage(builder.page);
        setStream(builder.stream);
        setSort(builder.sort);
        setFilter(builder.filter);
    }

    public boolean hasId() {
        return !Strings.isNullOrEmpty(id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean hasPage() {
        return page != null;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public boolean hasStream() {
        return stream != null;
    }

    public Stream getStream() {
        return stream;
    }

    public void setStream(Stream stream) {
        this.stream = stream;
    }

    public boolean hasSort() {
        return sort != null && !sort.isEmpty();
    }

    public List<Sort> getSort() {
        return sort;
    }

    public void setSort(List<Sort> sort) {
        this.sort = sort;
    }

    public boolean hasFilter() {
        return filter != null && !filter.isEmpty();
    }

    public List<Filter> getFilter() {
        return filter;
    }

    public void setFilter(List<Filter> filter) {
        this.filter = filter;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("page", page)
                .add("stream", stream)
                .add("sort", sort)
                .add("filter", filter)
                .toString();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(Request request) {
        return new Builder(request);
    }

    /**
     * A builder for {@link Request} objects
     */
    public static final class Builder {
        private String id;
        private Page page;
        private Stream stream;
        private List<Sort> sort;
        private List<Filter> filter;

        private Builder() {}

        private Builder(Request request) {
            withId(request.id);

            if (request.hasPage()) {
                withPage(Page.newBuilder()
                        .withIndex(request.page.index)
                        .withSize(request.page.size));
            }

            if (request.hasStream()) {
                withStream(Stream.newBuilder()
                        .withLimit(request.stream.limit));
            }

            if (request.hasSort()) {
                request.getSort().forEach((sort) -> withSort(Sort.newBuilder()
                        .withField(sort.field)
                        .withDescending(sort.descending)));
            }

            if (request.hasFilter()) {
                request.getFilter().forEach((filter) -> withFilter(Filter.newBuilder()
                        .withField(filter.field)
                        .withNull(filter.isNull)
                        .withValues(filter.values)
                        .withValue(filter.value)
                        .withRange(Filter.Range.newBuilder()
                                .withFrom(filter.range.from)
                                .withTo(filter.range.to))));
            }
        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withPage(Page page) {
            this.page = page;
            return this;
        }

        public Builder withPage(Page.Builder builder) {
            return withPage(builder.build());
        }

        public Builder withStream(Stream stream) {
            this.stream = stream;
            return this;
        }

        public Builder withStream(Stream.Builder builder) {
            return withStream(builder.build());
        }

        public Builder withSort(Sort sort) {
            if (this.sort == null) {
                this.sort = new ArrayList<>();
            }
            this.sort.add(sort);
            return this;
        }

        public Builder withSort(Sort.Builder builder) {
            return withSort(builder.build());
        }

        public Builder withFilter(Filter filter) {
            if (this.filter == null) {
                this.filter = new ArrayList<>();
            }
            this.filter.add(filter);
            return this;
        }

        public Builder withFilter(Filter.Builder builder) {
            return withFilter(builder.build());
        }

        public Request build() {
            return new Request(this);
        }
    }
    
    
    /**
     * Encapsulates pagination data
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Page {
        private Integer index;
        private Integer size;

        public Page() {}

        private Page(Builder builder) {
            setIndex(builder.index);
            setSize(builder.size);
        }

        public boolean hasIndex() {
            return index != null;
        }

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }

        public boolean hasSize() {
            return size != null;
        }

        public Integer getSize() {
            return size;
        }

        public void setSize(Integer size) {
            this.size = size;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("index", index)
                    .add("size", size)
                    .toString();
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        /**
         * A builder for {@link Page} objects
         */
        public static final class Builder {
            private Integer index;
            private Integer size;

            private Builder() {}

            public Builder withIndex(Integer index) {
                this.index = index;
                return this;
            }

            public Builder withSize(Integer size) {
                this.size = size;
                return this;
            }

            public Page build() {
                return new Page(this);
            }
        }
    }

    /**
     * Encapsulates sorting information
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Sort {
        private String field;
        private boolean descending;

        public Sort() {}

        private Sort(Builder builder) {
            setField(builder.field);
            setDescending(builder.descending);
        }

        public boolean hasField() {
            return field != null;
        }

        public String getField() {
            return field;
        }

        public Sort setField(String field) {
            this.field = field;
            return this;
        }

        public boolean isDescending() {
            return descending;
        }

        public Sort setDescending(boolean descending) {
            this.descending = descending;
            return this;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("field", field)
                    .add("descending", descending)
                    .toString();
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        /**
         * A builder for {@link Sort} objects
         */
        public static final class Builder {
            private String field;
            private boolean descending;

            private Builder() {}

            public Builder withField(String field) {
                this.field = field;
                return this;
            }

            public Builder withDescending(boolean descending) {
                this.descending = descending;
                return this;
            }

            public Sort build() {
                return new Sort(this);
            }
        }
    }

    /**
     * Encapsulates filtering criteria
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Filter {
        private String field;
        private Object value;
        private List<Object> values;
        private Range range;
        private boolean isNull;

        public Filter() {}

        private Filter(Builder builder) {
            setField(builder.field);
            setValue(builder.value);
            setValues(builder.values);
            setRange(builder.range);
            setNull(builder.isNull);
        }

        public boolean hasField() {
            return field != null;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public boolean hasValue() {
            return value != null;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public boolean hasValues() {
            return values != null && !values.isEmpty();
        }

        public List<Object> getValues() {
            return values;
        }

        public void setValues(List<Object> values) {
            this.values = values;
        }

        public boolean hasRange() {
            return range != null;
        }

        public Range getRange() {
            return range;
        }

        public void setRange(Range range) {
            this.range = range;
        }

        @JsonProperty("isNull")
        public boolean isNull() {
            return isNull;
        }

        public void setNull(boolean isNull) {
            this.isNull = isNull;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("field", field)
                    .add("value", value)
                    .add("values", values)
                    .add("range", range)
                    .add("isNull", isNull)
                    .toString();
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        /**
         * A builder for {@link Filter} objects
         */
        public static final class Builder {
            private String field;
            private Object value;
            private List<Object> values;
            private Range range;
            private boolean isNull;

            private Builder() {}

            public Builder withField(String field) {
                this.field = field;
                return this;
            }

            public Builder withValue(Object value) {
                this.value = value;
                return this;
            }

            public Builder withValues(List<Object> values) {
                this.values = values;
                return this;
            }

            public Builder withValues(Object ... values) {
                this.values = Arrays.asList(values);
                return this;
            }

            public Builder withRange(Range range) {
                this.range = range;
                return this;
            }

            public Builder withRange(Range.Builder builder) {
                return withRange(builder.build());
            }

            public Builder withNull(boolean isNull) {
                this.isNull = isNull;
                return this;
            }

            public Filter build() {
                return new Filter(this);
            }
        }

        /**
         * Represents a date range
         */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Range {
            private Long from;
            private Long to;

            public Range() {}

            private Range(Builder builder) {
                setFrom(builder.from);
                setTo(builder.to);
            }

            public boolean hasFrom() {
                return from != null;
            }

            public Long getFrom() {
                return from;
            }

            public Range setFrom(Long from) {
                this.from = from;
                return this;
            }

            public boolean hasTo() {
                return to != null;
            }

            public Long getTo() {
                return to;
            }

            public Range setTo(Long to) {
                this.to = to;
                return this;
            }

            @Override
            public String toString() {
                return MoreObjects.toStringHelper(this)
                        .add("from", from)
                        .add("to", to)
                        .toString();
            }

            public static Builder newBuilder() {
                return new Builder();
            }

            /**
             * A builder for {@link Range} objects
             */
            public static final class Builder {
                private Long from;
                private Long to;

                private Builder() {}

                public Builder withFrom(Long from) {
                    this.from = from;
                    return this;
                }

                public Builder withTo(Long to) {
                    this.to = to;
                    return this;
                }

                public Range build() {
                    return new Range(this);
                }
            }
        }
    }

    /**
     * Encapsulates streaming data
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Stream {
        private Long limit;

        public Stream() {}

        private Stream(Builder builder) {
            setLimit(builder.limit);
        }

        public boolean hasLimit() {
            return limit != null;
        }

        public Long getLimit() {
            return limit;
        }

        public void setLimit(Long limit) {
            this.limit = limit;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("limit", limit)
                    .toString();
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        /**
         * A builder for {@link Page} objects
         */
        public static final class Builder {
            private Long limit;

            private Builder() {}

            public Builder withLimit(Long limit) {
                this.limit = limit;
                return this;
            }

            public Stream build() {
                return new Stream(this);
            }
        }
    }

}
