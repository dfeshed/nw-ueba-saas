/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flume.interceptor;

import org.apache.flume.interceptor.presidio.*;

public enum InterceptorType {

    TIMESTAMP(org.apache.flume.interceptor.TimestampInterceptor.Builder.class),
    HOST(org.apache.flume.interceptor.HostInterceptor.Builder.class),
    STATIC(org.apache.flume.interceptor.StaticInterceptor.Builder.class),
    REGEX_FILTER(
            org.apache.flume.interceptor.RegexFilteringInterceptor.Builder.class),
    REGEX_EXTRACTOR(org.apache.flume.interceptor.RegexExtractorInterceptor.Builder.class),
    SEARCH_REPLACE(org.apache.flume.interceptor.SearchAndReplaceInterceptor.Builder.class),
    JSON_RENAMER(JsonFieldRenamerInterceptor.Builder.class),
    JSON_DUPLICATOR(JsonFieldDuplicatorInterceptor.Builder.class),
    JSON_ALWAYS_FILTER(PresidioJsonAlwaysFilterInterceptorBuilder.class),
    JSON_FIELD_COMPARISON_FILTER(PresidioJsonFieldComparisonFilterInterceptorBuilder.class),
    JSON_FIELD_JOINER(JsonFieldJoinerInterceptor.Builder.class),
    JSON_FIELD_VALUE_REPLACER(JsonFieldValueReplacerInterceptor.Builder.class),
    JSON_MAP_CREATOR(JsonMapCreatorInterceptor.Builder.class),
    JSON_REGEX_PREDICATOR(JsonRegexPredicatorInterceptor.Builder.class),
    JSON_CASE(JsonCaseInterceptor.Builder.class),
    JSON_TIMESTAMP_WITH_OFFSET(JsonTimestampWithOffsetFormatterInterceptor.Builder.class),
    JSON_SEARCH_AND_REPLACE(JsonSearchAndReplaceInterceptor.Builder.class);

    private final Class<? extends Interceptor.Builder> builderClass;

    InterceptorType(Class<? extends Interceptor.Builder> builderClass) {
        this.builderClass = builderClass;
    }

    public Class<? extends Interceptor.Builder> getBuilderClass() {
        return builderClass;
    }

}
